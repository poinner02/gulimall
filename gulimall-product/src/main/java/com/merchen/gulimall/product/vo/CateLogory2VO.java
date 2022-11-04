package com.merchen.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author MrChen
 * @create 2022-07-17 13:29
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CateLogory2VO {
    private String catelog1Id;
    private List<catalog3VO> catalog3List;
    private String id;
    private String name;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class catalog3VO{
        private String catalog2Id;
        private String id;
        private String name;
    }
}
