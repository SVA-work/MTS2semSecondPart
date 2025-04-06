package app.dto;

import app.entity.UserAudit;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ResponseReadUserAudit {

  private List<UserAudit> allUserAudits;
}
