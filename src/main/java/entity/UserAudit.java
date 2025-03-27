package entity;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class UserAudit {
  private UUID uuid;
  private Instant time;
  private String eventType;
  private String eventDetails;

  public UserAudit(UUID uuid, Instant time, String eventType, String eventDetails) {
    this.uuid = uuid;
    this.time = time;
    this.eventType = eventType;
    this.eventDetails = eventDetails;
  }
}
