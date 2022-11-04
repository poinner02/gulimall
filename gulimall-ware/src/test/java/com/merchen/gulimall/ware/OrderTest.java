package com.merchen.gulimall.ware;


import com.merchen.gulimall.ware.dao.WareSkuDao;
import com.merchen.gulimall.ware.entity.WareSkuEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author MrChen
 * @create 2022-08-29 20:28
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderTest {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Test
    @Transactional
    public void test(){
        for (int i = 0; i < 5; i++){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            Integer index = i+1;
            wareSkuEntity.setId(Long.parseLong(index.toString()));
            wareSkuEntity.setStock(10);
            wareSkuDao.updateById(wareSkuEntity);
            if(index==5){
//                throw  new RuntimeException();
            }
        }

    }
}
