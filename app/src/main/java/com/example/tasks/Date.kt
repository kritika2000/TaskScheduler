package com.example.tasks

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
@SuppressLint("ParcelCreator")
@Parcelize
data class TaskDate(
    val day:Int,
    val month:Int,
    val year:Int
) : Parcelable