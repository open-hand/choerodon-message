FROM registry.cn-shanghai.aliyuncs.com/c7n/javabase:0.9.0
COPY app.jar /notify-service.jar
RUN chown -R www-data:www-data /notify-service.jar
USER www-data
CMD java $JAVA_OPTS $SKYWALKING_OPTS -jar /notify-service.jar