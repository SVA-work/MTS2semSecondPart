package dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RequestReadUserAudit {

  private UUID uuid;

  public RequestReadUserAudit(UUID uuid) {
    this.uuid = uuid;
  }
}