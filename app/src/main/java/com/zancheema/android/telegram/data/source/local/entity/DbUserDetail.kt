package com.zancheema.android.telegram.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.zancheema.android.telegram.data.source.domain.UserDetail

/**
 * Detail for [DbUser]
 *
 * @property phoneNumber is [ForeignKey] to [DbUser.phoneNumber]
 * and is automatically deleted when [DbUser.phoneNumber] is deleted
 */
@Entity(
    tableName = "user_details",
    primaryKeys = ["phone_number"],
    foreignKeys = [
        ForeignKey(
            entity = DbUser::class,
            parentColumns = ["phone_number"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.RESTRICT,
            childColumns = ["phone_number"]
        )
    ]
)
data class DbUserDetail(
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String,
    @ColumnInfo(name = "photo_url") val photoUrl: String
)

fun DbUserDetail.asDomainModel() = UserDetail(
    phoneNumber = phoneNumber,
    firstName = firstName,
    lastName = lastName
)