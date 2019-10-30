package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify_webhook_message_setting.groovy') {
    changeSet(author: 'longhe1996@icloud.com', id: '2019-10-22-notify_webhook_message_setting') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'NOTIFY_WEBHOOK_MESSAGE_SETTING_S', startValue: "1")
        }
        createTable(tableName: "NOTIFY_WEBHOOK_MESSAGE_SETTING", remarks: "此表记录某WEBHOOK配置的发送设置置") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表 ID，主键，单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_NOTIFY_WEBHOOK_MESSAGE_SETTING')
            }
            column(name: 'WEBHOOK_ID', type: 'BIGINT UNSIGNED', remarks: 'WEBHOOK主键') {
                constraints(nullable: false)
            }
            column(name: 'SEND_SETTING_ID', type: 'BIGINT UNSIGNED', remarks: '发送设置主键') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'NOTIFY_WEBHOOK_MESSAGE_SETTING', columnNames: 'WEBHOOK_ID,SEND_SETTING_ID', constraintName: "UK_NOTIFY_WEBHOOK_MESSAGE_SETTING_U1")
    }
}