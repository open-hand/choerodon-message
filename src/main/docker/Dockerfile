FROM registry.cn-hangzhou.aliyuncs.com/choerodon-tools/javabase:0.8.0
COPY app.jar /notify-service.jar
ENTRYPOINT exec java $JAVA_OPTS $SKYWALKING_OPTS  -jar /notify-service.jar
