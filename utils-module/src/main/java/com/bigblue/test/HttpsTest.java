package com.bigblue.test;


import com.bigblue.utils.http.HttpsUtil;

import java.io.IOException;

/**
 * @Author: TheBigBlue
 * @Description:
 * @Date: 2019/7/30
 */
public class HttpsTest {


    public static void main(String[] args) {
        testHttpsUtil();
        testHttpClientUtil();
        testSelfHttpUtil();
    }

    private static void testHttpsUtil() {
        try {

            String retData = HttpsUtil.httpsPost("", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testHttpClientUtil() {

    }

    private static void testSelfHttpUtil() {

    }
}
