package org.hzero.websocket.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.listener.PatternTopic;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2021/05/26 14:43
 */
public class TopicRegistry {

    /**
     * 内存存储topic
     */
    private static final Map<String, PatternTopic> SESSION_MAP = new ConcurrentHashMap<>();

    public static void addTopic(String channel, PatternTopic topic) {
        SESSION_MAP.put(channel, topic);
    }

    public static PatternTopic getTopic(String channel) {
        return SESSION_MAP.get(channel);
    }

    public static void removeTopic(String channel) {
        SESSION_MAP.remove(channel);
    }
}
