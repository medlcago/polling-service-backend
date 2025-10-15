package com.backend.pollingservice.entities

import com.backend.pollingservice.enums.PollStatus
import com.backend.pollingservice.enums.PollType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Formula
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.annotations.UuidGenerator
import java.time.Instant
import java.util.*

@Entity
@Table(name = "polls")
class Poll(

    @Id
    @UuidGenerator
    @Column(
        nullable = false,
        updatable = false,
        columnDefinition = "UUID DEFAULT gen_random_uuid()"
    )
    val id: UUID? = null,

    @Column(nullable = false)
    var question: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: PollType,

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    var anonymous: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    var createdBy: User,

    @OneToMany(mappedBy = "poll", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var options: MutableList<PollOption> = mutableListOf(),

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: PollStatus = PollStatus.OPEN,

    @CreationTimestamp
    @Column(
        name = "created_at",
        nullable = false,
        updatable = false,
        columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP"
    )
    val createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(
        name = "updated_at",
        nullable = false,
        columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP"
    )
    var updatedAt: Instant? = null,

    @Formula("(SELECT COUNT(v.id) FROM votes v JOIN poll_options po ON v.option_id = po.id WHERE po.poll_id = id)")
    var totalVotes: Int = 0,
) {
    fun addOption(text: String, isCorrect: Boolean? = null): PollOption {
        val option = PollOption(
            text = text,
            isCorrect = isCorrect,
            poll = this
        )
        options.add(option)
        return option
    }

    fun isCreatedBy(user: User?): Boolean {
        return user != null && createdBy.id == user.id
    }
}