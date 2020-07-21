package script.db

databaseChangeLog(logicalFilePath: 'script/db/datafix_for_hzero_1_4_0.groovy') {
    changeSet(author: 'wanghao', id: '2020-07-21-data-fix') {
        sql("""
            UPDATE hmsg_email_filter hef
            SET hef.tenant_id = ( SELECT hes.tenant_id FROM hmsg_email_server hes WHERE hes.server_id = hef.server_id)
            WHERE hef.server_id in ( SELECT server_id FROM hmsg_email_server);
            """)
    }
}