package script.db.groovy.hzero_message.hzero_message

databaseChangeLog(logicalFilePath: 'script/db/hmsg_template_arg.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-10-08-hmsg_template_arg") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_template_arg_s', startValue:"1")
        }
        createTable(tableName: "hmsg_template_arg", remarks: "消息模板参数") {
            column(name: "arg_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "template_id", type: "bigint",  remarks: "消息模板ID,hmsg_message_template.template_id")  {constraints(nullable:"false")}  
            column(name: "arg_name", type: "varchar(" + 30 * weight + ")",  remarks: "参数名称")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "参数描述")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hmsg_template_arg", indexName: "hmsg_template_arg_n1") {
            column(name: "template_id")
            column(name: "arg_name")
        }

    }
}