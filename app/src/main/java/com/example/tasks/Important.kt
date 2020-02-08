package com.example.tasks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_important.*
import kotlinx.android.synthetic.main.activity_important.toolbar
import kotlinx.android.synthetic.main.activity_today.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Important : AppCompatActivity() {
    val db by lazy {
        Room.databaseBuilder(this,TodoDatabase::class.java,"tasks.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    val sdf= SimpleDateFormat("d/M/yyyy")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_important)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        val sortedlist=ArrayList<Todo>()
        var adapter=TodoAdapter(sortedlist)
        val s=sdf.format(Date())
        Log.i("DATE",s)
        db.todoDao().getImporatntTasks("true").observe(this,androidx.lifecycle.Observer {
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
            RvImportant.apply {
                layoutManager = LinearLayoutManager(this@Important)
                this.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        })
    }
}
