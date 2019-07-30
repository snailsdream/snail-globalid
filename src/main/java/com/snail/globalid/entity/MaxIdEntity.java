package com.snail.globalid.entity;

/**
 * @author: fanchao
 * @Date: 2019/7/29 10:06
 * @Description:
 */
public class MaxIdEntity {
    private Long value;
    private String name;

    public MaxIdEntity(Long value) {
        this.value = value;
    }

    public MaxIdEntity(Long maxId, String sequenceName) {
        this.value = maxId;
        this.name = sequenceName;
    }

    public long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
