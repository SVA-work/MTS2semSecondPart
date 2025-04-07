package app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAudit {
  private UUID uuid;
  private Instant time;
  private String eventType;
  private String eventDetails;
}
