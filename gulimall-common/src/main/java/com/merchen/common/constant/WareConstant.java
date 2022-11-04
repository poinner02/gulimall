package com.merchen.common.constant;

/**
 * @author MrChen
 * @create 2022-06-28 19:15
 */
public class WareConstant {

    public  enum PurChaseStatusEnum{
        CREATED(0,"已经新建"),ASSIGN(1,"已经分配"),
        RECEIVE(2,"已经领取"),FINISH(3,"已经完成"),
        HASERROR(4,"有异常");
        private int code;
        private String msg;

        PurChaseStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
    public  enum PurChaseDetailStatusEnum{
        /**
         * 新建
         * 已分配
         * 正在采购
         * 已完成
         * 采购失败
         */
        CREATED(0,"新建"),ASSIGN(1,"已分配"),
        DOING(2,"正在采购"),FINISH(3,"已完成"),
        DONEFAILED(4,"采购失败");
        private int code;
        private String msg;

        PurChaseDetailStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

}
