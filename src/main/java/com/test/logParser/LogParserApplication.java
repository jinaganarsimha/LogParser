package com.test.logParser;

import com.test.logParser.utilityImpl.ParserImplV1;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.IOException;

@SpringBootApplication
public class LogParserApplication {

	@Autowired
	private ParserImplV1 parserImplV1;

	@Value("${log.filepath}")
	private String filePath;
	private final Logger logger = LogManager.getLogger(LogParserApplication.class);
	/**
	 * Calls default parser to read file
	 * @throws IOException
	 */
	@PostConstruct
	public void readFile() throws IOException {
		logger.info(String.format("Trying to read file located at %s",filePath));
		parserImplV1.readFile(filePath);
	}

	public static void main(String[] args) throws IOException {
		SpringApplication.run(LogParserApplication.class, args);
	}

}
