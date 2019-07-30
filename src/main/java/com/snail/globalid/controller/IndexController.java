package com.snail.globalid.controller;

import com.snail.globalid.buffer.AtomicLongBufferRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * @author: fanchao
 * @Date: 2019/7/26 15:26
 * @Description:
 */
@RestController
@RequestMapping("index")
public class IndexController{
    @Resource
    AtomicLongBufferRepository repository;
    @RequestMapping("id")
    @ResponseBody
    public long getId(@RequestParam("seq_name") String seqName) throws Exception {
        return repository.incrementAndGet(seqName);
    }

    @ResponseBody
    @RequestMapping("moreId")
    public List moreId(@RequestParam("seq_name") String e) throws BrokenBarrierException, InterruptedException {
        List list =new ArrayList();
        int i=5;
        final CountDownLatch countdown = new CountDownLatch(1);
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(i+1);
        do {
            new Thread(() -> {
                long id ;
                try {
                    countdown.await();
                    Map param = new HashMap();
                    id=repository.incrementAndGet(e);
                    param.put("id",id);
                    param.put("key",e);
                    list.add(param);
                    System.out.println(param);
                    cyclicBarrier.await();
                } catch (Exception er) {
                    er.printStackTrace();
                    Map param = new HashMap();
                    param.put("msg",er.getLocalizedMessage());
                    param.put("key",e);
                    System.out.println(param);
                    list.add(param);
                }
            }).start();
            i--;
        }while (i>0);
        countdown.countDown();
        cyclicBarrier.await();
        System.out.println(list);
        System.out.println("*******************************");
        return list;
    }
}
