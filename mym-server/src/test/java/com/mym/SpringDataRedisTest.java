package com.mym;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author mingbb
 * @create 2023-09-23-21:16
 * 测试Spring Data Redis
 */
@SuppressWarnings("all")
//@SpringBootTest
public class SpringDataRedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testRedisTemplate(){
        System.out.println(redisTemplate);
        ValueOperations valueOperations = redisTemplate.opsForValue();  //String
        HashOperations hashOperations = redisTemplate.opsForHash();     //hash
        ListOperations listOperations = redisTemplate.opsForList();     //list
        SetOperations setOperations = redisTemplate.opsForSet();        //set
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();     //zset
    }

    /**
     * Redis测试String类型
     */
    @Test
    public void testString(){
        //set-set       get-get     setex-set   setnx-setIfAbsent
        redisTemplate.opsForValue().set("name","mym");

        String name = (String) redisTemplate.opsForValue().get("name");
        System.out.println(name);

        redisTemplate.opsForValue().set("age","22",3, TimeUnit.MINUTES);

        redisTemplate.opsForValue().setIfAbsent("gender","1");
        redisTemplate.opsForValue().setIfAbsent("gender","2");
    }


    /**
     * Redis测试Hash类型
     */
    @Test
    public void testHash(){
        //hset-put   hget-get    hkeys-keys   hvals-values    hdel-delete
        HashOperations hashOperations = redisTemplate.opsForHash();

        hashOperations.put("hash类型","name","mym");
        hashOperations.put("hash类型","age","22");

        String o = (String) hashOperations.get("hash类型", "name");
        System.out.println(o);

        Set set = hashOperations.keys("hash类型");
        System.out.println(set);

        List list = hashOperations.values("hash类型");
        System.out.println(list);

        hashOperations.delete("hash类型","name","age");
    }


    /**
     * Redis测试List类型
     */
    @Test
    public void testList(){
        //lpush-leftPushAll,leftPush   lrange-range    rpop-rightPop    llen-size
        ListOperations listOperations = redisTemplate.opsForList();

        listOperations.leftPushAll("left类型","a","b","c");
        listOperations.leftPush("left类型","d");

        List list1 = listOperations.range("left类型", 0, -1);
        System.out.println(list1);

        listOperations.rightPop("left类型");

        Long size = listOperations.size("left类型");
        System.out.println(size);
    }


    /**
     * Redis测试Set类型
     */
    @Test
    public void testSet(){
        //sadd-add   smembers-members    scard-size   sinter-intersect  sunion-union  srem-remove
        SetOperations setOperations = redisTemplate.opsForSet();

        setOperations.add("set类型1","a","b","c");
        setOperations.add("set类型2","1","2","3","4");

        Set set = setOperations.members("set类型1");
        System.out.println(set);

        Long size = setOperations.size("set类型1");
        System.out.println(size);

        Set intersect = setOperations.intersect("set类型1", "set类型2");
        System.out.println(intersect);

        Set union = setOperations.union("set类型1", "set类型2");
        System.out.println(union);

        setOperations.remove("set类型2","2","3");
    }


    /**
     * Redis测试Zset类型
     */
    @Test
    public void testZset(){
        //zadd-add    zrange-range   zincrby-incrementScore    zrem-remove
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();

        zSetOperations.add("zset类型","hahah",10);
        zSetOperations.add("zset类型","wuwuw",9);
        zSetOperations.add("zset类型","xixix",11);

        Set zset = zSetOperations.range("zset类型", 0, -1);
        System.out.println(zset);

        zSetOperations.incrementScore("zset类型","wuwuw",1.5);

        zSetOperations.remove("zset类型","xixix");
    }


    /**
     * Redis测试Common类型
     */
    @Test
    public void testCommon(){
        //keys-keys   exists-hasKey   type-type    del
        Set keys = redisTemplate.keys("*");
        System.out.println(keys);

        Boolean name = redisTemplate.hasKey("name");
        System.out.println(name);
        Boolean eman = redisTemplate.hasKey("eman");
        System.out.println(eman);

        DataType dataType = redisTemplate.type("zset类型");
        System.out.println(dataType.name());

        redisTemplate.delete("gender");
    }
}
