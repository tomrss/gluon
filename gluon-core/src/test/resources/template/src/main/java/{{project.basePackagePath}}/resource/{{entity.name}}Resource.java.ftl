package ${project.basePackage}.resource;

import ${project.basePackage}.domain.${entity.name};
import ${project.basePackage}.service.${entity.name}Service;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/${entity.resourcePath}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ${entity.name}Resource {

    @Inject
    ${entity.name}Service service;

    @GET
    public List<${entity.name}> list() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    public ${entity.name} get(Long id) {
        return service.get(id)
                .orElseThrow(NotFoundException::new);
    }

    @POST
    public Response create(${entity.name} entity) {
        service.create(entity);
        return Response.created(URI.create("/${entity.resourcePath}/" + entity.id)).build();
    }
    
    @PUT
    @Path("/{id}")
    public Response update(Long id, ${entity.name} entity) {
        service.update(id, entity);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public void delete(Long id) {
        service.delete(id);
    }

    @GET
    @Path("/count")
    public Long count() {
        return service.count();
    }
}
