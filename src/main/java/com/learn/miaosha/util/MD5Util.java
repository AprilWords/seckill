package com.learn.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    private  static final String salt = "2d34dcc0";
    /*
    * 对明文字符串做md5
    * */
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    /*
     * form密码转换成数据库存储数据
     * */
    public static String formPassDBPass(String inputPass,String salt){
        String src =  salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return  md5(src);
    }
    /*
     * 用户输入转换成form密码
     * */
    public static String inputPassFormPass(String inputPass){
        String src =  salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return  md5(src);
    }

    /*
     * 用户输入转换成DB密码
     * */
    public static String inputPassDBPass(String inputPass,String DBsalt){
        String formPass =  inputPassFormPass(inputPass);
        String DBPass = formPassDBPass(formPass,DBsalt);
        return  DBPass;
    }
    public static void main(String []args){

        System.out.println(inputPassDBPass("123456","2d34dcc0"));
    }
}
