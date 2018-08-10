package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify-business-type.groovy') {
    changeSet(author: 'jcalaz@163.com', id: '2018-08-09-add-notify-business-type') {
        createTable(tableName: "notify_business_type") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(64)', remarks: '消息业务类型名称。例如验证码,用户激活等') {
                constraints(nullable: false)
            }
            column(name: 'description', type: 'VARCHAR(255)', remarks: '消息业务类型描述') {
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