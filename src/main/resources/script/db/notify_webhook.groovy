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

    changeSet(id: '2019-10-22-notify_webhook-modify-column', author: 'longhe1996@icloud.com') {
        addColumn(tableName: 'NOTIFY_WEBHOOK') {
            column(name: 'NAME', type: 'VARCHAR(255)', remarks: 'WEBHOOK名称', afterColumn: 'WEBHOOK_NAME') {
                constraints(nullable: false)
            }
            column(name: 'TYPE', type: 'VARCHAR(255)', defaultValue: 'Json', remarks: 'WEBHOOK类型。包括DingTalk,WeChat,Json(默认)', afterColumn: 'WEBHOOK_TYPE') {
                constraints(nullable: false)
            }
        }
        sql(stripComments: true, splitStatements: false, endDelimiter: ';') {
            "UPDATE NOTIFY_WEBHOOK SET NAME=WEBHOOK_NAME,TYPE=WEBHOOK_TYPE"
        }
        dropColumn(tableName: 'NOTIFY_WEBHOOK', columnName: 'WEBHOOK_NAME')
        dropColumn(tableName: 'NOTIFY_WEBHOOK', columnName: 'WEBHOOK_TYPE')
        addUniqueConstraint(tableName: 'NOTIFY_WEBHOOK', columnNames: 'PROJECT_ID,WEBHOOK_PATH', constraintName: "UK_NOTIFY_WEBHOOK_U1")
    }

    changeSet(id: '2019-10-22-notify_webhook-add-default-value', author: 'longhe1996@icloud.com') {
        addDefaultValue(tableName: 'NOTIFY_WEBHOOK', columnName: 'ENABLE_FLAG', defaultValue: 1)
    }

    changeSet(id: '2019-11-01-notify_webhook-add-column', author: 'longhe1996@icloud.com') {
        addColumn(tableName: 'NOTIFY_WEBHOOK') {
            column(name: 'SECRET', type: 'VARCHAR(255)', remarks: '钉钉的加签密钥（密钥，机器人安全设置页面，加签一栏下面显示的SEC开头的字符串）', afterColumn: 'WEBHOOK_PATH')
        }
    }
}