package com.merchen.gulimall.ware.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author MrChen
 * @create 2022-06-28 19:55
 */
@Configuration
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 新增时对创建时间和修改时间进行自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        Object created = getFieldValByName("createTime", metaObject);
        if (null==created){
            setFieldValByName("createTime",new Date(),metaObject);
        }
        Object updated = getFieldValByName("updateTime", metaObject);
        if(null==updated){
            setFieldValByName("updateTime",new Date(),metaObject);
        }
    }

    /**
     * 修改时对修改时间自动填充
     * @param metaObject
     */

    @Override
    public void updateFill(MetaObject metaObject) {
        Object updated = getFieldValByName("updateTime", metaObject);
        setFieldValByName("updateTime",new Date(),metaObject);
    }
}