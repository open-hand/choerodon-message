package script.db.groovy.hzero_message.hzero_message

databaseChangeLog(logicalFilePath: 'script/db/hmsg_template_arg_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-10-08-hmsg_template_arg_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_template_arg_tl_s', startValue:"1")
        }
        createTable(tableName: "hmsg_template_arg_tl", remarks: "消息模板参数多语言") {
            column(name: "arg_id", type: "bigint",  remarks: "表ID，主键，供其他表做外键")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "参数描述")

        }

        addUniqueConstraint(columnNames:"arg_id,lang",tableName:"hmsg_template_arg_tl",constraintName: "hmsg_template_arg_tl_u1")
    }
}