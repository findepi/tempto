/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.trino.tempto.fulfillment.table

import io.trino.tempto.fulfillment.table.jdbc.RelationalDataSource
import io.trino.tempto.fulfillment.table.jdbc.RelationalTableDefinition
import io.trino.tempto.internal.configuration.EmptyConfiguration
import io.trino.tempto.internal.fulfillment.table.TableNameGenerator
import io.trino.tempto.internal.fulfillment.table.jdbc.JdbcTableManager
import io.trino.tempto.query.QueryExecutor
import spock.lang.Specification

import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.ResultSet
import java.sql.ResultSetMetaData

class JdbcTableManagerTest
        extends Specification
{
    static RelationalTableDefinition tableDefinition
    static JdbcTableManager tableManager
    static String tableName

    def setup()
    {
        tableName = "name"
        tableDefinition = RelationalTableDefinition.relationalTableDefinition(tableName, "CREATE TABLE %NAME%(col1 INT)",
                { Collections.<List<Object>> emptyList().iterator() } as RelationalDataSource)

        def mockExecutor = Mock(QueryExecutor)
        def mockConnection = Mock(Connection)
        def mockMetadata = Mock(DatabaseMetaData)
        mockExecutor.connection >> mockConnection
        mockConnection.getMetaData() >> mockMetadata

        def mockResultSet = Mock(ResultSet)
        mockMetadata.getTables(_, _, _, _) >> mockResultSet
        mockResultSet.getMetaData() >> Mock(ResultSetMetaData)
        tableManager = new JdbcTableManager(mockExecutor, new TableNameGenerator(), "db_name", EmptyConfiguration.emptyConfiguration())
    }

    def 'table without rows does not throw'()
    {
        when:
        tableManager.createImmutable(tableDefinition, TableHandle.tableHandle(tableName))

        then:
        noExceptionThrown()
    }
}
