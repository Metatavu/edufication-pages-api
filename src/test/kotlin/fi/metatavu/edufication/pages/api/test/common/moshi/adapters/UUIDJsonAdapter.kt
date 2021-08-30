package fi.metatavu.edufication.pages.api.test.common.moshi.adapters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.*

/**
 * Adapter class for UUID
 */
class UUIDJsonAdapter {

    @ToJson
    fun toJson(value: UUID?) = value?.toString()

    @FromJson
    fun fromJson(input: String) = UUID.fromString(input)
}
