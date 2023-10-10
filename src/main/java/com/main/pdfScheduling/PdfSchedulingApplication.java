package com.main.pdfScheduling;

import java.io.IOException;
import java.time.LocalDateTime;

import org.apache.logging.log4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.main.pdfScheduling.Service.PDFService;

@EnableScheduling
@SpringBootApplication
public class PdfSchedulingApplication {

	@Autowired
	private PDFService pdfService;

	private final static Logger logger = LogManager.getLogger(PdfSchedulingApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(PdfSchedulingApplication.class, args);
		logger.info("Test logger.");
	}

	@Scheduled(fixedDelayString = "${scheduled_repeat_time}")
	public void scheduleSendDataWithWS() throws InterruptedException {
		logger.info("Injected Scheduled at " + LocalDateTime.now());
		try {
			pdfService.convertPdfsInFolder();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
