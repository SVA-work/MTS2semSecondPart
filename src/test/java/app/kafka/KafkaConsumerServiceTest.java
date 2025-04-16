package app.kafka;

import app.entity.UserAudit;
import app.service.UserAuditService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@EnableKafka
@EmbeddedKafka(partitions = 1, topics = "test-topic")
@DirtiesContext
class KafkaConsumerServiceTest {

  @TestConfiguration
  static class TestConfig {
    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper()
          .registerModule(new JavaTimeModule());
    }

    @Bean
    public UserAuditService userAuditService() {
      return mock(UserAuditService.class);
    }
  }

  @Autowired
  private KafkaConsumerService kafkaConsumerService;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserAuditService userAuditService;

  @Test
  void shouldConsumeAndProcessValidMessageSuccessfully() throws JsonProcessingException {
    UserAudit testAudit = new UserAudit();
    testAudit.setUuid(UUID.randomUUID());
    testAudit.setTime(Instant.now());
    testAudit.setEventType("LOGIN");
    testAudit.setEventDetails("User logged in");

    String message = objectMapper.writeValueAsString(testAudit);

    kafkaConsumerService.consumeMessage(message, null);

    verify(userAuditService, times(1))
        .insertUserAction(any(UserAudit.class));
  }

  @Test
  void shouldHandleInvalidMessageGracefully() {
    String invalidMessage = "invalid-json-message";

    kafkaConsumerService.consumeMessage(invalidMessage, null);

    verify(userAuditService, never())
        .insertUserAction(any(UserAudit.class));
  }
}