package app.dbconfiguration;

import com.datastax.oss.driver.api.core.CqlSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class ScyllaConfig {

  @Bean
  public CqlSession cqlSession() {
    CqlSession session = CqlSession.builder()
        .addContactPoint(InetSocketAddress.createUnresolved("127.0.0.1", 9042))
        .withLocalDatacenter("datacenter1")
        .build();

    initializeSchema(session);

    return CqlSession.builder()
        .addContactPoint(InetSocketAddress.createUnresolved("127.0.0.1", 9042))
        .withLocalDatacenter("datacenter1")
        .withKeyspace("my_keyspace")
        .build();
  }

  private void initializeSchema(CqlSession session) {
    session.execute(
        "CREATE KEYSPACE IF NOT EXISTS my_keyspace " +
            "WITH replication = {'class': 'NetworkTopologyStrategy', 'datacenter1': 1}"
    );

    session.execute(
        "CREATE TABLE IF NOT EXISTS my_keyspace.user_audit (" +
            "    user_id UUID," +
            "    event_time TIMESTAMP," +
            "    event_type TEXT," +
            "    event_details TEXT," +
            "    PRIMARY KEY ((user_id), event_time)" +
            ") WITH CLUSTERING ORDER BY (event_time DESC)" +
            "   AND default_time_to_live = 31536000;"
    );
  }
}