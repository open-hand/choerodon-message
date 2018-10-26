package io.choerodon.notify.api.service.impl

import io.choerodon.notify.api.service.FileService
import io.choerodon.notify.infra.feign.FileFeignClient
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockMultipartHttpServletRequest
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

/**
 * @author dengyouquan
 * */

class FileServiceImplSpec extends Specification {
    private FileFeignClient fileFeignClient = Mock(FileFeignClient)
    private FileService fileService = new FileServiceImpl(fileFeignClient)

    def "UploadFile"() {
        given: "构造请求参数"
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest()
        MultipartFile file = new MockMultipartFile("file", new byte[10])
        request.addFile(file)
        List<MultipartFile> files = new ArrayList<>()
        files.add(file)
        ResponseEntity<String> response = new ResponseEntity<>("http://www.choerodon.io/notify-service/1.png",HttpStatus.OK)

        when: "调动方法"
        fileService.uploadFile(request)

        then: "校验结果"
        1 * fileFeignClient.uploadFile(_, _, _) >> response
    }
}
