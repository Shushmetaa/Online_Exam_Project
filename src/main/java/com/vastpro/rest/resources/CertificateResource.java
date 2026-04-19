package com.vastpro.rest.resources;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.vastpro.servicecall.UserMaster;

@Path("/certificate")
public class CertificateResource {

    // GET /exam/api/certificate/exams/{partyId}
    @GET
    @Path("/exams/{partyId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getExamsForParty(
            @PathParam("partyId") String partyId,
            @Context HttpServletRequest request) {

        return UserMaster.getPassedExams(partyId, request);
    }

    // POST /exam/api/certificate/download
    @POST
    @Path("/download")
    @Produces("application/pdf")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response downloadCertificate(
            @FormParam("examId") String examId,
            @FormParam("partyId") String partyId,
            @Context HttpServletRequest request) {

        return UserMaster.downloadCertificate(examId, partyId, request);
    }
}