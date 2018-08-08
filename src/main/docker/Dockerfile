FROM registry.cn-hangzhou.aliyuncs.com/choerodon-tools/javabase:0.5.0
COPY app.jar /notify-service.jar
ENTRYPOINT exec java $JAVA_OPTS  -jar /notify-service.jar
