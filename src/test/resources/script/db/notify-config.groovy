package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify-config.groovy') {
    changeSet(author: 'jcalaz@163.com', id: '2018-08-09-add-notify-config') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'NOTIFY_CONFIG_S', startValue:"1")
        }
        createTable(tableName: "NOTIFY_CONFIG") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_NOTIFY_CONFIG')
            }
            column(name: 'EMAIL_ACCOUNT', type: 'VARCHAR(64)', remarks: '邮箱账号')
            column(name: 'EMAIL_PASSWORD', type: 'VARCHAR(64)', remarks: '邮箱密码')
            column(name: 'EMAIL_SEND_NAME', type: 'VARCHAR(64)', remarks: '邮箱发件人')
            column(name: 'EMAIL_PROTOCOL', type: 'VARCHAR(16)', remarks: '邮箱服务器协议')
            column(name: 'EMAIL_HOST', type: 'VARCHAR(64)', remarks: '邮箱服务器地址')
            column(name: 'EMAIL_PORT', type: 'INT UNSIGNED', remarks: '邮箱服务器端口')
            column(name: 'EMAIL_SSL', type: 'TINYINT(1)', remarks: '邮箱服务器是否为ssl')
            column(name: 'SMS_DOMAIN', type: 'VARCHAR(64)', remarks: '短信服务器域名')
            column(name: 'SMS_KEY_ID', type: 'VARCHAR(64)', remarks: '短信服务器key')
            column(name: 'SMS_KEY_PASSWORD', type: 'VARCHAR(64)', remarks: '短信服务器')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}