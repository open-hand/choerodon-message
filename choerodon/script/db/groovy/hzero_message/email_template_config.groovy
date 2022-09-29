package script.db

databaseChangeLog(logicalFilePath: 'script/db/email_template_config.groovy') {
    changeSet(author: 'scp', id: '2022-09-26-add-email_template_config') {
        createTable(tableName: "EMAIL_TEMPLATE_CONFIG", remarks: "邮件模板自定义配置") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1'){
                constraints(primaryKey: true, primaryKeyName: 'PK_EMAIL_TEMPLATE_CONFIG')
            }
            column(name: 'LOGO', type: 'text', remarks: '邮件logo')
            column(name: 'SLOGAN', type: 'VARCHAR(32)', remarks: '邮件标语')
            column(name: 'FOOTER', type: 'text', remarks: '邮件页脚')
            column(name: 'TENANT_ID', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: false)
            }
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'EMAIL_TEMPLATE_CONFIG', columnNames: 'TENANT_ID', constraintName: 'UK_EMAIL_TEMPLATE_CONFIG_U1')
    }
}