package io.choerodon.notify.infra.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.choerodon.notify.websocket.relationship.DefaultRelationshipDefining;

@Service
public class HourlyStorageUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HourlyStorageUtils.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 每小时redis记录当前在线人数
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void HourlyStorageSchedule() {
        Integer onlineCount = DefaultRelationshipDefining.getOnlineCount();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
        String date = dateFormat.format(new Date());
        redisTemplate.opsForValue().set(date, onlineCount.toString(), 24, TimeUnit.HOURS);
    }

    /**
     * 每到午夜清空今日访问人数
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearNumberOfVisitorsTodaySchedule() {
        DefaultRelationshipDefining.clearNumberOfVisitorsToday();
    }
}
