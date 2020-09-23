package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_receive_config_tl.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hmsg_receive_config_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hmsg_receive_config_tl", remarks: "接收配置多语言") {
            column(name: "receive_id", type: "bigint",  remarks: "hmsg_receive_config主键")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 16 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "receive_name", type: "varchar(" + 120 * weight + ")",  remarks: "接收配置名称(TL)")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"receive_id,lang",tableName:"hmsg_receive_config_tl",constraintName: "hmsg_receive_config_tl_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hmsg_receive_config_tl") {
        addColumn(tableName: 'hmsg_receive_config_tl') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
    changeSet(author: 'wanghao', id: '2020-07-21-data-fix') {
        preConditions(onFail: "MARK_RAN") {
            tableExists(tableName: "hmsg_receive_config")
        }
        sql("""
            UPDATE hmsg_receive_config_tl hrct
            SET hrct.tenant_id = ( SELECT hrc.tenant_id FROM hmsg_receive_config hrc WHERE hrct.receive_id = hrc.receive_id)
            WHERE hrct.receive_id in ( SELECT receive_id FROM hmsg_receive_config);
            """)
    }

    changeSet(author: "wx@hand-china.com",id: "2020-09-22-fix-receive_config_tl"){
        sql("""
            DELETE 
            FROM
              hmsg_receive_config_tl;

            INSERT INTO hmsg_receive_config_tl ( receive_id, lang, receive_name, tenant_id ) SELECT
              receive_id,
              'zh_CN',
              receive_name,
              tenant_id 
            FROM
              hmsg_receive_config;

            INSERT INTO hmsg_receive_config_tl ( receive_id, lang, receive_name, tenant_id ) SELECT
              receive_id,
              'en_US',
              "default",
              tenant_id 
            FROM
              hmsg_receive_config
        """)
    }
}