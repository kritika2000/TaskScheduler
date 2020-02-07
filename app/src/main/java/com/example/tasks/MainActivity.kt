package com.example.tasks
import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.icu.text.Edits
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Parcelable
import android.system.Os.remove
import android.text.TextUtils.substring
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Adapter
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.activity_set_task.*
import kotlinx.android.synthetic.main.item_task.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDate.parse
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import java.time.LocalDateTime
import java.time.OffsetDateTime

class MainActivity : AppCompatActivity() {
    val db by lazy {
        Room.databaseBuilder(this,TodoDatabase::class.java,"tasks.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    var Todos = arrayListOf<Todo>()
    var showMain=false
    val sdf=SimpleDateFormat("d/M/yyyy")
    val sdfT=SimpleDateFormat("h:mm")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        var adapter= TodoAdapter(Todos)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val ttl = intent?.getStringExtra("NAME1").toString()
            val tsk = intent?.getStringExtra("NAME2").toString()
            val dt = intent?.getParcelableExtra("NAME3") as? TaskDate
            val tm = intent?.getStringExtra("NAME4").toString()
            val priority = intent?.getStringExtra("NAME5").toString()
            Todos.add(Todo(ttl, tsk, dt!!.day,dt.month,dt.year,false,tm,priority))
            GlobalScope.launch(Dispatchers.IO) {
                db.todoDao().insertTasks(Todo(ttl, tsk, dt.day,dt.month,dt.year,false,tm,priority))
            }
                db.todoDao().getTasks().observe(this, Observer {
                    Todos.clear()
                    Todos.addAll(it)
                    adapter.notifyDataSetChanged()
                })
            RvTodos.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    this.adapter = adapter
                    adapter.notifyDataSetChanged()
            }
        }
            super.onActivityResult(requestCode, resultCode, intent)
    }
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
                    setSupportActionBar(toolbar)
                    var adapter = TodoAdapter(Todos)
                    db.todoDao().getTasks().observe(this, Observer {
                        Todos.clear()
                        Todos.addAll(it)
                        adapter.notifyDataSetChanged()
                    })
                    RvTodos.apply {
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        this.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                var c=Calendar.getInstance()
                var s=sdf.format(Date())
                var t=sdfT.format(c.getTime())
                Log.i("Time",t)
                val nMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                db.todoDao().getTasks().observe(this, Observer {
                    for (i in it) {
                        Log.i("Time",i.Time)
                        if (i.day == s.substring(0, s.indexOf('/')).toInt() &&
                            i.month == s.substring(
                                s.indexOf('/') + 1,
                                s.lastIndexOf('/')
                            ).toInt() &&
                            i.year == s.substring(s.lastIndexOf('/') + 1, s.length).toInt() &&
                            (i.Time.substring(0,i.Time.indexOf(':')).toInt()==t.substring(0,t.indexOf(':')).toInt()||
                            i.Time.substring(0,i.Time.indexOf(':')).toInt()-12==t.substring(0,t.indexOf(':')).toInt())&&
                           i.Time.substring(i.Time.indexOf(':')+1,i.Time.length).toInt()==t.substring(t.indexOf(':')+1,t.length).toInt()
                        ) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                nMgr.createNotificationChannel(
                                    NotificationChannel(
                                        "first",
                                        "default",
                                        NotificationManager.IMPORTANCE_DEFAULT
                                    )
                                )
                            }
                            val notification = NotificationCompat.Builder(this, "first")
                                .setContentTitle("Task Deadline")
                                .setContentText(i.Title)
                                .setSmallIcon(R.drawable.ic_check_black_24dp)
                                .build()
                                nMgr.notify(System.currentTimeMillis().toInt(), notification)
                        }
                    }
                })
                    fab.setOnClickListener {
                        val intent = Intent(this, SetTask::class.java)
                        startActivityForResult(intent, 1)
                    }

                    val ith = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                        override fun onMove(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder
                        ): Boolean {
                            return false
                        }

                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            val id = Todos.get(viewHolder.adapterPosition).id
                            (adapter as TodoAdapter).Remove(viewHolder)
                            adapter.notifyDataSetChanged()
                            GlobalScope.launch(Dispatchers.IO) {
                                db.todoDao().updateTask(id, true)
                            }
                            Snackbar.make(root, "Task Completed", Snackbar.LENGTH_INDEFINITE)
                                .setAction("UNDO", View.OnClickListener {
                                    GlobalScope.launch(Dispatchers.IO) {
                                        db.todoDao().updateTask(id, false)
                                    }
                                    db.todoDao().getTasks().observe(this@MainActivity, Observer {
                                        Todos.clear()
                                        Todos.addAll(it)
                                        adapter.notifyDataSetChanged()
                                    })
                                })
                                .show()
                        }
                    }
                    ItemTouchHelper(ith).attachToRecyclerView(RvTodos)
          }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(R.menu.menu_main, menu)
            return true
        }
        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            var adapter=TodoAdapter(Todos)
            when(item.itemId){
                R.id.action_history -> {
                    var intent=Intent(this,History::class.java)
                startActivity(intent)
                }
                R.id.action_sortBy -> {
                    val dialog = AlertDialog.Builder(this)
                    var list= arrayOf("My Order","Date")
                    //dialog.setNeutralButton("CANCEL",DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
                    dialog.setSingleChoiceItems(list,-1,DialogInterface.OnClickListener { dialog, which ->
                        if(list[which]=="Date") {
                            db.todoDao().getTasks().observe(this, Observer {
                                Todos.clear()
                                Todos.addAll(it)
                                var sortedlist=ArrayList<Todo>()
                                var adapter=TodoAdapter(sortedlist)
                                sortedlist.addAll(Todos)
                                val list = sortedlist.sortedWith(
                                    compareBy<Todo>({ it.year.toInt() },
                                        { it.month.toInt() },
                                        { it.day.toInt() },
                                        {it.Time.substring(0,it.Time.indexOf(':')).toInt()},
                                        {it.Time.substring(it.Time.indexOf(':')+1,it.Time.length).toInt()})
                                )
                                sortedlist.clear()
                                sortedlist.addAll(list)
                                adapter.notifyDataSetChanged()
                                RvTodos.apply {
                                    layoutManager = LinearLayoutManager(this@MainActivity)
                                    this.adapter = adapter
                                    adapter.notifyDataSetChanged()
                                }
                            }
                            )
                        }
                        if(list[which]=="My Order"){
                            RvTodos.apply {
                                layoutManager = LinearLayoutManager(this@MainActivity)
                                this.adapter = adapter
                                adapter.notifyDataSetChanged()
                            }
                        }

                    })
                    dialog.show()
                }
                R.id.action_today -> {
                    val intent=Intent(this,Today::class.java)
                    startActivity(intent)
                }
            }
            return when (item.itemId) {
                R.id.action_history -> true
                else -> super.onOptionsItemSelected(item)
            }
        }

}

