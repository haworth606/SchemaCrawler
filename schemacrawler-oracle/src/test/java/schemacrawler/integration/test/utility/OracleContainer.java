package schemacrawler.integration.test.utility;

import static us.fatehi.utility.Utility.isBlank;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * https://github.com/testcontainers/testcontainers-java/blob/master/modules/oracle-xe/src/main/java/org/testcontainers/containers/OracleContainer.java
 */
public class OracleContainer extends JdbcDatabaseContainer<OracleContainer> {

  public static final String NAME = "oracle";
  private static final DockerImageName DEFAULT_IMAGE_NAME =
      DockerImageName.parse("gvenzl/oracle-xe");

  static final String DEFAULT_TAG = "18.4.0-slim";
  static final String IMAGE = DEFAULT_IMAGE_NAME.getUnversionedPart();

  private static final int ORACLE_PORT = 1521;
  private static final int APEX_HTTP_PORT = 8080;

  private static final int DEFAULT_STARTUP_TIMEOUT_SECONDS = 240;
  private static final int DEFAULT_CONNECT_TIMEOUT_SECONDS = 120;
  private static final List<String> ORACLE_SYSTEM_USERS = Arrays.asList("system", "sys");

  private String username = "test";
  private String password = "test";

  /** @deprecated use {@link OracleContainer(DockerImageName)} instead */
  @Deprecated
  public OracleContainer() {
    this(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
  }

  public OracleContainer(final DockerImageName dockerImageName) {
    super(dockerImageName);
    preconfigure();
  }

  public OracleContainer(final Future<String> dockerImageName) {
    super(dockerImageName);
    preconfigure();
  }

  public OracleContainer(final String dockerImageName) {
    this(DockerImageName.parse(dockerImageName));
  }

  @Override
  public String getDriverClassName() {
    return "oracle.jdbc.OracleDriver";
  }

  @Override
  public String getJdbcUrl() {
    return "jdbc:oracle:thin:"
        + getUsername()
        + "/"
        + getPassword()
        + "@"
        + getHost()
        + ":"
        + getOraclePort()
        + "/xepdb1";
  }

  @Override
  public Set<Integer> getLivenessCheckPortNumbers() {
    return new HashSet<>(Arrays.asList(ORACLE_PORT));
  }

  public Integer getOraclePort() {
    return getMappedPort(ORACLE_PORT);
  }

  @Override
  public String getPassword() {
    return password;
  }

  @SuppressWarnings("SameReturnValue")
  public String getSid() {
    return "xe";
  }

  @Override
  public String getTestQueryString() {
    return "SELECT 1 FROM DUAL";
  }

  @Override
  public String getUsername() {
    return username;
  }

  @SuppressWarnings("unused")
  public Integer getWebPort() {
    return getMappedPort(APEX_HTTP_PORT);
  }

  @Override
  public OracleContainer withPassword(final String password) {
    if (isBlank(password)) {
      throw new IllegalArgumentException("Password cannot be null or empty");
    }
    this.password = password;
    return self();
  }

  @Override
  public OracleContainer withUrlParam(final String paramName, final String paramValue) {
    throw new UnsupportedOperationException("The OracleDb does not support this");
  }

  @Override
  public OracleContainer withUsername(final String username) {
    if (isBlank(username)) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    if (ORACLE_SYSTEM_USERS.contains(username.toLowerCase())) {
      throw new IllegalArgumentException("Username cannot be one of " + ORACLE_SYSTEM_USERS);
    }
    this.username = username;
    return self();
  }

  @Override
  protected void configure() {
    withEnv("ORACLE_PASSWORD", password);
    withEnv("APP_USER", username);
    withEnv("APP_USER_PASSWORD", password);
  }

  private void preconfigure() {
    withStartupTimeoutSeconds(DEFAULT_STARTUP_TIMEOUT_SECONDS);
    withConnectTimeoutSeconds(DEFAULT_CONNECT_TIMEOUT_SECONDS);
    addExposedPorts(ORACLE_PORT, APEX_HTTP_PORT);
  }
}
