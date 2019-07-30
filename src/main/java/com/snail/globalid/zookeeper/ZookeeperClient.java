package com.snail.globalid.zookeeper;

import com.snail.globalid.popertity.ZookeeperProperty;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: fanchao
 * @Date: 2019/7/29 16:16
 * @Description:
 */
@Component
public class ZookeeperClient {
    @Autowired
    private ZookeeperProperty property;


    private CuratorFramework curatorFramework;

    public synchronized CuratorFramework getConnect(){
        if (curatorFramework == null) {
            String namespace = "globalId";
            curatorFramework = CuratorFrameworkFactory.builder().connectString(property.getUrl())
                    .sessionTimeoutMs(4000)
                    .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                    .namespace(namespace).build();
            curatorFramework.start();
        }
        return curatorFramework;
    }
}
