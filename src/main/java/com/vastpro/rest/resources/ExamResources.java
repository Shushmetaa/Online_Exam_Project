package com.vastpro.rest.resources;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.vastpro.servicecall.ExamMaster;
import com.vastpro.servicecall.ExamService;

@Path("/user/exam")
public class ExamResources {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/startexam")
    public Map<String, Object> startExam(
        @FormParam("examId")  String examId,
        @FormParam("partyId") String partyId,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response) {
        return ExamService.startExam(examId, partyId, request, response);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/saveanswer")
    public Map<String, Object> saveAnswer(
        @FormParam("examId")          String examId,
        @FormParam("partyId")         String partyId,
        @FormParam("questionId")      String questionId,
        @FormParam("submittedAnswer") String submittedAnswer,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response) {
        return ExamService.saveAnswer(examId, partyId, questionId, submittedAnswer, request, response);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/submitexam")
    public Map<String, Object> submitExam(
        @FormParam("examId")  String examId,
        @FormParam("partyId") String partyId,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response) {
        return ExamService.submitExam(examId, partyId, request, response);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/results/{examId}/{partyId}/{performanceId}")
    public Map<String, Object> getResults(
        @PathParam("examId")        String examId,
        @PathParam("partyId")       String partyId,
        @PathParam("performanceId") String performanceId,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response) {
        return ExamService.getResults(examId, partyId, performanceId, request, response);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/attempts/{examId}/{partyId}")
    public Map<String, Object> getAttempts(
        @PathParam("examId")  String examId,
        @PathParam("partyId") String partyId,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response) {
        return ExamService.getAttempts(examId, partyId, request, response);
    }
}