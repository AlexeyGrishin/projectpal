package io.github.alexeygrishin.pal.ideaplugin.remote.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/")
public interface PalApi {

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public FunctionsList search(@QueryParam("q") String q, @QueryParam("from") int from, @QueryParam("count") int count);

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public FunctionsList search(@QueryParam("q") String q, @QueryParam("slow") String slow);


    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public FunctionsList search(@QueryParam("q") String q);


    @GET
    @Path("/{id}/{language}")
    @Produces(MediaType.APPLICATION_JSON)
    public FunctionClassResult getImplementation(@PathParam("id") String functionId, @PathParam("language") String language);

    @GET
    @Path("/languages")
    @Produces(MediaType.APPLICATION_JSON)
    public Result getLanguages();



}
