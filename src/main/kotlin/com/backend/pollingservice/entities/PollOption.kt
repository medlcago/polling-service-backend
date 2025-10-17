package com.backend.pollingservice.entities

import jakarta.persistence.*
import org.hibernate.annotations.Formula
import org.hibernate.annotations.UuidGenerator
import java.util.*
import kotlin.math.roundToInt

@Entity
@Table(name = "poll_options")
class PollOption(

    @Id
    @UuidGenerator
    @Column(
        nullable = false,
        updatable = false,
        columnDefinition = "UUID DEFAULT gen_random_uuid()"
    )
    val id: UUID? = null,

    @Column(nullable = false, length = 100)
    var text: String,

    @Column
    var isCorrect: Boolean? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id", nullable = false)
    var poll: Poll,

    @OneToMany(mappedBy = "option", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var votes: MutableList<Vote> = mutableListOf(),

    @Formula("(SELECT COUNT(v.id) FROM votes v WHERE v.option_id = id)")
    var voteCount: Long = 0
) {
    val percent: Int
        get() {
            if (poll.totalVotes == 0) return 0
            return ((voteCount * 100.0) / poll.totalVotes).roundToInt()
        }
}