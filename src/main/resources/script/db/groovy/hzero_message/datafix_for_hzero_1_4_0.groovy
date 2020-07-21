package script.db

databaseChangeLog(logicalFilePath: 'script/db/datafix_for_hzero_1_4_0.groovy') {
    changeSet(author: 'wanghao', id: '2020-07-21-data-fix') {
        sql("""
            UPDATE hmsg_email_filter hef
            SET hef.tenant_id = ( SELECT hes.tenant_id FROM hmsg_email_server hes WHERE hes.server_id = hef.server_id)
            WHERE hef.server_id in ( SELECT server_id FROM hmsg_email_server);

            UPDATE hmsg_message_event hme
            SET hme.tenant_id = ( SELECT hts.tenant_id FROM hmsg_template_server hts WHERE hts.temp_server_id = hme.temp_server_id)
            WHERE hme.temp_server_id in ( SELECT temp_server_id FROM hmsg_template_server);

            UPDATE hmsg_receive_config_tl hrct
            SET hrct.tenant_id = ( SELECT hrc.tenant_id FROM hmsg_receive_config hrc WHERE hrct.receive_id = hrc.receive_id)
            WHERE hrct.receive_id in ( SELECT receive_id FROM hmsg_receive_config);

            UPDATE hmsg_template_arg hta
            SET hta.tenant_id = ( SELECT hmt.tenant_id FROM hmsg_message_template hmt WHERE hta.template_id = hmt.template_id)
            WHERE hta.template_id in ( SELECT template_id FROM hmsg_message_template);

            UPDATE hmsg_template_arg_tl htat
            SET htat.tenant_id = ( SELECT hta.tenant_id FROM hmsg_template_arg hta WHERE hta.arg_id = htat.arg_id)
            WHERE htat.arg_id in ( SELECT arg_id FROM hmsg_template_arg);

            UPDATE hmsg_template_server_line htsl
            SET htsl.tenant_id = ( SELECT hts.tenant_id FROM hmsg_template_server hts WHERE hts.temp_server_id = htsl.temp_server_id)
            WHERE htsl.temp_server_id in ( SELECT temp_server_id FROM hmsg_template_server);
            """)
    }
}