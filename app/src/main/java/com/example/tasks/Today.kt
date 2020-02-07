package com.example.tasks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_today.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Today : AppCompatActivity() {
    val db by lazy {
        Room.databaseBuilder(this,TodoDatabase::class.java,"tasks.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    val sdf= SimpleDateFormat("d/M/yyyy")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_today)
        val sortedlist=ArrayList<Todo>()
        var adapter=TodoAdapter(sortedlist)
        val s=sdf.format(Date())
        Log.i("DATE",s)
        db.todoDao().getTodayTasks(s.substring(0,s.indexOf('/')).toInt(),
            s.substring(s.indexOf('/')+1,s.lastIndexOf('/')).toInt(),
            s.substring(s.lastIndexOf('/')+1,s.length).toInt()).observe(this,androidx.lifecycle.Observer {
            sortedlist.addAll(it)
            val list = sortedlist.sortedWith(
                compareBy<Todo>({ it.year.toInt()},
                    { it.month.toInt() },
                    { it.day.toInt() },
                    {it.Time.substring(0,it.Time.indexOf(':')).toInt()},
                    {it.Time.substring(it.Time.indexOf(':')+1,it.Time.length).toInt()})
            )
            sortedlist.clear()
            sortedlist.addAll(list)
            adapter.notifyDataSetChanged()
            RvToday.apply {
                layoutManager = LinearLayoutManager(this@Today)
                this.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        })
    }
}
