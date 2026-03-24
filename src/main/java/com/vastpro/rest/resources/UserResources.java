package com.vastpro.rest.resources;

import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.DelegatorFactory;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceContainer;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResources {

    @Context
    private HttpServletRequest request;

    @Context
    private ServletContext servletContext;  // ← ADD THIS

    // Helper method to get Delegator
    private Delegator getDelegator() {
        Delegator delegator = (Delegator) servletContext.getAttribute("delegator");
        if (delegator == null) {
            // Fallback — get directly from factory
            delegator = DelegatorFactory.getDelegator("default");
        }
        return delegator;
    }

    // Helper method to get Dispatcher
    private LocalDispatcher getDispatcher() {
        LocalDispatcher dispatcher = 
            (LocalDispatcher) servletContext.getAttribute("dispatcher");
        if (dispatcher == null) {
            // Fallback — get directly from ServiceContainer
            dispatcher = ServiceContainer.getLocalDispatcher(
                "exam",   // must match localDispatcherName in web.xml
                getDelegator()
            );
        }
        return dispatcher;
    }

    @POST
    @Path("/register")
    public Response createStudent(Map<String, Object> input) {
        try {
            Delegator delegator   = getDelegator();
            LocalDispatcher dispatcher = getDispatcher();

            if (dispatcher == null) {
                return Response.status(500)
                    .entity(Map.of("error", "Dispatcher is still null"))
                    .build();
            }

            GenericValue userLogin = EntityQuery.use(delegator)
                    .from("UserLogin")
                    .where("userLoginId", "admin")
                    .queryOne();

            input.put("userLogin", userLogin);

            Map<String, Object> result =
                dispatcher.runSync("registerUser", input);

            return Response.ok(result).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}