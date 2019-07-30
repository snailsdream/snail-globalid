package com.snail.globalid.buffer;

/**
 * @author: fanchao
 * @Date: 2019/7/27 14:53
 * @Description:
 */
public interface GlobalAtomicLongService {
    long addAndGet(long addValue);
    void set(long newValue);
}
