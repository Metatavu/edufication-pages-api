package fi.metatavu.edufication.pages.api.liquibase.changes

import liquibase.database.Database
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.CustomChangeException
import org.apache.commons.lang3.StringUtils

/**
 * Custom Liquibase migrations for adding page parent ids
 */
class PageParentPagesChange: AbstractCustomTaskChange() {

    override fun getConfirmationMessage(): String {
        return "Migrated parentIds for pages"
    }

    override fun execute(database: Database?) {
        database ?: throw CustomChangeException("No database connection")
        val connection: JdbcConnection = database.connection as JdbcConnection

        try {
            connection.prepareStatement("SELECT id, path FROM page").use { statement ->
                statement.executeQuery().use { resultSet ->
                    while (resultSet.next()) {
                        val id = resultSet.getBytes(1)
                        val path = resultSet.getString(2)
                        val parentPath = getParentPath(path = path)
                        val parentId = if (parentPath != null) getPageIdByPath(connection = connection, path = parentPath) else null
                        if (parentId != null) {
                            updatePageParentId(connection = connection, id = id, parentId = parentId)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw CustomChangeException(e)
        }
    }

    /**
     * Finds page id for a path
     *
     * @param connection JDBC connection
     * @param path path
     * @return page id
     */
    private fun getPageIdByPath(connection: JdbcConnection, path: String): ByteArray? {
        connection.prepareStatement("SELECT id FROM page WHERE path = ?").use { statement ->
            statement.setString(1, path)

            statement.executeQuery().use { resultSet ->
                return if (resultSet.next()) {
                    resultSet.getBytes(1)
                } else {
                    null
                }
            }
        }
    }

    /**
     * Resolves parent path for given path
     *
     * @param path path
     * @return parent path for given path
     */
    private fun getParentPath(path: String): String? {
        val slugs = StringUtils.removeEnd(path, "/").split('/')
        val parentPath = slugs.dropLast(1).joinToString("/")

        return parentPath.ifBlank { null }
    }

    /**
     * Update page parent id
     *
     * @param connection JDBC connection
     * @param id page id
     * @param parentId parent id
     * @throws CustomChangeException when migration fails
     */
    private fun updatePageParentId(connection: JdbcConnection, id: ByteArray, parentId: ByteArray) {
        try {
            connection.prepareStatement("UPDATE page SET parent_id = ? WHERE id = ?").use { statement ->
                statement.setBytes(1, parentId)
                statement.setBytes(2, id)
                statement.execute()
            }
        } catch (e: java.lang.Exception) {
            throw CustomChangeException(e)
        }
    }

}