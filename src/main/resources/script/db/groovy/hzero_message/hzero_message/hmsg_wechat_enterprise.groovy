package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_wechat_enterprise.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-10-28-hmsg_wechat_enterprise") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_wechat_enterprise_s', startValue:"1")
        }
        createTable(tableName: "hmsg_wechat_enterprise", remarks: "企业微信配置") {
            column(name: "server_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "server_code", type: "varchar(" + 30 * weight + ")",  remarks: "配置编码")  {constraints(nullable:"false")}  
            column(name: "server_name", type: "varchar(" + 30 * weight + ")",  remarks: "配置名称")  {constraints(nullable:"false")}  
            column(name: "auth_type", type: "varchar(" + 30 * weight + ")",  remarks: "授权类型，值集：HMSG.WECHAT.AUTH_TYPE")  {constraints(nullable:"false")}  
            column(name: "corpid", type: "varchar(" + 60 * weight + ")",  remarks: "企业ID")   
            column(name: "corpsecret", type: "varchar(" + 120 * weight + ")",  remarks: "凭证密钥")   
            column(name: "auth_address", type: "varchar(" + 240 * weight + ")",  remarks: "第三方授权地址")   
            column(name: "callback_url", type: "varchar(" + 240 * weight + ")",  remarks: "回调地址")
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"server_code,tenant_id",tableName:"hmsg_wechat_enterprise",constraintName: "hmsg_wechat_enterprise_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-11-13-hmsg_wechat_enterprise") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hmsg_wechat_enterprise", columnName: 'server_name', newDataType: "varchar(" + 60 * weight + ")")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-11-14-hmsg_wechat_enterprise") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hmsg_wechat_enterprise", columnName: 'auth_address', newDataType: "varchar(" + 480 * weight + ")")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-11-15-hmsg_wechat_enterprise") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hmsg_wechat_enterprise", columnName: 'callback_url', newDataType: "varchar(" + 480 * weight + ")")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-05-14-hmsg_wechat_enterprise") {
        addColumn(tableName: "hmsg_wechat_enterprise") {
            column(name: "agent_id", type: "bigint",  remarks: "默认应用ID")
        }
    }
}