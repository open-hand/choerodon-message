package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify_sms_config.groovy') {
    changeSet(author: 'superlee', id: '2019-05-16-notify-sms-config') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'NOTIFY_SMS_CONFIG_S', startValue: "1")
        }
        createTable(tableName: "NOTIFY_SMS_CONFIG") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_NOTIFY_SYSTEM_ANNOUNCEMENT')
            }
            column(name: 'ORGANIZATION_ID', type: 'BIGINT UNSIGNED', remarks: '组织id', defaultValue: '0') {
                constraints(unique: true, uniqueConstraintName: 'UK_NOTIFY_SMS_CONFIG_U1', nullable: false)
            }
            column(name: 'SIGNATURE', type: 'VARCHAR(255)', remarks: '短信签名') {
                constraints(nullable: false)
            }
            column(name: 'HOST_ADDRESS', type: 'VARCHAR(255)', remarks: '短信服务器地址') {
                constraints(nullable: false)
            }
            column(name: 'HOST_PORT', type: 'VARCHAR(16)', remarks: '短信服务器端口')
            column(name: 'SEND_TYPE', type: 'VARCHAR(16)', defaultValue: 'single', remarks: '发送类型,单条(single),批量(batch),异步(async),默认值为single') {
                constraints(nullable: false)
            }
            column(name: 'SINGLE_SEND_API', type: 'VARCHAR(255)', remarks: '单条调用api，发送给一个用户')
            column(name: 'BATCH_SEND_API', type: 'VARCHAR(255)', remarks: '批量发送调用api')
            column(name: 'ASYNC_SEND_API', type: 'VARCHAR(255)', remarks: '异步发送调用api')
            column(name: 'SECRET_KEY', type: 'VARCHAR(255)', remarks: '短信服务器密钥') {
                constraints(nullable: false)
            }
            column(name: 'CONTENT_FIELD', type: 'VARCHAR(64)', remarks: 'content对应的接口字段名') {
                constraints(nullable: false)
            }
            column(name: 'PHONE_FIELD', type: 'VARCHAR(64)', remarks: '手机号对应字段') {
                constraints(nullable: false)
            }
            column(name: 'SIGNATURE_FIELD', type: 'VARCHAR(64)', remarks: '签名对应字段') {
                constraints(nullable: false)
            }
            column(name: 'BUSINESS_CODE_FIELD', type: 'VARCHAR(64)', remarks: '业务代码映射字段') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1") {
                constraints(nullable: true)
            }
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}