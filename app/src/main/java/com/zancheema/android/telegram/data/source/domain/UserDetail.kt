package com.zancheema.android.telegram.data.source.domain

import android.os.Parcelable
import com.zancheema.android.telegram.data.source.local.entity.DbUserDetail
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDetail(
    val phoneNumber: String,
    val firstName: String,
    val lastName: String,
    val imgUrl: String = "nothing",
    val msgToken: String = "nothing",
    val photoUrl: String = ""
) : Parcelable {
    @IgnoredOnParcel
    val fullName = "$firstName $lastName"
}

fun UserDetail.asDatabaseEntity() = DbUserDetail(
    phoneNumber = phoneNumber,
    firstName = firstName,
    lastName = lastName,
    photoUrl = imgUrl
)