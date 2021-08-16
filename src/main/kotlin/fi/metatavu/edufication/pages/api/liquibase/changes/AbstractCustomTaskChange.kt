package fi.metatavu.edufication.pages.api.liquibase.changes

import liquibase.change.custom.CustomTaskChange
import liquibase.database.Database
import liquibase.exception.ValidationErrors
import liquibase.resource.ResourceAccessor

/**
 * Abstract base class for custom Liquibase migrations
 */
abstract class AbstractCustomTaskChange: CustomTaskChange {

    override fun setUp() {

    }

    override fun setFileOpener(resourceAccessor: ResourceAccessor?) {

    }

    override fun validate(database: Database?): ValidationErrors {
        return ValidationErrors()
    }

}