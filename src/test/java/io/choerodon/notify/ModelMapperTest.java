package io.choerodon.notify;

import io.choerodon.notify.api.dto.EmailTemplateDTO;
import io.choerodon.notify.api.dto.EmailTemplateQueryDTO;
import io.choerodon.notify.domain.Config;
import io.choerodon.notify.domain.Template;
import io.choerodon.notify.infra.utils.ConvertUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.mail.MailProperties;

@Slf4j
public class ModelMapperTest {

    private final ModelMapper mapper = new ModelMapper();

    @Before
    public void init(){
        mapper.addMappings(EmailTemplateDTO.entity2Dto());
        mapper.addMappings(EmailTemplateDTO.dto2Entity());
        mapper.addMappings(EmailTemplateQueryDTO.dto2Entity());
        mapper.addMappings(EmailTemplateQueryDTO.entity2Dto());
        mapper.validate();
    }

    @Test
    public void emailTemplateDTO() {
        Template template = new Template();
        template.setBusinessType("setBusinessType");
        template.setMessageType("email");
        template.setCode("code");
        template.setEmailTitle("title");
        template.setEmailContent("emailContent");
        template.setId(10L);
        template.setName("name");
        template.setSmsContent("smsContent");
        template.setObjectVersionNumber(1L);
        template.setIsPredefined(true);

        EmailTemplateDTO dto = mapper.map(template, EmailTemplateDTO.class);

        log.info("EmailTemplateDTO {}", dto);

        Template template2 = mapper.map(dto, Template.class);

        log.info("template2 {}", template2);

    }

    @Test
    public void emailTemplateQueryDTO() {
        Template template = new Template();
        template.setBusinessType("setBusinessType");
        template.setMessageType("email");
        template.setCode("code");
        template.setEmailTitle("title");
        template.setEmailContent("emailContent");
        template.setId(10L);
        template.setName("name");
        template.setSmsContent("smsContent");
        template.setObjectVersionNumber(1L);
        template.setIsPredefined(true);

        EmailTemplateQueryDTO dto = mapper.map(template, EmailTemplateQueryDTO.class);

        log.info("EmailTemplateQueryDTO {}", dto);

        Template template2 = mapper.map(dto, Template.class);

        log.info("template2 {}", template2);

    }

    @Test
    public void MailProperties() {
        MailProperties mailProperties = new MailProperties();
        mailProperties.setHost("host");
        mailProperties.setPassword("122");
        mailProperties.setProtocol("pr");
        mailProperties.setPort(10);
        mailProperties.setUsername("sdsd");

        Config saveConfig = mapper.map(mailProperties, Config.class);

        log.info("result {}", saveConfig);

    }
}
