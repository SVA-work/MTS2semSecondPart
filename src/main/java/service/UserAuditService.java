package service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import dto.RequestAddUserAudit;
import dto.RequestReadUserAudit;
import dto.ResponseReadUserAudit;
import entity.UserAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAuditService {

  @Autowired
  protected CqlSession session;

  public void insertUserAction(RequestAddUserAudit requestAddUserAudit) {
    PreparedStatement preparedStatement = session.prepare(
        "INSERT INTO my_keyspace.user_audit (user_id, event_time, event_type, event_details) " +
            "VALUES (?, ?, ?, ?)"
    );

    BoundStatement boundStatement = preparedStatement.bind(
        requestAddUserAudit.getUuid(),
        requestAddUserAudit.getTime(),
        requestAddUserAudit.getEvent_type(),
        requestAddUserAudit.getEvent_details()
    );

    session.execute(boundStatement);
  }

  public ResponseReadUserAudit readUserAction(RequestReadUserAudit requestReadUserAudit) {
    PreparedStatement preparedStatement = session.prepare(
        "SELECT * FROM my_keyspace.user_audit WHERE user_id = ?"
    );

    BoundStatement boundStatement = preparedStatement.bind(
        requestReadUserAudit.getUuid()
    );

    ResultSet result = session.execute(boundStatement);

    ResponseReadUserAudit response = new ResponseReadUserAudit();
    response.setAllUserAudits(result.all().stream()
        .map(row -> new UserAudit(
            row.getUuid("user_id"),
            row.getInstant("event_time"),
            row.getString("event_type"),
            row.getString("event_details")
        ))
        .toList());

    return response;
  }
}