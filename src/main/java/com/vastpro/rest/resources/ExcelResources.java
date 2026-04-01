package com.vastpro.rest.resources;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.vastpro.servicecall.ExcelUpload;

@Path("/admin/excel")
public class ExcelResources {

	@POST
	@Path("/upload/{examId}")
	@Consumes("multipart/form-data")
	public Map<String, Object> upload(@PathParam("examId") String examId,  @FormDataParam("file") InputStream fileInputStream, @Context HttpServletRequest request, @Context HttpServletResponse response){
		
		return ExcelUpload.uploadQuestions(examId, fileInputStream, request, response);
		
	}
	
}
