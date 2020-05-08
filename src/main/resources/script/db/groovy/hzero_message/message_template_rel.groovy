package script.db

databaseChangeLog(logicalFilePath: 'script/db/message_template_rel.groovy') {
    changeSet(author: 'scp', id: '2020-05-08-add-message_template_rel') {
        createTable(tableName: "MESSAGE_TEMPLATE_REL") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'MESSAGE_CODE', type: 'VARCHAR(64)', remarks: '消息code') {
                constraints(nullable: false)
            }
            column(name: 'SEND_TYPE', type: 'VARCHAR(20)', remarks: '发送类型') {
                constraints(nullable: false)
            }
            column(name: 'TEMPLATE_ID', type: 'BIGINT UNSIGNED', remarks: '消息模板Id')
            column(name: 'ENABLED_FLAG', type: 'TINYINT UNSIGNED', defaultValue: "1", remarks: '是否启用。1禁用，0未禁用')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }


}