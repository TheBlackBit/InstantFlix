package com.theblackbit.instantflix.core.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Genre(
    @PrimaryKey
    val id: Int,
    val name: String?,
)
