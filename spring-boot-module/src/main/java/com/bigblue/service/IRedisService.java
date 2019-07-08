package com.bigblue.service;


import com.bigblue.enums.CommonEnum;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/***
 * @ClassName(类名) : IRedisService
 * @Description(描述) : redis 操作接口
 * @author(作者) ：吴桂镇
 * @date (开发日期)      ：2018年5月15日 上午9:28:49
 */
public interface IRedisService {
    /***
     * @param key
     * @param value
     * @return boolean
     * @throws :
     * @Description(功能描述) :  写入缓存
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年5月15日 上午9:29:14
     */
    public boolean set(final String key, Object value);

    /***
     * @param key
     * @param value
     * @param expireTime
     * @return boolean
     * @throws :
     * @Description(功能描述) :  写入缓存设置时效时间
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年5月15日 上午9:29:44
     */
    public boolean set(final String key, Object value, Long expireTime);

    /***
     * @param k
     * @param v void
     * @throws :
     * @Description(功能描述) :  列表添加
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年5月15日 上午9:32:50
     */
    public void listPush(String k, Object v);

    /***
     * @param key
     * @param value void
     * @throws :
     * @Description(功能描述) :  集合添加
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年5月15日 上午9:34:03
     */
    public void add(String key, Object value);

    /**
     * @param key
     * @return Set<Object>
     * @throws :
     * @Description(功能描述) :  集合获取
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年5月15日 上午9:34:32
     */
    public Set<Object> setMembers(String key);

    /***
     * @param key
     * @param value
     * @param scoure void
     * @throws :
     * @Description(功能描述) :  有序集合添加
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年5月15日 上午9:35:56
     */
    public void zAdd(String key, Object value, double scoure);

    /***
     * @param key
     * @param scoure
     * @param scoure1
     * @return Set<Object>
     * @throws :
     * @Description(功能描述) :  有序集合获取
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年5月15日 上午9:36:13
     */
    public Set<Object> rangeByScore(String key, double scoure, double scoure1);


    /***
     * @param pattern void
     * @throws :
     * @Description(功能描述) :  批量刪除
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年5月15日 上午11:16:18
     */
    public void removePattern(final String pattern);

    /***
     * @param k
     * @param l
     * @param l1
     * @return List<Object>
     * @throws :
     * @Description(功能描述) :  列表获取
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年5月15日 上午11:19:56
     */
    public List<Object> lRange(String k, long l, long l1);

    /**
     * @param keys void
     * @throws :
     * @Description(功能描述) :  批量删除对应的value
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年5月15日 上午9:30:08
     */
    public void remove(final String... keys);

    /***
     * @param key void
     * @throws :
     * @Description(功能描述) :  删除对应的value
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年5月15日 上午9:30:28
     */
    public void remove(final String key);

    /***
     * @param key
     * @return boolean
     * @throws :
     * @Description(功能描述) :  判断缓存中是否有对应的value
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年5月15日 上午9:30:49
     */
    public boolean exists(final String key);

    /***
     * @param key
     * @return Object
     * @throws :
     * @Description(功能描述) :  读取缓存
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年5月15日 上午9:31:16
     */
    public Object get(final String key);

    /**
     * @param key
     * @param hashKey
     * @param value   void
     * @throws :
     * @Description(功能描述) :  哈希 添加
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年5月15日 上午9:32:16
     */
    public void hmSet(String key, Object hashKey, Object value);
    /**
     * @author ：YanZiheng：
     * @Description ：哈希添加，自主设置过期时间
     * @date ：2018-7-3
     */
    public void hmSet(String key, Object hashKey, Object value, CommonEnum commonEnum);
    /**
     * @author ：YanZiheng：
     * @Description ：哈希添加，自主设置过期时间
     * @date ：2018-7-3
     */
    public void hmSet(String key, Object hashKey, Object value, long expireTime);

    /**
     * @author ：YanZiheng
     * @Description ：哈希添加，自主设置过期时间及时间单位
     * @date ：
     */
    public void hmSet(String key, Object hashKey, Object value, CommonEnum expireTime, TimeUnit timeUnit);

    /**
     * @author ：YanZiheng
     * @Description ：哈希添加，自主设置过期时间及时间单位
     * @date ：
     */
    public void hmSet(String key, Object hashKey, Object value, long expireTime, TimeUnit timeUnit);

    /***
     * @param key
     * @param hashKey
     * @return Object
     * @throws :
     * @Description(功能描述) :  哈希获取数据
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年5月15日 上午9:32:31
     */
    public Object hmGet(String key, Object hashKey);

    /***
     * @param key
     * @return Set<String>
     * @throws :
     * @Description(功能描述) :  获取hash 的所有key
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年5月24日 下午3:03:13
     */
    public Set<Object> hmKeySet(String key);


    /***
     * @param mainKey
     * @param map     void
     * @throws :
     * @Description(功能描述) :  批量添加
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年6月6日 上午9:33:36
     */
    public void hmPutAll(String mainKey, Map<String, Object> map);
    /**
     * @return ：
     * @author ：YanZiheng
     * @Description ：哈希批量添加,自主设置过期时间, 单位默认为秒
     * @date ：2018-7-3
     */
    public void hmPutAll(String mainKey, Map<String, Object> map, CommonEnum commonEnum);

    /**
     * @return ：
     * @author ：YanZiheng
     * @Description ：哈希批量添加,自主设置过期时间, 单位默认为秒
     * @date ：2018-7-3
     */
    public void hmPutAll(String mainKey, Map<String, Object> map, long expireTime);

    /**
     * @author ：YanZiheng：
     * @Description ：哈希批量添加,自主设置过期时间,及时间单位
     * @date ：2018-7-3
     */

    public void hmPutAll(String mainKey, Map<String, Object> map, CommonEnum expireTime, TimeUnit timeUnit);


    /**
     * @author ：YanZiheng：
     * @Description ：哈希批量添加,自主设置过期时间,及时间单位
     * @date ：2018-7-3
     */
    public void hmPutAll(String mainKey, Map<String, Object> map, long expireTime, TimeUnit timeUnit);



    /***
     * @param mainKey
     * @return List<Map<String,Object>>
     * @throws :
     * @Description(功能描述) :  获取hash 数据类型的 全部数据
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年6月6日 上午9:36:25
     */
    public Map<String, Object> hmGetAll(String mainKey);

    /**
     * @return ：
     * @author ：YanZiheng
     * @Description ：哈希批量精准删除（慎用!!!!）
     * @date ：
     */

    public void hmdelete(String mainKey, String... field);
    
    /***
     * 
     * @Description(功能描述)    :  查询hash中是否包含某个key
     * @author(作者)             ：  吴桂镇
     * @date (开发日期)          :  2018年7月4日 上午10:55:51 
     * @exception                : 
     * @param mainKey
     * @param key
     * @return  boolean
     */
    public boolean hmHasKey(String mainKey, String key);

}
