package fi.metatavu.edufication.pages.api.persistence.model

import fi.metatavu.edufication.pages.api.model.ContentBlockLayout
import java.util.*
import javax.persistence.*


@Entity
class ContentBlock {

    @Id
    var id: UUID? = null

    @ManyToOne(optional = false)
    var page: Page? = null

    @Column(nullable = false)
    var layout: ContentBlockLayout? = null

    @Column(nullable = true)
    var title: String? = null

    @Column(nullable = true)
    var textContent: String? = null

    @Column(nullable = true)
    var media: String? = null

    @Column(nullable = true)
    var link: String? = null

    @Column(nullable = false)
    var orderInPage: Int? = null

    @OneToOne(cascade = [CascadeType.REMOVE])
    @JoinColumn(name = "quiz_id", referencedColumnName = "id")
    var quiz: Quiz? = null

}
