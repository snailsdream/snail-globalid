package com.snail.globalid.popertity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: fanchao
 * @Date: 2019/7/29 16:00
 * @Description:
 */

@ConfigurationProperties(
        prefix = "zk"
)
@Component
public class ZookeeperProperty {
    private String url = "127.0.0.1:2181";
    private int sessionTimeoutMs = 4000;
    private int baseSleepTimeMs = 1000;
    private int maxRetries = 3;
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }

    public void setBaseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
}
