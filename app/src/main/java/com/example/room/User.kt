package com.example.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class User(

        @ColumnInfo(name = "uuid")
        val uuid: String,

        @ColumnInfo(name = "first_name")
        val firstName: String = "firstName",

        @ColumnInfo(name = "last_name")
        val lastName: String? = "lastName"
) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var gardenPlantingId: Long = 0
}
