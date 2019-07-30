package com.snail.globalid.buffer;

import org.springframework.data.redis.support.atomic.RedisAtomicLong;

/**
 * @author: fanchao
 * @Date: 2019/7/27 14:55
 * @Description:
 */
public class RedisAtomicLongService implements GlobalAtomicLongService {

    private RedisAtomicLong atomicLong;

    public RedisAtomicLongService(RedisAtomicLong atomicLong) {
        this.atomicLong = atomicLong;
    }
    @Override
    public long addAndGet(long addValue) {
        return atomicLong.addAndGet(addValue);
    }

    @Override
    public void set(long newValue) {
        atomicLong.set(newValue);
    }
}
