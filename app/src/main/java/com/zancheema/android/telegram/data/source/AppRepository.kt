package com.zancheema.android.telegram.data.source

import androidx.lifecycle.LiveData
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.source.domain.UserDetail

interface AppRepository {
    suspend fun getUserDetail(): Result<UserDetail>

    fun observeUserDetail(): LiveData<Result<UserDetail>>

    suspend fun isRegisteredPhoneNumber(phoneNumber: String): Result<Boolean>

    fun observeUserExists(): LiveData<Result<Boolean>>

    suspend fun saveUser(user: User)

    suspend fun saveUserDetail(detail: UserDetail)
}