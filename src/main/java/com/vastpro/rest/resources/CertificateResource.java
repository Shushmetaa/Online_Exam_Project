package com.vastpro.rest.resources;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

import com.vastpro.servicecall.UserMaster;

public class CertificateResource {
    @GET
    @Path("/certificate")
    @Produces("application/pdf")
    public Object generateCertificate(@QueryParam("partyId") String partyId,@QueryParam("examId") String examId,@Context 
    		HttpServletRequest request, @Context HttpServletResponse response) {
    	return UserMaster.getCertificate(partyId,examId,request,response);
    }
}
