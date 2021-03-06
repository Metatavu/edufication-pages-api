package fi.metatavu.edufication.pages.api.persistence.model

import fi.metatavu.edufication.pages.api.model.PageStatus
import fi.metatavu.edufication.pages.api.model.PageTemplate
import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*

@Entity
class Page {

    @Id
    var id: UUID? = null

    @Column(nullable = true)
    var status: PageStatus? = null

    var title: String? = null

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    var template: PageTemplate? = null

    @ManyToOne
    var parent: Page? = null

    @Column(nullable = false)
    var path: String? = null

    @Column(nullable = false)
    var language: String? = null

    @Column(nullable = false)
    var private: Boolean? = null

    @Column(nullable = false)
    var createdAt: OffsetDateTime? = null

    @Column(nullable = false)
    var modifiedAt: OffsetDateTime? = null

    @Column(nullable = false)
    var creatorId: UUID? = null

    @Column(nullable = false)
    var lastModifierId: UUID? = null

    /**
     * JPA pre-persist event handler
     */
    @PrePersist
    fun onCreate() {
        createdAt = OffsetDateTime.now()
        modifiedAt = OffsetDateTime.now()
    }

    /**
     * JPA pre-update event handler
     */
    @PreUpdate
    fun onUpdate() {
        modifiedAt = OffsetDateTime.now()
    }
}
