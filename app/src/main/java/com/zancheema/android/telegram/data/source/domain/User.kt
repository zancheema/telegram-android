package com.zancheema.android.telegram.data.source.domain

import com.zancheema.android.telegram.data.source.local.entity.DbUser

data class User(
    val phoneNumber: String,
)

fun User.asDatabaseEntity() = DbUser(
    phoneNumber = phoneNumber
)