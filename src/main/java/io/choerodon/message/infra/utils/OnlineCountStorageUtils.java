package io.choerodon.message.infra.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OnlineCountStorageUtils {
    private static final String ONLINE_COUNT = "OnlineCount";
    private static final String NUMBER_OF_VISITORS_TODAY = "NumberOfVisitorsToday";
    private static final Logger LOGGER = LoggerFactory.getLogger(OnlineCountStorageUtils.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 每小时redis记录当前在线人数
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void hourlyStorageSchedule() {
        int onlineCount = Optional.ofNullable(redisTemplate.keys(ONLINE_COUNT + "*")).orElse(Collections.emptySet()).size();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
        String date = dateFormat.format(new Date());
        redisTemplate.opsForValue().set(date, String.valueOf(onlineCount), 24, TimeUnit.HOURS);
        LOGGER.info("Record the number of people online at {}", date);
    }

    /**
     * 每日零点清空今日访问人数
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearNumberOfVisitorsTodaySchedule() {
        redisTemplate.delete(NUMBER_OF_VISITORS_TODAY);
        LOGGER.info("Clear the number of visitors");
    }

    /**
     * 每天凌晨0点清空在线人数
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void clearOnlineCountSchedule() {
        redisTemplate.delete(Optional.ofNullable(redisTemplate.keys(ONLINE_COUNT + "*")).orElse(Collections.emptySet()));
        LOGGER.info("Clear the number of onliners");
    }

    public Map<String, Object> makeVisitorsInfo() {
        Map<String, Object> visitorsInfo = new HashMap<>();
        visitorsInfo.put("CurrentOnliners", getOnlineCount());
        visitorsInfo.put("numberOfVisitorsToday", getNumberOfVisitorsToday());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        List<String> times = new ArrayList<>();
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            String time = dateFormat.format(calendar.getTime());
            times.add(time);
            String onlinersOnThatTime = redisTemplate.opsForValue().get(time);
            if (onlinersOnThatTime == null) {
                onlinersOnThatTime = "0";
            }
            data.add(onlinersOnThatTime);
            calendar.add(Calendar.HOUR, -1);
        }
        Collections.reverse(times);
        Collections.reverse(data);
        visitorsInfo.put("time", times);
        visitorsInfo.put("data", data);
        return visitorsInfo;
    }


    public Integer getOnlineCount() {
        return Optional.ofNullable(redisTemplate.keys(ONLINE_COUNT + "*")).orElse(Collections.emptySet()).size();
    }

    public void addOnlineCount(String id, String sessionId) {
        redisTemplate.opsForSet().add(ONLINE_COUNT + ":" + id, sessionId);
    }

    public void subOnlineCount(String id, String sessionId) {
        redisTemplate.opsForSet().remove(ONLINE_COUNT + ":" + id, sessionId);
    }

    public void clearOnlineCount() {
        redisTemplate.delete(Optional.ofNullable(redisTemplate.keys(ONLINE_COUNT + "*")).orElse(Collections.emptySet()));
    }

    public Integer getNumberOfVisitorsToday() {
        return Optional.ofNullable(redisTemplate.opsForSet().members(NUMBER_OF_VISITORS_TODAY)).orElse(Collections.emptySet()).size();
    }

    public void addNumberOfVisitorsToday(String id) {
        redisTemplate.opsForSet().add(NUMBER_OF_VISITORS_TODAY, id);
    }

    public void clearNumberOfVisitorsToday() {
        redisTemplate.delete(NUMBER_OF_VISITORS_TODAY);
    }

    public Map getCurrentCount() {
        Map<String, Integer> map = new HashMap<>();
        map.put(ONLINE_COUNT, getOnlineCount());
        map.put(NUMBER_OF_VISITORS_TODAY, getNumberOfVisitorsToday());
        return map;
    }

    public Map getCurrentCountPerHour() {
        Map<String, Integer> map = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
        SimpleDateFormat outDateFormat = new SimpleDateFormat("HH:mm");

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(new Date());
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        for (int i = 0; i <= calendar.get(calendar.HOUR_OF_DAY); i++) {
            String time = dateFormat.format(startCalendar.getTime());
            String onlinersOnThatTime = redisTemplate.opsForValue().get(time);
            Integer onlineCount = onlinersOnThatTime == null ? 0 : Integer.parseInt(onlinersOnThatTime);
            map.put(outDateFormat.format(startCalendar.getTime()), onlineCount);
            startCalendar.add(Calendar.HOUR, +1);
        }
        return map;
    }
}
