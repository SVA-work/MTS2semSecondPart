package service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import dto.RequestAddUserAudit;
import dto.RequestReadUserAudit;
import dto.ResponseReadUserAudit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.aggregator.ArgumentAccessException;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserAuditServiceTest {

  @Test
  void insertUserAction_Success() {
    CqlSession session = Mockito.mock(CqlSession.class);
    PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
    BoundStatement boundStatement = Mockito.mock(BoundStatement.class);

    when(session.prepare(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.bind(
        Mockito.any(UUID.class),
        Mockito.any(Instant.class),
        anyString(),
        anyString()
    )).thenReturn(boundStatement);

    UserAuditService service = new UserAuditService();
    service.session = session;

    RequestAddUserAudit request = new RequestAddUserAudit(
        UUID.randomUUID(),
        Instant.now(),
        "LOGIN",
        "User logged in"
    );

    service.insertUserAction(request);

    Mockito.verify(session).execute(boundStatement);
  }

  @Test
  void insertUserAction_Failure() {
    CqlSession session = Mockito.mock(CqlSession.class);
    when(session.prepare(anyString()))
        .thenThrow(new ArgumentAccessException("Invalid argument"));

    UserAuditService service = new UserAuditService();
    service.session = session;

    RequestAddUserAudit request = new RequestAddUserAudit(
        null,
        null,
        null,
        null
    );

    assertThrows(ArgumentAccessException.class, () -> service.insertUserAction(request));
  }


  @Test
  void readUserAction_Success() {
    CqlSession session = Mockito.mock(CqlSession.class);
    PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
    BoundStatement boundStatement = Mockito.mock(BoundStatement.class);
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    Row row = Mockito.mock(Row.class);

    UUID userId = UUID.randomUUID();
    Instant eventTime = Instant.now();

    when(session.prepare(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.bind(userId)).thenReturn(boundStatement);
    when(session.execute(boundStatement)).thenReturn(resultSet);
    when(resultSet.all()).thenReturn(List.of(row));
    when(row.getUuid("user_id")).thenReturn(userId);
    when(row.getInstant("event_time")).thenReturn(eventTime);
    when(row.getString("event_type")).thenReturn("LOGIN");
    when(row.getString("event_details")).thenReturn("User logged in");

    UserAuditService service = new UserAuditService();
    service.session = session;

    RequestReadUserAudit request = new RequestReadUserAudit(userId);

    ResponseReadUserAudit response = service.readUserAction(request);

    assertEquals(1, response.getAllUserAudits().size());
    assertEquals(userId, response.getAllUserAudits().get(0).getUuid());
  }

  @Test
  void readUserAction_NotFound() {
    CqlSession session = Mockito.mock(CqlSession.class);
    PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
    BoundStatement boundStatement = Mockito.mock(BoundStatement.class);
    ResultSet resultSet = Mockito.mock(ResultSet.class);

    when(session.prepare(anyString())).thenReturn(preparedStatement);
    when(preparedStatement.bind(any(UUID.class))).thenReturn(boundStatement);
    when(session.execute(boundStatement)).thenReturn(resultSet);
    when(resultSet.all()).thenReturn(Collections.emptyList());

    UserAuditService service = new UserAuditService();
    service.session = session;

    RequestReadUserAudit request = new RequestReadUserAudit(UUID.randomUUID());
    ResponseReadUserAudit response = service.readUserAction(request);

    assertNotNull(response);
    assertTrue(response.getAllUserAudits().isEmpty());
  }
}