package com.bigblue.impl;

import com.bigblue.enums.CommonEnum;
import com.bigblue.service.IRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisService implements IRedisService {
    @Autowired
    private RedisTemplate redisTemplate;

    private long expireTime = 72000;

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存设置时效时间
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 列表添加
     *
     * @param k
     * @param v
     */
    @Override
    public void listPush(String k, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPush(k, v);
    }

    /**
     * 列表获取
     *
     * @param k
     * @param l
     * @param l1
     * @return
     */
    @Override
    public List<Object> lRange(String k, long l, long l1) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(k, l, l1);
    }

    /**
     * 集合添加
     *
     * @param key
     * @param value
     */
    @Override
    public void add(String key, Object value) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key, value);
    }

    /**
     * 集合获取
     *
     * @param key
     * @return
     */
    @Override
    public Set<Object> setMembers(String key) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 有序集合添加
     *
     * @param key
     * @param value
     * @param scoure
     */
    @Override
    public void zAdd(String key, Object value, double scoure) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add(key, value, scoure);
    }

    /**
     * 有序集合获取
     *
     * @param key
     * @param scoure
     * @param scoure1
     * @return
     */
    @Override
    public Set<Object> rangeByScore(String key, double scoure, double scoure1) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, scoure, scoure1);
    }

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    @Override
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     *
     * @param pattern
     */
    @Override
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 删除对应的value
     *
     * @param key
     */
    @Override
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    @Override
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    @Override
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    /**
     * 哈希 添加
     *
     * @param key
     * @param hashKey
     * @param value
     */
    @Override
    public void hmSet(String key, Object hashKey, Object value) {
        //若没有传进时间相关的参数，则过期时间为如下。默认单位为秒，时间从配置文件中读取
        hmSet(key, hashKey, value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 哈希添加 ，还需传入过期时间，单位为秒
     */

    @Override
    public void hmSet(String key, Object hashKey, Object value, CommonEnum expireTime) {
        hmSet(key, hashKey, value,expireTime, TimeUnit.SECONDS);
    }

    /**
     * 哈希添加 ，还需传入过期时间，单位为秒
     */

    @Override
    public void hmSet(String key, Object hashKey, Object value, long expireTime) {
        hmSet(key, hashKey, value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 哈希添加 ，还需传入过期时间及单位
     */
    @Override
    public void hmSet(String key, Object hashKey, Object value, CommonEnum expireTime, TimeUnit timeUnit) {
        hmSet(key, hashKey, value,  Long.parseLong(expireTime.getCode()), timeUnit);
    }

    /**
     * 哈希添加 ，还需传入过期时间及单位
     */
    @Override
    public void hmSet(String key, Object hashKey, Object value, long expireTime, TimeUnit timeUnit) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
        redisTemplate.expire(key, expireTime, timeUnit);
    }

    /**
     * 哈希获取数据
     *
     * @param key
     * @param hashKey
     * @return
     */
    @Override
    public Object hmGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    /***
     * hash 获取所有key
     */
    @Override
    public Set<Object> hmKeySet(String key) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.keys(key);
    }


    /***
     * @param mainKey
     * @param map     void
     * @throws :
     * @Description(功能描述) :  批量添加
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年6月6日 上午9:33:36
     */
    @Override
    public void hmPutAll(String mainKey, Map<String, Object> map) {
        hmPutAll(mainKey, map, expireTime);
    }

    /**
     * 哈希批量添加 ，还需传入过期时间
     */
    @Override
    public void hmPutAll(String mainKey, Map<String, Object> map,CommonEnum expireTime) {
        hmPutAll(mainKey, map, expireTime, TimeUnit.SECONDS);
    }
    /**
     * 哈希批量添加 ，还需传入过期时间
     */
    @Override
    public void hmPutAll(String mainKey, Map<String, Object> map, long expireTime) {
        hmPutAll(mainKey, map, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 哈希批量添加 ，还需传入过期时间及单位
     */
    @Override
    public void hmPutAll(String mainKey, Map<String, Object> map, CommonEnum expireTime, TimeUnit timeUnit) {
        BoundHashOperations<String, String, Object> boundHashOperations = redisTemplate.boundHashOps(mainKey);
        boundHashOperations.putAll(map);
        redisTemplate.expire(mainKey, Long.parseLong(expireTime.getCode()), timeUnit);
    }

    /**
     * 哈希批量添加 ，还需传入过期时间及单位
     */
    @Override
    public void hmPutAll(String mainKey, Map<String, Object> map, long expireTime, TimeUnit timeUnit) {
        BoundHashOperations<String, String, Object> boundHashOperations = redisTemplate.boundHashOps(mainKey);
        boundHashOperations.putAll(map);
        redisTemplate.expire(mainKey, expireTime, timeUnit);
    }

    /***
     * @param mainKey
     * @return List<Map<String,Object>>
     * @throws :
     * @Description(功能描述) :  获取hash 数据类型的 全部数据
     * @author(作者) ：  吴桂镇
     * @date (开发日期)          :  2018年6月6日 上午9:36:25
     */
    @Override
    public Map<String, Object> hmGetAll(String mainKey) {
        BoundHashOperations<String, String, Object> boundHashOperations = redisTemplate.boundHashOps(mainKey);
        return boundHashOperations.entries();
    }


    /**
     * @return ：
     * @author ：YanZiheng
     * @Description ：哈希批量精准删除（慎用!!!!）
     * @date ：
     */
    @Override
    public void hmdelete(String mainKey, String... field) {
        BoundHashOperations<String, String, ?> boundHashOperations = redisTemplate.boundHashOps(mainKey);
        boundHashOperations.delete(field);
    }
	@Override
	public boolean hmHasKey(String mainKey, String key) {
		 BoundHashOperations<String, String, ?> boundHashOperations = redisTemplate.boundHashOps(mainKey);
		return boundHashOperations.hasKey(key);
	}

}