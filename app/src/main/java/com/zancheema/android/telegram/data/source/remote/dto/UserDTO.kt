package com.zancheema.android.telegram.data.source.remote.dto

import com.zancheema.android.telegram.data.source.domain.UserDetail

data class UserDTO(
    val phoneNumber: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val photoUrl: String = ""
)

fun UserDTO.asDomainModel() = UserDetail(
    phoneNumber = phoneNumber,
    firstName = firstName,
    lastName = lastName,
    photoUrl = photoUrl
)