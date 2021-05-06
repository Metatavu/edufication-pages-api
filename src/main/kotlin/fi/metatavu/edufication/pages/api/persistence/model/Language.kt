package fi.metatavu.edufication.pages.api.persistence.model

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Language {

    @Id
    var id: UUID? = null

    @Column(nullable = true)
    var name: String? = null

}
