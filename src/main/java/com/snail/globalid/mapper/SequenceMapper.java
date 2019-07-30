package com.snail.globalid.mapper;

import com.snail.globalid.entity.MaxIdEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author: fanchao
 * @Date: 2019/7/27 16:33
 * @Description:
 */
public interface SequenceMapper{
    @Insert("INSERT INTO sequenceinfo" +
            "(name," +
            "value" +
            ")" +
            " VALUES (" +
            "#{name}," +
            "#{value}" +
            ")")
    void insertSequenceInfo(MaxIdEntity entity);

    @Update("UPDATE sequenceinfo SET value =#{value}  where name=#{name} and value < #{value}")
    int updateSequenceInfo(MaxIdEntity entity);

    @Select("SELECT value FROM sequenceinfo WHERE name = #{name}")
    MaxIdEntity queryMaxId(@Param("name") String name);
}
