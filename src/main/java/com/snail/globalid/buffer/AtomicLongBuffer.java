package com.snail.globalid.buffer;

import com.snail.globalid.entity.MaxIdEntity;
import com.snail.globalid.mapper.SequenceMapper;
import com.snail.globalid.zookeeper.ZookeeperClient;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author: fanchao
 * @Date: 2019/7/27 10:38
 * @Description: id缓存区，cacheA,cacheB
 */
public class AtomicLongBuffer {
    private final Object lock = new Object();
    private Logger logger = LoggerFactory.getLogger(AtomicLongBuffer.class);
    /**
     * 序列名称
     */
    private String sequenceName;

    protected AtomicLong[] buffer =new AtomicLong[]{new AtomicLong(0),new AtomicLong(0)};
    /**
     * 步长，每次从redis/zookeeper生成的id区间的长度
     */
    /**
     * id区间的最大值
     */
    protected long[] maxIds = new long[]{0,0};

    protected int[] steps = new int[]{10,10};

    //是否正在准备
    private AtomicBoolean isPerparing = new AtomicBoolean(false);
    //是否正在准备下一个缓冲区
    private AtomicBoolean[] isOks = new AtomicBoolean []{new AtomicBoolean(false),new AtomicBoolean(false)};

    //id生成器
    private GlobalAtomicLongService atomicLongService;
    //当前所在缓存区
    private int currentIndex=0;
    /**
     * 计数器，统计一个周期内的id需求量
     * 在高并发的情况下LongAdder优于AtomicLong，固采用LongAdder计数
     */
    private LongAdder count = new LongAdder();

    private SequenceMapper mapper;

    public AtomicLongBuffer(String sequenceName, GlobalAtomicLongService atomicLongService, SequenceMapper mapper, ZookeeperClient zookeeperClient) {
        Assert.hasLength(sequenceName,"序列名不能为空");
        this.sequenceName = sequenceName;
        this.atomicLongService=atomicLongService;
        this.mapper = mapper;
        this.zookeeperClient =zookeeperClient;
    }
    /**
     * Atomically increment by one the current value.
     *
     * @return the previous value.
     */
    public long incrementAndGet() throws Exception {
        long current;
        long next;
        int nextIndex;
        do {
            current = this.buffer[currentIndex].get();
            next = current + 1L;
            nextIndex = this.currentIndex^1;
            if (next > this.maxIds[currentIndex]) {
                synchronized (lock) {
                    if (!isOks[nextIndex].get() && nextIndex != this.currentIndex) {
                        /**
                         * 如果下一个缓存区没有在准备中
                         * 手动准备下一个缓存区，手动准备时说明在定时任务内id被提前消耗完，
                         * 在当前周期内的步长过小，此时将步长翻倍，避免频繁准备缓存区的值
                         */
                        logger.info("提前缓存，{}",nextIndex);
                        this.startPerpareProgress(nextIndex);
                    /*this.steps[nextIndex] = this.steps[currentIndex]<<1;
                    this.prepareNextBuffer(nextIndex);*/
                    }else  if (this.isOks[nextIndex].get()){
                        //如果下一个缓冲区准备好了，切换当前缓冲区
                        if (this.isOks[nextIndex].compareAndSet(true, false)){
                            logger.info("**********切换**************");
                            this.currentIndex = nextIndex;
                            this.isPerparing.set(false);
                        }
                    }
                }
            }
        } while( next >  this.maxIds[currentIndex] || !this.buffer[currentIndex].compareAndSet(current, next));
        //统计id生成情况
        count.increment();
        return next;
    }







    /**
     * 判断状态，是否进行一下次缓存
     */
    public void validateStatus() throws Exception {
        if (!isPerparing.get()) {
            //判断剩余id量是否还有10%,如果小于等于10%则进行下一次准备
            double productive = (this.maxIds[currentIndex] - this.buffer[currentIndex].doubleValue()) / this.steps[currentIndex];
            logger.info("缓存区：{}的剩余量为，{}",currentIndex,productive);
            if (productive <= 0.1 ){
                logger.info("***************提前缓存******************");
                startPerpareProgress(currentIndex^1);
            }
        }
    }


    private void startPerpareProgress(int index) throws Exception {
        //加入分布式锁，保证同一时间只能有一个进程从redis获取id
        logger.info("分布式锁：加锁");
        lockPrepareProgress();
        if (isPerparing.compareAndSet(false, true) && !this.isOks[index].get()) {
            logger.info("开始准备");
            //周期内统计数的两倍取为下一次的步长
            this.steps[index] =count.intValue() == 0 ? this.steps[index]<< 1 : count.intValue() << 1;
            //统计数清零
            count.reset();
            long max = atomicLongService.addAndGet(this.steps[index]);
            long start = max - this.steps[index];
            MaxIdEntity idEntity = getCurrentMaxId();
            if (idEntity.getValue() > start) {
                //数据库的值大于redis的值，将redis的值设为数据库的最大值+本次步长
                start = idEntity.getValue();
                max = start + this.steps[index];
                atomicLongService.set(max);
            }
            this.buffer[index].set(start);
            this.maxIds[index] = max;
            storeMaxSequence(max);
            this.isOks[index].set(true);
        }
        unLockPrepareProgress();
        logger.info("准备完成，分布式锁：释放");
    }
    private ZookeeperClient zookeeperClient;

    private InterProcessMutex interProcessMutex;
    private void lockPrepareProgress() throws Exception {
        if (interProcessMutex == null) {
            String distributedLockName = "/globalIdLock_" + sequenceName;
            interProcessMutex = new InterProcessMutex(zookeeperClient.getConnect(), distributedLockName);
        }
        interProcessMutex.acquire(10, TimeUnit.SECONDS);
    }
    private void unLockPrepareProgress() throws Exception {
        interProcessMutex.release();
    }
    private MaxIdEntity getCurrentMaxId(){
        return mapper.queryMaxId(sequenceName);
    }
    private void storeMaxSequence(long max) {
        MaxIdEntity idEntity = new MaxIdEntity(max,sequenceName);
        int i = mapper.updateSequenceInfo(idEntity);
        if ( i == 0 ) {
            mapper.insertSequenceInfo(idEntity);
        }
    }
}
