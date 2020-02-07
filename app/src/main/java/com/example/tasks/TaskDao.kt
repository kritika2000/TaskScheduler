package com.example.tasks

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.text.SimpleDateFormat

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTasks(task:Todo)
    @Insert
    suspend fun insertAllTasks(tasks:List<Todo>)
    @Query("Select * From Todo")
    fun getAllTasks():LiveData<List<Todo>>
    @Query("Delete From Todo where id=:id")
    suspend fun deleteTask(id:Long)
    @Query("Delete From Todo")
    suspend fun deleteAll()
    @Query("Update Todo Set isSwiped=:v where id=:id")
    suspend fun updateTask(id:Long,v:Boolean)
    @Query("Select * From Todo where (isSwiped==0)")
    fun getTasks():LiveData<List<Todo>>
    @Query("Select * From Todo where day=:day AND month=:month AND year=:year")
    fun getTodayTasks(day:Int,month:Int,year:Int) : LiveData<List<Todo>>
}