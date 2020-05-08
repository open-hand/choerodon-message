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
    changeSet(author: 'youquandeng1@gmail.com', id: '2018-11-07-add-column') {
        addColumn(tableName: "NOTIFY_SEND_SETTING") {
            column(name: 'IS_ALLOW_CONFIG', type: 'TINYINT UNSIGNED', defaultValue: "1", remarks: '是否允许配置通知设置。1表示可以，0不可以', afterColumn: 'PM_TYPE')
        }
    }

    changeSet(author: 'longhe6699@gmail.com', id: '2019-08-01-add-column_enabled') {
        addColumn(tableName: "NOTIFY_SEND_SETTING") {
            column(name: 'IS_ENABLED', type: 'TINYINT UNSIGNED', defaultValue: "1", remarks: '是否启用。1表示启用，0不启用', afterColumn: 'IS_ALLOW_CONFIG')
        }
    }

    changeSet(author: 'bg_zyy@foxmail.com', id: '2019-11-15-add-column_enabled') {
        addColumn(tableName: "NOTIFY_SEND_SETTING") {
            column(name: 'WH_TEMPLATE_ID', type: 'BIGINT UNSIGNED', remarks: 'webhook 发送模版 id')
            column(name: 'WH_ENABLED_FLAG', type: 'TINYINT UNSIGNED', remarks: '是否启用WebHook发送')
        }
    }


    changeSet(id: '2019-10-22-notify_send_setting-modify-column', author: 'longhe1996@icloud.com') {
        addColumn(tableName: 'NOTIFY_SEND_SETTING') {
            column(name: 'CATEGORY_CODE', type: 'VARCHAR(32)', remarks: '发送设置类目编码', afterColumn: 'FD_LEVEL', defaultValue: 'default') {
                constraints(nullable: false)
            }
            column(name: 'BACKLOG_FLAG', type: 'TINYINT UNSIGNED', defaultValue: 0, remarks: '是否为待办提醒消息。0：不是（默认）；1：是') {
                constraints(nullable: false)
            }
            column(name: 'EMAIL_ENABLED_FLAG', type: 'TINYINT UNSIGNED', defaultValue: 0, remarks: '是否启用邮件方式发送消息。0：不启用（默认）；1：启用') {
                constraints(nullable: false)
            }
            column(name: 'PM_ENABLED_FLAG', type: 'TINYINT UNSIGNED', defaultValue: 0, remarks: '是否启用站内信方式发送消息。0：不启用（默认）；1：启用') {
                constraints(nullable: false)
            }
            column(name: 'SMS_ENABLED_FLAG', type: 'TINYINT UNSIGNED', defaultValue: 0, remarks: '是否启用短信方式发送消息。0：不启用（默认）；1：启用') {
                constraints(nullable: false)
            }
            column(name: 'WEBHOOK_ENABLED_FLAG', type: 'TINYINT UNSIGNED', defaultValue: 0, remarks: '是否启用WEBHOOK方式发送消息。0：不启用（默认）；1：启用') {
                constraints(nullable: false)
            }
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "UPDATE NOTIFY_SEND_SETTING \n" +
                    "SET BACKLOG_FLAG=CASE WHEN PM_TYPE='notice' THEN 1 ELSE 0 END,\n" +
                    "EMAIL_ENABLED_FLAG =CASE WHEN EMAIL_TEMPLATE_ID IS NULL THEN 0 ELSE 1 END,\n" +
                    "PM_ENABLED_FLAG =CASE WHEN PM_TEMPLATE_ID IS NULL THEN 0 ELSE 1 END,\n" +
                    "SMS_ENABLED_FLAG =CASE WHEN SMS_TEMPLATE_ID IS NULL THEN 0 ELSE 1 END,\n" +
                    "WEBHOOK_ENABLED_FLAG =CASE WHEN WH_TEMPLATE_ID IS NULL OR  WH_ENABLED_FLAG =0 THEN 0 ELSE 1 END"
        }
        /*以下列需要提供template做数据迁移，所以在template.groovy中做删除

        dropColumn(tableName: 'NOTIFY_SEND_SETTING', columnName: 'PM_TYPE')
        dropColumn(tableName: 'NOTIFY_SEND_SETTING', columnName: 'EMAIL_TEMPLATE_ID')
        dropColumn(tableName: 'NOTIFY_SEND_SETTING', columnName: 'SMS_TEMPLATE_ID')
        dropColumn(tableName: 'NOTIFY_SEND_SETTING', columnName: 'PM_TEMPLATE_ID')
        dropColumn(tableName: 'NOTIFY_SEND_SETTING', columnName: 'WH_TEMPLATE_ID')
        dropColumn(tableName: 'NOTIFY_SEND_SETTING', columnName: 'WH_ENABLED_FLAG')*/
    }

    changeSet(author: 'scp', id: '2019-12-17-add-column_edit') {
        addColumn(tableName: "NOTIFY_SEND_SETTING") {
            column(name: 'IS_EDIT', type: 'TINYINT UNSIGNED', remarks: '能够编辑isAllowConfig', defaultValue: 1)
        }
    }

    changeSet(author: 'scp', id: '2020-03-17-rename-column-webhook') {
        renameColumn(columnDataType: 'TINYINT UNSIGNED', newColumnName: "WEBHOOK_OTHER_ENABLED_FLAG", oldColumnName: "WEBHOOK_ENABLED_FLAG", remarks: '是否启用webhook 微信钉钉发送方式', tableName: 'NOTIFY_SEND_SETTING')
        addColumn(tableName: "NOTIFY_SEND_SETTING") {
            column(name: 'WEBHOOK_JSON_ENABLED_FLAG', type: 'TINYINT UNSIGNED', remarks: '是否启用webhook json发送方式', afterColumn: "WEBHOOK_OTHER_ENABLED_FLAG") {
                constraints(nullable: false)
            }
        }
    }
}