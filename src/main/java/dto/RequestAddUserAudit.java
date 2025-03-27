package dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class RequestAddUserAudit {

  private UUID uuid;
  private Instant time;
  private String event_type;
  private String event_details;

  public RequestAddUserAudit(UUID uuid, Instant time, String event_type, String event_details) {
    this.uuid = uuid;
    this.time = time;
    this.event_type = event_type;
    this.event_details = event_details;
  }
}
