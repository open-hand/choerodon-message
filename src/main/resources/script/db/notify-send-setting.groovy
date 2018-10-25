package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify-send-setting.groovy') {
    changeSet(author: 'jcalaz@163.com', id: '2018-08-09-add-notify-send-setting') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'NOTIFY_SEND_SETTING_S', startValue: "1")
        }
        createTable(tableName: "NOTIFY_SEND_SETTING") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_NOTIFY_SEND_SETTING')
            }
            column(name: 'CODE', type: 'VARCHAR(32)', remarks: '业务类型code') {
                constraints(nullable: false, unique: true, uniqueConstraintName: 'UK_NOTIFY_SEND_SETTING_U1')
            }
            column(name: 'NAME', type: 'VARCHAR(64)', remarks: '消息业务类型名称。例如验证码,用户激活等') {
                constraints(nullable: false)
            }
            column(name: 'DESCRIPTION', type: 'VARCHAR(255)', remarks: '消息业务类型描述') {
                constraints(nullable: false)
            }
            column(name: 'LEVEL', type: 'VARCHAR(16)', remarks: '所属层级') {
                constraints(nullable: false)
            }
            column(name: 'EMAIL_TEMPLATE_ID', type: 'BIGINT UNSIGNED', remarks: '邮箱模板id')
            column(name: 'SMS_TEMPLATE_ID', type: 'BIGINT UNSIGNED', remarks: '短信模板id')
            column(name: 'PM_TEMPLATE_ID', type: 'BIGINT UNSIGNED', remarks: '站内信模板id')

            column(name: 'RETRY_COUNT', type: 'INT UNSIGNED', defaultValue: 0, remarks: '重试次数。默认0次') {
                constraints(nullable: false)
            }

            column(name: 'IS_SEND_INSTANTLY', type: 'TINYINT(1)', defaultValue: 1, remarks: '是否即时发送，默认即时发送') {
                constraints(nullable: false)
            }

            column(name: 'IS_MANUAL_RETRY', type: 'TINYINT(1)', defaultValue: 0, remarks: '是否允许手动重试发送，默认不允许') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
    changeSet(author: 'jcalaz@163.com', id: '2018-09-19-rename-column') {
        renameColumn(tableName: 'NOTIFY_SEND_SETTING', oldColumnName: 'LEVEL', newColumnName: 'FD_LEVEL', columnDataType: 'VARCHAR(16)', remarks: '所属层级')
    }
    changeSet(author: 'youquan.deng@hand-china.com', id: '2018-10-25-add-column') {
        addColumn(tableName: "NOTIFY_SEND_SETTING") {
            column(name: 'PM_TYPE', type: 'VARCHAR(16)', defaultValue: "msg", remarks: '站内信消息类型（消息：msg，通知：notice）', afterColumn: 'FD_LEVEL')
        }
    }
}