package com.vastpro.rest;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.core.util.JacksonFeature;

public class RestApplication extends ResourceConfig {

	public RestApplication() {
		packages("com.vastpro.rest.resources");
		register(JacksonFeature.class);
		register(MultiPartFeature.class);
	}

}