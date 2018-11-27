#!/usr/bin/env bash
mkdir -p choerodon_temp
if [ ! -f choerodon_temp/choerodon-tool-liquibase.jar ]
then
    curl http://nexus.choerodon.com.cn/repository/choerodon-release/io/choerodon/choerodon-tool-liquibase/0.5.2.RELEASE/choerodon-tool-liquibase-0.5.2.RELEASE.jar -L  -o choerodon_temp/choerodon-tool-liquibase.jar
fi
java -Dspring.datasource.url="jdbc:oracle:thin:@127.0.0.1:1521:xe" \
 -Dspring.datasource.username=system \
 -Dspring.datasource.password=oracle \
 -Ddata.drop=false -Ddata.init=true \
 -Ddata.dir=src/main/resources \
 -jar choerodon_temp/choerodon-tool-liquibase.jar