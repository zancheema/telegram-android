package com.zancheema.android.telegram.data.source.local.dao

import androidx.room.*
import com.zancheema.android.telegram.data.source.local.entity.DbUserDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDetailDao {
    @Query("SELECT * FROM user_details")
    fun observeAll(): Flow<List<DbUserDetail>>

    @Query("SELECT * FROM user_details")
    suspend fun getAll(): List<DbUserDetail>

    @Query("SELECT * FROM user_details WHERE phone_number IN (:phoneNumbers)")
    fun observeUserDetailsByPhoneNumbers(phoneNumbers: List<String>): Flow<List<DbUserDetail>>

    @Query("SELECT * FROM user_details WHERE phone_number IN (:phoneNumbers)")
    suspend fun getUserDetailsByPhoneNumbers(phoneNumbers: List<String>): List<DbUserDetail>

    @Query("SELECT * FROM user_details WHERE phone_number = :phoneNumber")
    fun observeUserDetailByPhoneNumber(phoneNumber: String): Flow<DbUserDetail?>

    @Query("SELECT * FROM user_details WHERE phone_number = :phoneNumber")
    suspend fun getUserDetailByPhoneNumber(phoneNumber: String): DbUserDetail?

    /**
     * Inserts new [DbUserDetail] and replaces with the new one in case of duplication
     * thus, also does the job of update
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserDetail(userDetail: DbUserDetail)
}
