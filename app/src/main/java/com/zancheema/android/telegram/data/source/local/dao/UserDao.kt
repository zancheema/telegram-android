package com.zancheema.android.telegram.data.source.local.dao

import androidx.room.*
import com.zancheema.android.telegram.data.source.local.entity.DbUser

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun getAll(): List<DbUser>

    @Query("SELECT * FROM users WHERE phone_number = :phoneNumber")
    suspend fun getUserByPhoneNumber(phoneNumber: String): DbUser?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: DbUser)

    @Query("DELETE FROM users")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteUser(user: DbUser)

    @Query("DELETE FROM users WHERE phone_number = :phoneNumber")
    suspend fun deleteUserByPhoneNumber(phoneNumber: String)
}
