package dto;

import entity.UserAudit;
import lombok.Data;

import java.util.List;

@Data
public class ResponseReadUserAudit {

  private List<UserAudit> allUserAudits;
}
