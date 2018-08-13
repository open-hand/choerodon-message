package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify-variable.groovy') {
    changeSet(author: 'jcalaz@163.com', id: '2018-08-09-add-notify-variable') {
        createTable(tableName: "notify_variable") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }

            column(name: 'name', type: 'VARCHAR(64)', remarks: '变量名') {
                constraints(nullable: false)
            }

            column(name: 'value', type: 'TEXT', remarks: '变量值') {
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