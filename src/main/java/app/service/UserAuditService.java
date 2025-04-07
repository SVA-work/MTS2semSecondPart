package app.service;

import app.dto.RequestAddUserAudit;
import app.dto.RequestReadUserAudit;
import app.dto.ResponseReadUserAudit;
import app.entity.UserAudit;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAuditService {

  private final CqlSession session;
  private final PreparedStatement insertStatement;
  private final PreparedStatement selectStatement;

  @Autowired
  public UserAuditService(CqlSession session) {
    this.session = session;
    // Подготавливаем statements один раз при создании сервиса
    this.insertStatement = session.prepare(
        "INSERT INTO my_keyspace.user_audit (user_id, event_time, event_type, event_details) " +
            "VALUES (?, ?, ?, ?)"
    );
    this.selectStatement = session.prepare(
        "SELECT user_id, event_time, event_type, event_details " +
            "FROM my_keyspace.user_audit WHERE user_id = ?"
    );
  }

  public void insertUserAction(RequestAddUserAudit request) {
    BoundStatement boundStatement = insertStatement.bind(
        request.getUuid(),
        request.getTime(),
        request.getEvent_type(),
        request.getEvent_details()
    );
    session.execute(boundStatement);
  }

  public ResponseReadUserAudit readUserAction(RequestReadUserAudit request) {
    BoundStatement boundStatement = selectStatement.bind(request.getUuid());
    ResultSet result = session.execute(boundStatement);

    List<UserAudit> audits = result.all().stream()
        .map(row -> new UserAudit(
            row.getUuid("user_id"),
            row.getInstant("event_time"),
            row.getString("event_type"),
            row.getString("event_details")
        ))
        .collect(Collectors.toList());

    return new ResponseReadUserAudit(audits);
  }
}