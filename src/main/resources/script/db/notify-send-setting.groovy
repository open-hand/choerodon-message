package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify-send-setting.groovy') {
    changeSet(author: 'jcalaz@163.com', id: '2018-08-09-add-notify-send-setting') {
        createTable(tableName: "notify_send_setting") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'code', type: 'VARCHAR(32)', remarks: '业务类型code') {
                constraints(nullable: false, unique: true)
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '消息业务类型名称。例如验证码,用户激活等') {
                constraints(nullable: false)
            }
            column(name: 'description', type: 'VARCHAR(255)', remarks: '消息业务类型描述') {
                constraints(nullable: false)
            }
            column(name: 'fd_level', type: 'VARCHAR(16)', remarks: '所属层级') {
                constraints(nullable: false)
            }
            column(name: 'email_template_id', type: 'BIGINT UNSIGNED', remarks: '邮箱模板id')
            column(name: 'sms_template_id', type: 'BIGINT UNSIGNED', remarks: '短信模板id')
            column(name: 'pm_template_id', type: 'BIGINT UNSIGNED', remarks: '站内信模板id')

            column(name: 'retry_count', type: 'INT UNSIGNED', defaultValue: 0, remarks: '重试次数。默认0次') {
                constraints(nullable: false)
            }

            column(name: 'is_send_instantly', type: 'TINYINT(1)', defaultValue: 1, remarks: '是否即时发送，默认即时发送') {
                constraints(nullable: false)
            }

            column(name: 'is_manual_retry', type: 'TINYINT(1)', defaultValue: 0, remarks: '是否允许手动重试发送，默认不允许') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}