package com.example.tasks

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [Todo::class],version = 11,exportSchema = false)
abstract class TodoDatabase : RoomDatabase(){
    abstract fun todoDao() : TaskDao
}