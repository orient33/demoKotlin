package com.example.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
