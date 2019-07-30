package com.snail.globalid.buffer;

import com.snail.globalid.mapper.SequenceMapper;
import com.snail.globalid.zookeeper.ZookeeperClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: fanchao
 * @Date: 2019/7/27 10:37
 * @Description:
 */
@Component
public class AtomicLongBufferRepository{
    private static final  String DEFAULT_SEQ_NAME = "DEFAULT_WHOLE_SEQUENCE_NAME";
    private final Map<String, AtomicLongBuffer> pool = new ConcurrentHashMap<>(16);

    @Autowired
    private SequenceMapper mapper;
    @Autowired
    RedisConnectionFactory connectionFactory;

    public long incrementAndGet(String sequenceName) throws Exception {
        Assert.hasLength(sequenceName,"序列名不能为空");
        return createSequenceIfNotExistAndGet(sequenceName).incrementAndGet();
    }
    public long incrementAndGet() throws Exception {
        return this.incrementAndGet(DEFAULT_SEQ_NAME);
    }
    @Autowired
    private ZookeeperClient zookeeperClient;
    /**
     *
     * @param sequenceName
     */
    private AtomicLongBuffer createSequenceIfNotExistAndGet (String sequenceName) {
        AtomicLongBuffer buffer = pool.get(sequenceName);
        if (buffer == null) {
            synchronized (pool) {
                if (pool.get(sequenceName) == null) {
                    buffer = new AtomicLongBuffer(sequenceName,
                            new RedisAtomicLongService(new RedisAtomicLong(sequenceName,connectionFactory)),
                            mapper,zookeeperClient
                            );
                    pool.put(sequenceName,buffer);
                }
            }
        }
        return pool.get(sequenceName);
    }

    public Map<String, AtomicLongBuffer> getPool() {
        return pool;
    }
}
