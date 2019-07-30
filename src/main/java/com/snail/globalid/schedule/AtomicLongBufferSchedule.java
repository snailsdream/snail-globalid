package com.snail.globalid.schedule;

import com.snail.globalid.buffer.AtomicLongBuffer;
import com.snail.globalid.buffer.AtomicLongBufferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author: fanchao
 * @Date: 2019/7/27 13:39
 * @Description:
 */
@Component
public class  AtomicLongBufferSchedule {

    private Logger logger= LoggerFactory.getLogger(AtomicLongBufferSchedule.class);
    @Resource
    AtomicLongBufferRepository repository;
    @Scheduled(cron = "0/10 * * * * *")
    public void perpareId() throws Exception {
        Map<String, AtomicLongBuffer> atomicLongBufferMap = repository.getPool();
        for (Map.Entry<String, AtomicLongBuffer> entry : atomicLongBufferMap.entrySet()) {
            logger.info("开始检验{}序列的使用情况",entry.getKey());
            entry.getValue().validateStatus();
        }
    }
}
