package com.example.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT id FROM user WHERE uuid = :uuid")
    fun get(uuid: String): Int

    @Query("SELECT * FROM user")
    fun getAllLiveData(): LiveData<List<User>>


//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " + "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User

    @Insert
    fun insertAll(vararg users: User)//:LiveData<Int>

    @Delete
    fun delete(user: User)

    @Query("DELETE FROM user")
    fun deleteAll()
}