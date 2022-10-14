package ${project.basePackage}.service;

import ${project.basePackage}.domain.${entity.name};

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class ${entity.name}Service {

    public List<${entity.name}> getAll() {
        return ${entity.name}.listAll();
    }

    public Optional<${entity.name}> get(Long id) {
        return ${entity.name}.<${entity.name}>findByIdOptional(id);
    }

    public void create(${entity.name} entity) {
        entity.persist();
    }

    public void update(Long id, ${entity.name} entity) {
        final ${entity.name} existing = ${entity.name}.<${entity.name}>findByIdOptional(id)
                .orElseThrow(NotFoundException::new);

        <#list entity.fields as field>
        Optional.ofNullable(entity.${field.name}).ifPresent(it -> existing.${field.name} = it);
        </#list>
        <#list entity.relations as relation>
        Optional.ofNullable(entity.${relation.name}).ifPresent(it -> existing.${relation.name} = it);
        </#list>
    }

    public void delete(Long id) {
        ${entity.name}.findByIdOptional(id)
                .orElseThrow(NotFoundException::new)
                .delete();
    }

    public long count() {
        return ${entity.name}.count();
    }
}
