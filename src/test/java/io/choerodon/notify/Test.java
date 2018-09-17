package io.choerodon.notify;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.notify.websocket.ws.WebSocketReceivePayload;
import org.json.JSONObject;

public class Test {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @org.junit.Test
    public void name() throws Exception {

        WebSocketReceivePayload<String> payload1 = new WebSocketReceivePayload<>("test", "data");
        String json1 = objectMapper.writeValueAsString(payload1);
        System.out.println(json1);

        WebSocketReceivePayload<?> payload2 = objectMapper.readValue(json1, WebSocketReceivePayload.class);
        System.out.println(payload2);
        System.out.println(payload2.getData().getClass());


        WebSocketReceivePayload<TestOne> payload3 = new WebSocketReceivePayload<>("test", new TestOne("xiaoming",34));
        String json2 = objectMapper.writeValueAsString(payload3);
        System.out.println(json2);


        JSONObject jsonObject = new JSONObject(json2);
        String type = jsonObject.getString("type");
        if (type.equals("test")) {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(WebSocketReceivePayload.class, TestOne.class);
            WebSocketReceivePayload<?> payload4 = objectMapper.readValue(json2, javaType);
            System.out.println(payload4.getData());
        }

    }

    static class TestOne{
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public TestOne() {
        }

        public TestOne(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "TestOne{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }


}
