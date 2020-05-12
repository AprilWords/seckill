package com.learn.miaosha.util;

import org.apache.commons.lang3.StringUtils;

import javax.validation.Validation;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {
    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");
    public static boolean isMobile(String src){
        if(StringUtils.isEmpty(src)){
            return false;
        }
        Matcher m = mobile_pattern.matcher(src);
        return m.matches();
    }
    public static void main(String []args){

        System.out.println(ValidatorUtil.isMobile("1234568"));
        System.out.println(ValidatorUtil.isMobile("18392004938"));
    }
}
