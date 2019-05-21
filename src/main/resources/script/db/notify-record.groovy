package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify-record.groovy') {
    changeSet(author: 'jcalaz@163.com', id: '2018-08-21-add-notify-record') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'NOTIFY_RECORD_S', startValue: "1")
        }
        createTable(tableName: "NOTIFY_RECORD") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_NOTIFY_RECORD')
            }
            column(name: "STATUS", type: "VARCHAR(16)", remarks: '消息记录的状态')
            column(name: 'RECEIVE_ACCOUNT', type: 'VARCHAR(64)', remarks: '接收账号') {
                constraints(nullable: false)
            }
            column(name: 'BUSINESS_TYPE', type: 'VARCHAR(32)', remarks: '模版业务类型code') {
                constraints(nullable: false)
            }
            column(name: "RETRY_COUNT", type: "INT UNSIGNED", remarks: '手动重发次数', defaultValue: "0") {
                constraints(nullable: false)
            }
            column(name: 'VARIABLES', type: 'TEXT', remarks: '发送邮件的参数(map形式)')
            column(name: 'FAILED_REASON', type: 'VARCHAR(64)', remarks: '失败原因')
            column(name: 'MESSAGE_TYPE', type: 'VARCHAR(16)', remarks: '消息类型:email,sms') {
                constraints(nullable: false)
            }
            column(name: 'TEMPLATE_ID', type: 'BIGINT UNSIGNED', remarks: '使用的邮件模板id') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: 'superlee', id: '2019-05-21-modify-type') {
        modifyDataType(columnName: 'FAILED_REASON', newDataType: 'VARCHAR(255)', tableName: 'NOTIFY_RECORD')
    }
}