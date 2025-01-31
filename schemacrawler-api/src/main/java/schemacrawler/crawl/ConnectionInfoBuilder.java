/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Logger;

import schemacrawler.schema.ConnectionInfo;

public final class ConnectionInfoBuilder {

  private static final Logger LOGGER = Logger.getLogger(ConnectionInfoBuilder.class.getName());

  public static ConnectionInfoBuilder builder(final Connection connection) {
    return new ConnectionInfoBuilder(connection);
  }

  private final Connection connection;

  private ConnectionInfoBuilder(final Connection connection) {
    this.connection = requireNonNull(connection, "No connection provided");
  }

  public ConnectionInfo build() throws SQLException {

    final DatabaseMetaData dbMetaData = connection.getMetaData();

    final ConnectionInfo connectionInfo =
        new ImmutableConnectionInfo(
            dbMetaData.getDatabaseProductName(),
            dbMetaData.getDatabaseProductVersion(),
            dbMetaData.getURL(),
            dbMetaData.getUserName(),
            dbMetaData.getDriverName(),
            dbMetaData.getDriverVersion(),
            dbMetaData.getDriverMajorVersion(),
            dbMetaData.getDriverMinorVersion(),
            dbMetaData.getJDBCMajorVersion(),
            dbMetaData.getJDBCMinorVersion());

    return connectionInfo;
  }
}
