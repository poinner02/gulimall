package com.merchen.common.constant;

/**
 * @author MrChen
 * @create 2022-06-19 23:02
 */
public class ProductConstant {


    public enum Status{
        NEW_SPU(0,"商品新建"),SPU_UP(1,"商品上架"),SPU_DOWN(2,"商品下架");
        private int code;
        private String message;

        Status(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    public enum AttrEnum{
        ATTR_TYPE_BAES(1,"基本属性"),ATTR_TYPE_SALE(0,"销售属性");
        private int code;
        private String message;

        AttrEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }



}
