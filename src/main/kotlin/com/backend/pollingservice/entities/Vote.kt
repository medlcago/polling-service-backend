package com.backend.pollingservice.entities

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "votes",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "poll_id", "option_id"])
    ]
)
class Vote(

    @Id
    @UuidGenerator
    @Column(
        nullable = false,
        updatable = false,
        columnDefinition = "UUID DEFAULT gen_random_uuid()"
    )
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id", nullable = false)
    var poll: Poll,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "option_id", nullable = false)
    var option: PollOption,

    @Column(
        name = "voted_at",
        nullable = false,
        updatable = false,
        columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP"
    )
    val votedAt: Instant = Instant.now()
)