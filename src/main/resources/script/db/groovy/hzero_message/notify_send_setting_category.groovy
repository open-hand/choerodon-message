package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify_send_setting_category.groovy') {
    changeSet(author: 'longhe1996@icloud.com', id: '2019-10-22-notify_send_setting_category') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'NOTIFY_SEND_SETTING_CATEGORY_S', startValue: "1")
        }
        createTable(tableName: "NOTIFY_SEND_SETTING_CATEGORY", remarks: "发送设置类目表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表 ID，主键，单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_NOTIFY_SEND_SETTING_CATEGORY')
            }
            column(name: 'CODE', type: 'varchar(32)', remarks: '发送设置类目编码') {
                constraints(nullable: false,unique: true,uniqueConstraintName: 'UK_NOTIFY_SEND_SETTING_CATEGORY_U1')
            }
            column(name: 'NAME', type: 'varchar(64)', remarks: '发送设置类目名称') {
                constraints(nullable: false,unique: true,uniqueConstraintName: 'UK_NOTIFY_SEND_SETTING_CATEGORY_U2')
            }
            column(name: 'DESCRIPTION', type: 'varchar(255)', remarks: '发送设置类目描述') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}