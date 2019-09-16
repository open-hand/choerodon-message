package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify_webhook.groovy') {
    changeSet(author: 'bg_zyy@foxmail.com', id: '2019-09-11-notify-webhook') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'NOTIFY_WEBHOOK_S', startValue: "10001")
        }
        createTable(tableName: "NOTIFY_WEBHOOK", remarks: "webhook 存储表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表 ID，主键，单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_NOTIFY_WEBHOOK')
            }
            column(name: 'WEBHOOK_NAME', type: 'varchar(255)', remarks: 'webhook 名称')
            column(name: 'WEBHOOK_TYPE', type: 'varchar(255)', remarks: 'webhook 类型，钉钉') {
                constraints(nullable: false)
            }
            column(name: 'WEBHOOK_PATH', type: 'varchar(255)', remarks: 'webhook 地址') {
                constraints(nullable: false)
            }
            column(name: 'PROJECT_ID', type: 'BIGINT UNSIGNED', remarks: '项目 ID') {
                constraints(nullable: false)
            }
            column(name: 'ENABLE_FLAG', type: 'TINYINT UNSIGNED', remarks: '是否启用')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}