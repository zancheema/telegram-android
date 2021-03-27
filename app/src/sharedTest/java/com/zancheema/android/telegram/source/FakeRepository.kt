package com.zancheema.android.telegram.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.zancheema.android.telegram.data.Result
import com.zancheema.android.telegram.data.Result.Error
import com.zancheema.android.telegram.data.Result.Success
import com.zancheema.android.telegram.data.source.AppRepository
import com.zancheema.android.telegram.data.source.domain.User
import com.zancheema.android.telegram.data.source.domain.UserDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeRepository @Inject constructor() : AppRepository {

    private val observableUser = MutableLiveData<User>(null)
    private val observableUsers = MutableLiveData<List<User>>(emptyList())
    private var observableUserDetail = MutableLiveData<UserDetail>(null)

    override suspend fun getUserDetail(): Result<UserDetail> {
        return observableUserDetail.value?.let {
            Success(it)
        } ?: Error(Exception("user does not exist"))
    }

    override fun observeUserDetail(): LiveData<Result<UserDetail>> {
        TODO("Not yet implemented")
    }

    override suspend fun isRegisteredPhoneNumber(phoneNumber: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            for (u in observableUsers.value!!) {
                if (u.phoneNumber == phoneNumber) return@withContext Success(true)
            }
            Success(false)
        }

    override fun observeUserExists(): LiveData<Result<Boolean>> =
        observableUser.map { Success(it != null) }

    override suspend fun saveUser(user: User) = withContext(Dispatchers.Main) {
        // save user
        observableUser.value = user
        observableUsers.value = observableUsers.value!!.toMutableList().apply { add(user) }
    }

    override suspend fun saveUserDetail(detail: UserDetail) = withContext(Dispatchers.Main) {
        observableUserDetail.value = detail
    }
}