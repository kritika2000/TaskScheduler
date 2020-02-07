package com.example.tasks

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import com.example.tasks.TaskDate as TaskDate
@SuppressLint("ParcelCreator")
@Entity
@Parcelize
data class Todo  (
    val Title:String,
    val Task:String,
    val day:Int,
    val month:Int,
    val year:Int,
    var isSwiped:Boolean,
    val Time:String,
    val Priority:String,
    @PrimaryKey(autoGenerate = true)
   val id:Long=0L):Parcelable
