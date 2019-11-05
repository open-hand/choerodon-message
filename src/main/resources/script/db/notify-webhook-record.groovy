package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify-webhook-record.groovy') {
    changeSet(author: 'jiameng.cao', id: '2019-11-04-add-notify-webhook-record') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'NOTIFY_WEBHOOK_RECORD_S', startValue: "1")
        }
        createTable(tableName: "NOTIFY_WEBHOOK_RECORD") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_NOTIFY_WEBHOOK_RECORD')
            }
            column(name: 'CONTENT', type: 'TEXT', remarks: 'webhook消息内容')
            column(name: "STATUS", type: "VARCHAR(16)", remarks: '消息记录的状态')
            column(name: 'SEND_SETTING_CODE', type: 'VARCHAR(32)', remarks: '模版业务类型code') {
                constraints(nullable: false)
            }
            column(name: 'FAILED_REASON', type: 'VARCHAR(255)', remarks: '失败原因')
            column(name: 'PROJECT_ID', type: 'BIGINT UNSIGNED', remarks: 'webhook的项目ID') {
                constraints(nullable: false)
            }
            column(name: 'WEBHOOK_PATH', type: 'varchar(255)', remarks: 'webhook 地址') {
                constraints(nullable: false)
            }
            column(name: "SEND_TIME", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

}