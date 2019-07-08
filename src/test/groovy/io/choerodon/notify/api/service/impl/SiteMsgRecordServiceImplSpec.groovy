package io.choerodon.notify.api.service.impl

import io.choerodon.core.domain.Page
import io.choerodon.notify.api.dto.OrganizationDTO
import io.choerodon.notify.api.dto.ProjectDTO
import io.choerodon.notify.api.dto.SiteMsgRecordDTO
import io.choerodon.notify.api.dto.UserDTO
import io.choerodon.notify.api.service.SiteMsgRecordService
import io.choerodon.notify.domain.Template
import io.choerodon.notify.infra.feign.UserFeignClient
import io.choerodon.notify.infra.mapper.SiteMsgRecordMapper
import io.choerodon.websocket.send.MessageSender
import org.springframework.beans.BeanUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class SiteMsgRecordServiceImplSpec extends Specification {
    private final SiteMsgRecordMapper siteMsgRecordMapper = Mock(SiteMsgRecordMapper)
    private final MessageSender messageSender = Mock(MessageSender)
    private final UserFeignClient userFeignClient = Mock(UserFeignClient)
    private SiteMsgRecordService siteMsgRecordService =
            new SiteMsgRecordServiceImpl(siteMsgRecordMapper, messageSender, userFeignClient)

    def "PagingQueryByUserId"() {
        given: "构造请求参数"
        List<UserDTO> users = new ArrayList<>()
        for (int i = 0; i < 1; i++) {
            UserDTO dto = new UserDTO()
            dto.setId(1L)
            dto.setRealName("dengyouquan")
            dto.setLoginName("20631")
            dto.setImageUrl("/image.jpg")
            users.add(dto)
        }
        List<SiteMsgRecordDTO> list = new ArrayList<>()
        for (int i = 0; i < 3; i++) {
            SiteMsgRecordDTO dto = new SiteMsgRecordDTO()
            dto.setSendBy(1L)
            dto.setType("type")
            dto.setTitle("title")
            dto.setDeleted(false)
            dto.setRead(true)
            dto.setUserId(1L)
            dto.setSendByUser(users.get(0))
            dto.setContent("title")
            SiteMsgRecordDTO dto1 = new SiteMsgRecordDTO()
            BeanUtils.copyProperties(dto, dto1)
            list.add(dto)
            list.add(dto1)
        }
        Page<SiteMsgRecordDTO> page = new Page()
        page.setContent(list)
        ResponseEntity<List<UserDTO>> entity = new ResponseEntity<>(users, HttpStatus.OK)
        ResponseEntity<List<OrganizationDTO>> organizationEntity = new ResponseEntity<>(new ArrayList<OrganizationDTO>(), HttpStatus.OK)
        ResponseEntity<List<ProjectDTO>> projectEntity = new ResponseEntity<>(new ArrayList<ProjectDTO>(), HttpStatus.OK)

        when: "调用方法"
        siteMsgRecordService.pagingQueryByUserId(1L, true, "type", 0,10)

        then: "校验结果"
        1 * siteMsgRecordMapper.selectByUserIdAndReadAndDeleted(_, _, _) >> page
        1 * userFeignClient.listUsersByIds(_) >> entity
        1 * userFeignClient.listOrganizationsByIds(_) >> organizationEntity
        1 * userFeignClient.listProjectsByIds(_) >> projectEntity
    }

    def "InsertRecord"() {
        given: "构造请求参数"
        Template template = new Template()
        template.setPmTitle("title")
        def pmContent = "content"
        Long[] ids = new Long[1]
        ids[0] = 1L

        when: "调用方法"
        siteMsgRecordService.insertRecord(template, pmContent, ids)

        then: "校验结果"
        1 * siteMsgRecordMapper.batchInsert(_)
    }
}
