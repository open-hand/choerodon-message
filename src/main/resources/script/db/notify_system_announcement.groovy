package script.db

databaseChangeLog(logicalFilePath: 'script/db/notify_system_announcement.groovy') {
    changeSet(author: 'youquandeng1@gmail.com', id: '2018-12-05-notify-system-announcement') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'NOTIFY_SYSTEM_ANNOUNCEMENT_S', startValue: "1")
        }
        createTable(tableName: "NOTIFY_SYSTEM_ANNOUNCEMENT") {
            column(name: 'ID', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_NOTIFY_SYSTEM_ANNOUNCEMENT')
            }
            column(name: 'TITLE', type: 'VARCHAR(64)', remarks: '公告标题')
            column(name: 'CONTENT', type: 'TEXT', remarks: '公告内容')
            column(name: "SEND_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: '发送时间')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT UNSIGNED", defaultValue: "1") {
                constraints(nullable: true)
            }
            column(name: "CREATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT UNSIGNED", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: 'longhe1996@icloud.com', id: '2018-12-17-add-column-is_send_notices') {
        addColumn(tableName: "NOTIFY_SYSTEM_ANNOUNCEMENT") {
            column(name: "IS_SEND_NOTICES", type: "TINYINT UNSIGNED", defaultValue: "1", remarks: '是否发送站内信。1.发送站内信，0：不发送站内信', afterColumn: 'SEND_DATE') {
                constraints(nullable: false)
            }
            column(name: "STATUS", type: "VARCHAR(16)", remarks: '系统公告的状态：COMPLETED（已完成）WAITING（等待发送）', afterColumn: 'IS_SEND_NOTICES')
            column(name: 'SCHEDULE_TASK_ID', type: 'BIGINT UNSIGNED', remarks: '公告关联任务ID', afterColumn: 'STATUS')
        }
    }
}