package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify-record.groovy') {
    changeSet(author: 'jcalaz@163.com', id: '2018-08-21-add-notify-record') {
        createTable(tableName: "notify_record") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: "status", type: "VARCHAR(16)", remarks: '消息记录的状态') {
                constraints(nullable: false)
            }
            column(name: 'receive_account', type: 'VARCHAR(64)', remarks: '接收账号') {
                constraints(nullable: false)
            }
            column(name: 'template_type', type: 'VARCHAR(32)', remarks: '模版类型') {
                constraints(nullable: false)
            }
            column(name: 'failed_reason', type: 'VARCHAR(32)', remarks: '失败原因')
            column(name: 'message_type', type: 'VARCHAR(16)', remarks: '消息类型:email,sms') {
                constraints(nullable: false)
            }
            column(name: 'max_retry_count', type: 'INT UNSIGNED', remarks: '最大重试次数')
            column(name: 'is_manual_retry', type: 'TINYINT(1)', remarks: '是否允许手动重试发送')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}