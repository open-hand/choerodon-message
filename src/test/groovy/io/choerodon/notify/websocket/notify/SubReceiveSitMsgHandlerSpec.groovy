package io.choerodon.notify.websocket.notify

import io.choerodon.notify.api.service.WebSocketSendService
import io.choerodon.notify.infra.mapper.SiteMsgRecordMapper
import io.choerodon.notify.websocket.relationship.DefaultRelationshipDefining
import io.choerodon.notify.websocket.relationship.RelationshipDefining
import io.choerodon.notify.websocket.send.MessageSender
import org.springframework.web.socket.WebSocketSession
import spock.lang.Specification

/**
 * @author dengyouquan
 * */
class SubReceiveSitMsgHandlerSpec extends Specification {
    private RelationshipDefining relationshipDefining = Mock(RelationshipDefining)
    private MessageSender messageSender = Mock(MessageSender)
    private final SiteMsgRecordMapper siteMsgRecordMapper = Mock(SiteMsgRecordMapper)

    private WebSocketSendService webSocketSendService = Mock(WebSocketSendService)

    private DefaultRelationshipDefining defaultRelationshipDefining = Mock(DefaultRelationshipDefining)
    private SubReceiveMessageHandler subReceiveSitMsgHandler =
            new SubReceiveMessageHandler(relationshipDefining, messageSender, siteMsgRecordMapper, webSocketSendService, defaultRelationshipDefining)

    def "Handle"() {
        given: "构造请求参数"
        def key = "choerodon:msg:site-msg:1"
        def session = Mock(WebSocketSession)
        session.getId() >> { return "xxxssswww" }

        when: "调用方法"
        subReceiveSitMsgHandler.handle(session, key)
        then: "校验结果"
        1 * relationshipDefining.contact(_, _)
        1 * relationshipDefining.getKeysBySession(_)
        siteMsgRecordMapper.selectCountOfUnRead(_) >> 1
    }
}
