package io.choerodon.notify.websocket.redis;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisOperatorHelper {

    private RedisTemplate<Object, Object> redisTemplate;

    public RedisOperatorHelper(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


}
