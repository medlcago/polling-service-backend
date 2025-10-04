package com.backend.pollingservice.entities


import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.annotations.UuidGenerator
import java.time.Instant
import java.util.*


@Entity
@Table(name = "users")
class User(
    @Id
    @UuidGenerator
    @Column(
        nullable = false,
        updatable = false,
        columnDefinition = "UUID DEFAULT gen_random_uuid()"
    )
    val id: UUID? = null,

    @Column(
        nullable = false,
        unique = true,
        length = 50
    )
    var username: String,

    @Column(length = 128, nullable = false)
    var password: String,

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
    var updatedAt: Instant? = null
)
