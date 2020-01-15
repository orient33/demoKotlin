package com.example.kotlindemo

import androidx.room.Room
import android.os.SystemClock
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.util.Log
import com.example.room.AppDb
import com.example.room.User
import com.example.room.UserDao
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class RoomTest {
    private lateinit var userDao: UserDao
    private lateinit var db: AppDb

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getTargetContext()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDb::class.java
        ).build()
        userDao = db.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() {

        for (i in 1..10){//Int.MAX_VALUE) {
            val user = User(UUID.randomUUID().toString())
//            .createUser(3).apply {setName("george") }
//            userDao.insertAll(user)
            write(userDao, user)
        }
        val u = userDao.get("111")
        Log.d("df", "get result. $u")
//        val byName = userDao.findUsersByName("george")
//        assertThat(byName.get(0), equalTo(user))
    }

    private fun write(dao: UserDao, user: User) {
        Single.create(SingleOnSubscribe<Int> {
            SystemClock.sleep(10)
            dao.insertAll(user)
            it.onSuccess(1)
        }).subscribeOn(Schedulers.io()).subscribe()
    }
}