package com.zancheema.android.telegram.data.source

import androidx.lifecycle.LiveData
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.source.domain.*

interface AppDataSource {
    fun observeUser(): LiveData<Result<User>>

    fun observeUserDetailByPhoneNumber(phoneNumber: String): LiveData<Result<UserDetail>>

    suspend fun getUserByPhoneNumber(phoneNumber: String): Result<User>

    suspend fun getUserDetailByPhoneNumber(phoneNumber: String): Result<UserDetail>

    suspend fun saveUser(user: User)

    suspend fun saveUserDetail(userDetail: UserDetail)

    suspend fun deleteUser(user: User)

    suspend fun deleteUserWithPhoneNumber(phoneNumber: String)
}