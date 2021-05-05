package fi.metatavu.edufication.pages.api.persistence.model

import java.util.*
import javax.persistence.*

@Entity
class Quiz {

    @Id
    var id: UUID? = null

    @OneToOne(mappedBy = "quiz")
    var contentBlock: ContentBlock? = null

    @Column(nullable = true)
    var text: String? = null

    @Column(nullable = true)
    @Lob
    var options: String? = null

    @Column(nullable = true)
    var correctIndex: Int? = null

}
