package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify-template.groovy') {
    changeSet(author: 'jcalaz@163.com', id: '2018-08-09-add-notify-template') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'NOTIFY_TEMPLATE_S', startValue: "1")
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
    changeSet(author: 'youquan.deng@hand-china.com', id: '2018-09-05-add-column-pm') {
        addColumn(tableName: "NOTIFY_TEMPLATE") {
            column(name: 'PM_TITLE', type: 'VARCHAR(32)', remarks: '站内信模版标题', afterColumn: 'SMS_CONTENT')
            column(name: 'PM_CONTENT', type: 'TEXT', remarks: '站内信模版内容', afterColumn: 'PM_TITLE')
        }
    }
    changeSet(author: 'youquan.deng@hand-china.com', id: '2018-09-26-change-column') {
        if (helper.isOracle()) {
            modifyDataType(columnName: 'CODE', newDataType: 'VARCHAR(64)', tableName: 'NOTIFY_TEMPLATE')
        }
        if (helper.isMysql()) {
            //会删掉注释
            addNotNullConstraint(tableName: 'NOTIFY_TEMPLATE', columnName: 'CODE', columnDataType: 'VARCHAR(64)')
        }
    }

    changeSet(author: 'bg_zyy@foxmail.com', id: '2019-11-15-add-column_enabled') {
        addColumn(tableName: "NOTIFY_TEMPLATE") {
            column(name: 'WH_CONTENT', type: 'TEXT', remarks: 'webhook 文本消息模版', afterColumn: 'PM_CONTENT')
        }
    }


    changeSet(id: '2019-10-22-notify_template-modify-column', author: 'longhe1996@icloud.com') {
        addColumn(tableName: 'NOTIFY_TEMPLATE') {
            column(name: 'SEND_SETTING_CODE', type: 'VARCHAR(32)', defaultValue: "EMPTY STRING", remarks: '模版所属的发送设置CODE', afterColumn: 'BUSINESS_TYPE') {
                constraints(nullable: false)
            }
            column(name: 'SENDING_TYPE', type: 'VARCHAR(16)', defaultValue: "EMPTY STRING", remarks: '模版类型:email,sms,pm,webhook', afterColumn: 'SEND_SETTING_CODE') {
                constraints(nullable: false)
            }
            column(name: 'TITLE', type: 'VARCHAR(64)', remarks: '模版标题')
            column(name: 'CONTENT', type: 'TEXT', remarks: '模版内容')
        }
        //数据迁移
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "UPDATE notify_template " +
                    "SET SEND_SETTING_CODE=BUSINESS_TYPE," +
                    "SENDING_TYPE=MESSAGE_TYPE," +
                    "TITLE=CASE WHEN MESSAGE_TYPE='pm' THEN PM_TITLE WHEN MESSAGE_TYPE='email' THEN EMAIL_TITLE END," +
                    "CONTENT=CASE WHEN MESSAGE_TYPE='pm' THEN PM_CONTENT WHEN MESSAGE_TYPE='email' THEN EMAIL_CONTENT WHEN MESSAGE_TYPE='sms' THEN SMS_CONTENT WHEN MESSAGE_TYPE='webhook' THEN WH_CONTENT END;"
        }
        dropUniqueConstraint(constraintName: 'UK_NOTIFY_TEMPLATE_U1', tableName: 'NOTIFY_TEMPLATE')
        dropColumn(tableName: 'NOTIFY_TEMPLATE', columnName: 'CODE')
        dropColumn(tableName: 'NOTIFY_TEMPLATE', columnName: 'NAME')
        dropColumn(tableName: 'NOTIFY_TEMPLATE', columnName: 'MESSAGE_TYPE')
        dropColumn(tableName: 'NOTIFY_TEMPLATE', columnName: 'BUSINESS_TYPE')
        dropColumn(tableName: 'NOTIFY_TEMPLATE', columnName: 'EMAIL_TITLE')
        dropColumn(tableName: 'NOTIFY_TEMPLATE', columnName: 'EMAIL_CONTENT')
        dropColumn(tableName: 'NOTIFY_TEMPLATE', columnName: 'PM_TITLE')
        dropColumn(tableName: 'NOTIFY_TEMPLATE', columnName: 'PM_CONTENT')
        dropColumn(tableName: 'NOTIFY_TEMPLATE', columnName: 'SMS_CONTENT')
        dropColumn(tableName: 'NOTIFY_TEMPLATE', columnName: 'WH_CONTENT')

        addUniqueConstraint(tableName: 'NOTIFY_TEMPLATE', columnNames: 'SENDING_TYPE,SEND_SETTING_CODE', constraintName: "UK_NOTIFY_TEMPLATE_U1")
    }
}