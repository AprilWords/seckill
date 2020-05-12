package com.learn.miaosha.result;

public class CodeMsg {
    private int code;
    private  String msg;
    //通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0,"success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100,"服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101,"参数校验异常:%s");
    public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500103,"请求非法");
    public static CodeMsg REQUEST_LIMIT = new CodeMsg(500104,"访问非法");
    private CodeMsg(int code, String msg) {
        this.code=code;
        this.msg=msg;
    }

    //登陆模块5002xx
    public static CodeMsg SESSION_EMPTY = new CodeMsg(500210,"Session不存在或已失效");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211,"登陆密码不能为空");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500212,"登陆密码错误");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500213,"手机号不能为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500214,"手机号格式错误");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500215,"手机号不存在");

    //商品模块5003xx
    //订单模块5004xx
    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500215,"订单不存在");
    //秒杀模块5005xx
    public static CodeMsg MIAOSHA_OVER = new CodeMsg(5005001,"商品已经秒杀完毕");
    public static CodeMsg REPEATIVE_MIAOSHA = new CodeMsg(5005002,"商品已经秒杀过了");


    public int getCode() {
        return code;
    }



    public String getMsg() {
        return msg;
    }

    public CodeMsg fillArgs(Object...args){
        int code =this.code;
        String message =String.format(this.msg,args);
        return new CodeMsg(code,msg);
    }


}
