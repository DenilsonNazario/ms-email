package com.ms.email.consumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.email.dtos.EmailDto;
import com.ms.email.models.EmailModel;
import com.ms.email.services.EmailService;

@Component
public class EmailConsumer {

	@Autowired
	EmailService emailService;
	
	private static Logger logger = LoggerFactory.getLogger(EmailConsumer.class);

	@JmsListener(destination = "${queues.name}")
	public void listen(@Payload String emailDtoStr) {
		
		logger.info("EmailConsumer.listen {}", emailDtoStr);
		
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			EmailDto emailDto = objectMapper.readValue(emailDtoStr, EmailDto.class);
			EmailModel emailModel = new EmailModel();
			BeanUtils.copyProperties(emailDto, emailModel);
			emailService.sendEmail(emailModel);
			logger.info("EmailConsumer.listen {}", "Email Status: " + emailModel.getStatusEmail());

		} catch (JsonMappingException e) {
			logger.error("EmailConsumer.listen {}", e.getMessage());
		} catch (JsonProcessingException e) {
			logger.error("EmailConsumer.listen {}", e.getMessage());
		}
	}

}
