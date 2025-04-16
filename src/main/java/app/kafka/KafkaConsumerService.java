package app.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import app.entity.UserAudit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import app.service.UserAuditService;

import java.time.Instant;
import java.util.UUID;

@Service
public class KafkaConsumerService {
  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

  private final ObjectMapper objectMapper;
  private final UserAuditService userAuditService;

  public KafkaConsumerService(ObjectMapper objectMapper,
                              UserAuditService userAuditService) {
    this.objectMapper = objectMapper;
    this.userAuditService = userAuditService;
  }

  @KafkaListener(topics = "${topic-to-consume-message}")
  public void consumeMessage(String message, Acknowledgment acknowledgment) {
    try {
      UserAudit userAudit = objectMapper.readValue(message, UserAudit.class);
      LOGGER.info("Received audit message: {}", userAudit);

      userAuditService.insertUserAction(userAudit);

      LOGGER.info("Audit record saved successfully for user: {}", userAudit.getUuid());

      acknowledgment.acknowledge();
    } catch (JsonProcessingException e) {
      LOGGER.error("Error processing Kafka message: {}", message, e);
    } catch (Exception e) {
      LOGGER.error("Unexpected error while processing audit message", e);
    }
  }
}