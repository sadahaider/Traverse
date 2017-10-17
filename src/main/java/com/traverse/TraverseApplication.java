package com.traverse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.SpringVersion;

import java.util.Date;

@SpringBootApplication
@ComponentScan("com.traverse")
public class TraverseApplication extends SpringBootServletInitializer {

	private static final Log logger = LogFactory.getLog(TraverseApplication.class);

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(TraverseApplication.class);
	}

	public static void main(String[] args) throws Exception {
		logger.info("SPRING VERSION: " + SpringVersion.getVersion());
		SpringApplication.run(TraverseApplication.class, args);
	}

}
