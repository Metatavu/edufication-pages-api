package fi.metatavu.edufication.pages.api.persistence.model

import java.util.*
import javax.persistence.*

/**
 * JPA entity for page content block navigation item
 */
@Entity
class ContentBlockNavigationItem {

    @Id
    var id: UUID? = null

    @ManyToOne
    var contentBlock: ContentBlock? = null

    @Column(nullable = true)
    var title: String? = null

    @Column(nullable = true)
    var url: String? = null

    @Column(nullable = true)
    var imageUrl: String? = null

    @Column(nullable = true)
    var orderNumber: Int? = null

}
