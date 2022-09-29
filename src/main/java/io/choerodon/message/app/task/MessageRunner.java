package io.choerodon.message.app.task;

import java.util.List;

import org.hzero.message.domain.entity.MessageTemplate;
import org.hzero.message.infra.mapper.MessageTemplateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Created by wangxiang on 2021/4/20
 */
@Component
public class MessageRunner implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${choerodon.fix.data.flag:true}")
    private Boolean fixDataFlag;

    @Autowired
    private MessageTemplateMapper messageTemplateMapper;


    @Override
    public void run(String... args) throws Exception {
        if (fixDataFlag) {
            new Thread(() -> {
                try {
                    List<MessageTemplate> messageTemplates = messageTemplateMapper.selectAll();
                    if (CollectionUtils.isEmpty(messageTemplates)) {
                        return;
                    }
                    messageTemplates.forEach(messageTemplate -> {
                        messageTemplate.setMessageCategoryCode(null);
                        messageTemplate.setMessageSubcategoryCode(null);
                        messageTemplateMapper.updateByPrimaryKey(messageTemplate);
                    });

                } catch (Exception e) {
                    logger.error("Failed to fix data", e);
                }
            }).start();
        }
    }
}
