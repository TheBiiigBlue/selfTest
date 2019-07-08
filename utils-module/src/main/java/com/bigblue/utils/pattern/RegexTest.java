package com.bigblue.utils.pattern;

import java.util.regex.Pattern;

/**
 * Created By TheBigBlue on 2018/8/21 : 14:48
 * Description :
 */
public class RegexTest {

    private static Pattern pattern = Pattern.compile("^[0-9]*[1-9][0-9]*$");    //验证正整数

    public static void main(String[] args) {
        Integer integer = testRegex("123");
        Integer integer1 = testRegex(" - ");
        Integer integer2 = testRegex("  ");
        System.out.println(integer + "\t" + integer1 + "\t" + integer2);
    }

    private static Integer testRegex(String str){
        boolean flag = pattern.matcher(str).matches();
        if(flag){
            //是数字
            return Integer.parseInt(str);
        }else {
            //不是数字
            return null;
        }
    }
}
