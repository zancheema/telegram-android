package com.zancheema.android.telegram.data.source.local

import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.AppDataSource
import com.zancheema.android.telegram.data.source.local.entity.asDomainModel
import com.zancheema.android.telegram.data.source.domain.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalDataSource(
    private val database: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AppDataSource {
    override fun observeUser(): LiveData<Result<User>> {
        TODO("Not yet implemented")
    }

    override fun observeUserDetailByPhoneNumber(phoneNumber: String): LiveData<Result<UserDetail>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserByPhoneNumber(phoneNumber: String): Result<User> =
        withContext(ioDispatcher) {
            try {
                val user = database.userDao().getUserByPhoneNumber(phoneNumber)?.asDomainModel()
                    ?: return@withContext domainError("user with given phone number does not exist")
                Success(user)
            } catch (e: Exception) {
                Error(e)
            }
        }

    override suspend fun getUserDetailByPhoneNumber(phoneNumber: String): Result<UserDetail> =
        withContext(ioDispatcher) {
            try {
                val userDetail = database.userDetailDao().getUserDetailByPhoneNumber(phoneNumber)
                    ?: return@withContext domainError("user detail with given phone number does not exist")
                Success(userDetail.asDomainModel())
            } catch (e: Exception) {
                Error(e)
            }
        }

    private fun domainError(message: String) = Error(Exception(message))

    override suspend fun saveUser(user: User) = withContext(ioDispatcher) {
        database.userDao().insertUser(user.asDatabaseEntity())
    }

    override suspend fun saveUserDetail(userDetail: UserDetail) {
        withContext(ioDispatcher) {
            try {
                database.userDetailDao().insertUserDetail(userDetail.asDatabaseEntity())
            } catch (e: SQLiteException) {
                println("error saving user detail: ${e.message}")
            }
        }
    }

    override suspend fun deleteUser(user: User) {
        deleteUserWithPhoneNumber(user.phoneNumber)
    }

    override suspend fun deleteUserWithPhoneNumber(phoneNumber: String) {
        withContext(ioDispatcher) {
            try {
                database.userDao().deleteUserByPhoneNumber(phoneNumber)
            } catch (e: SQLiteException) {
                println("error deleting user: ${e.message}")
            }
        }
    }
}