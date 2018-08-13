package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify-template.groovy') {
    changeSet(author: 'jcalaz@163.com', id: '2018-08-09-add-notify-template') {
        createTable(tableName: "notify_template") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'code', type: 'VARCHAR(32)', remarks: '模版code') {
                constraints(nullable: false)
            }
            column(name: 'name', type: 'VARCHAR(32)', remarks: '模版名称') {
                constraints(nullable: false)
            }
            column(name: 'message_type', type: 'VARCHAR(16)', remarks: '模版类型:email,sms') {
                constraints(nullable: false)
            }
            column(name: 'is_predefined', type: 'TINYINT(1)', remarks: '是否为预定义') {
                constraints(nullable: false)
            }

            column(name: 'business_type', type: 'VARCHAR(64)', remarks: '模版业务类型') {
                constraints(nullable: false)
            }

            column(name: 'email_title', type: 'VARCHAR(64)', remarks: 'email模版标题')

            column(name: 'email_content', type: 'TEXT', remarks: 'email模版内容')

            column(name: 'sms_content', type: 'TEXT', remarks: '短信模版内容')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'notify_template', columnNames: 'code,message_type', constraintName: "message_type_code_unique")
    }
}