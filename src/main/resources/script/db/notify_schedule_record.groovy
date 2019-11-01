package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify_schedule_record.groovy') {
    changeSet(author: 'foxnotail@foxmail.com', id: '2019-11-01-notify_schedule_record') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'NOTIFY_SCHEDULE_RECORD_S', startValue: "1")
        }
        createTable(tableName: "NOTIFY_SCHEDULE_RECORD", remarks: "消息与定时任务关系表") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表 ID，主键，单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_NOTIFY_SEND_SETTING_CATEGORY')
            }
            column(name: 'TASK_ID', type: 'BIGINT UNSIGNED', remarks: '任务id') {
                constraints(nullable: false)
            }
            column(name: 'SCHEDULE_NOTICE_CODE', type: 'varchar(255)', remarks: '定时消息编码') {
                constraints(nullable: false,unique: true,uniqueConstraintName: 'UK_NOTIFY_SHEDULE_RECORD_U1')
            }
            column(name: 'NOTICE_CONTENT', type: 'varchar(255)', remarks: '消息内容') {
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