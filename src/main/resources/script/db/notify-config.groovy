package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify-config.groovy') {
    changeSet(author: 'jcalaz@163.com', id: '2018-08-09-add-notify-config') {
        createTable(tableName: "notify_config") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'email_account', type: 'VARCHAR(64)', remarks: '邮箱账号')
            column(name: 'email_password', type: 'VARCHAR(64)', remarks: '邮箱密码')
            column(name: 'email_send_name', type: 'VARCHAR(64)', remarks: '邮箱发件人')
            column(name: 'email_protocol', type: 'VARCHAR(16)', remarks: '邮箱服务器协议')
            column(name: 'email_host', type: 'VARCHAR(64)', remarks: '邮箱服务器地址')
            column(name: 'email_port', type: 'INT UNSIGNED', remarks: '邮箱服务器端口')
            column(name: 'email_ssl', type: 'TINYINT(1)', remarks: '邮箱服务器是否为ssl')
            column(name: 'sms_host', type: 'VARCHAR(64)', remarks: '短信服务器域名')
            column(name: 'sms_key_id', type: 'VARCHAR(64)', remarks: '短信服务器key')
            column(name: 'sms_key_password', type: 'VARCHAR(64)', remarks: '短信服务器')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}