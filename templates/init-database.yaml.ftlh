databaseChangeLog:
<#list entities as entity>
- changeSet:
    id: create-table-${entity.name}
    author: gluon
    changes:
    - createSequence:
        sequenceName: ${entity.sequence}
        incrementBy: 50
    - createTable:
        tableName: ${entity.name}
        columns:
        - column:
            name: id
            type: bigint # TODO this is hardcoded in template
            valueSequenceNext: ${entity.sequence}
            constraints:
                nullable: false
                unique: true
                primaryKey: true
                primaryKeyName: ${entity.primaryKeyName}
        <#list entity.fields as field>
        - column:
            name: ${field.column}
            type: ${field.sqlType}
            <#if !field.nullable || field.unique>
            constraints:
              nullable: ${field.nullable?then('true', 'false')}
              unique: ${field.unique?then('true', 'false')}
            </#if>
        </#list>
        <#list entity.relations as relation>
        - column:
            name: ${relation.joinColumn}
            type: bigint # todo hardcoded in template
            constraints:
              foreignKeyName: ${relation.foreignKeyName}
              references: ${relation.targetEntity.table}(id)
              nullable: ${relation.nullable?then('true', 'false')}
              unique: ${relation.unique?then('true', 'false')}
        </#list>
    <#list entity.indexes as index>
    - createIndex:
        columns:
        <#list index.fields as field>
        - column:
           name: ${field.column}
        </#list>
        indexName: ${index.name}
        tableName: ${entity.table}
        unique: ${index.unique?then('true', 'false')}
    </#list>
</#list>