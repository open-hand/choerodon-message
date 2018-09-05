package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify-template.groovy') {
    changeSet(author: 'jcalaz@163.com', id: '2018-08-09-add-notify-template') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'NOTIFY_TEMPLATE_S', startValue:"1")
        }
        createTable(tableName: "NOTIFY_TEMPLATE") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_NOTIFY_TEMPLATE')
            }
            column(name: 'CODE', type: 'VARCHAR(32)', remarks: '模版code') {
                constraints(nullable: false)
            }
            column(name: 'NAME', type: 'VARCHAR(32)', remarks: '模版名称') {
                constraints(nullable: false)
            }
            column(name: 'MESSAGE_TYPE', type: 'VARCHAR(16)', remarks: '模版类型:email,sms') {
                constraints(nullable: false)
            }
            column(name: 'IS_PREDEFINED', type: 'TINYINT(1)', remarks: '是否为预定义') {
                constraints(nullable: false)
            }
            column(name: 'BUSINESS_TYPE', type: 'VARCHAR(64)', remarks: '模版业务类型') {
                constraints(nullable: false)
            }
            column(name: 'EMAIL_TITLE', type: 'VARCHAR(64)', remarks: 'email模版标题')
            column(name: 'EMAIL_CONTENT', type: 'TEXT', remarks: 'email模版内容')
            column(name: 'SMS_CONTENT', type: 'TEXT', remarks: '短信模版内容')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'NOTIFY_TEMPLATE', columnNames: 'CODE,MESSAGE_TYPE', constraintName: "message_type_code_unique")
    }

    changeSet(author: 'superleader8@gmail.com', id: '2018-09-05-rename-unique') {
        dropUniqueConstraint(constraintName: 'message_type_code_unique', tableName: 'NOTIFY_TEMPLATE')
        addUniqueConstraint(tableName: 'NOTIFY_TEMPLATE', columnNames: 'CODE,MESSAGE_TYPE', constraintName: "UK_NOTIFY_TEMPLATE_U1")
    }
}