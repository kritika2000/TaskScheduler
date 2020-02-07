package com.example.tasks

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.Observer as JavaUtilObserver
private fun setMonth(month: String): Int {
    when (month) {
        "Jan" -> return 0
        "Feb" -> return 1
        "Mar" -> return 2
        "Apr" -> return 3
        "May" -> return 4
        "Jun" -> return 5
        "Jul" -> return 6
        "Aug" -> return 7
        "Sep" -> return 8
        "Oct" -> return 9
        "Nov" -> return 10
        "Dec" -> return 11
    }
    return -1
}
class History : AppCompatActivity() {
    val db by lazy {
        Room.databaseBuilder(this,TodoDatabase::class.java,"tasks.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    var Todos=ArrayList<Todo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        var adapter=TodoAdapter(Todos)
        db.todoDao().getAllTasks().observe(this, Observer {
                Todos.clear()
                Todos.addAll(it)
                val list=Todos.sortedWith(compareBy<Todo>({it.year.toInt()},{it.month.toInt()},{it.day.toInt()}))
                Todos.clear()
                Todos.addAll(list)
                adapter.notifyDataSetChanged()
        }
        )
        Rvmenu.apply {
            layoutManager = LinearLayoutManager(this@History)
            this.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        val ith=object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val id=Todos.get(viewHolder.adapterPosition).id
                val task=Todos.get(viewHolder.adapterPosition)
                (adapter as TodoAdapter).Remove(viewHolder)
                adapter.notifyDataSetChanged()
                GlobalScope.launch(Dispatchers.IO) {
                    db.todoDao().deleteTask(id)
                    }
                Snackbar.make(ROOT,"Task Deleted", Snackbar.LENGTH_INDEFINITE)
                    .setAction("UNDO", View.OnClickListener {
                        GlobalScope.launch(Dispatchers.IO) {
                            db.todoDao().insertTasks(task)
                        }
                        db.todoDao().getAllTasks().observe(this@History, Observer {
                            Todos.clear()
                            Todos.addAll(it)
                            val list=Todos.sortedWith(compareBy({it.year},{it.month},{it.day}))
                            Todos.clear()
                            Todos.addAll(list)
                            adapter.notifyDataSetChanged()
                        })
                    })
                    .show()
            }
        }
        ItemTouchHelper(ith).attachToRecyclerView(Rvmenu)
    }
    fun DeleteAll(view: View) {
        var adapter = TodoAdapter(Todos)
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Are you sure you want to delete all Tasks?")
        dialog.setPositiveButton("PROCEED", DialogInterface.OnClickListener { dialog, which ->
            GlobalScope.launch(Dispatchers.IO) {
                db.todoDao().deleteAll()
            }
            adapter.notifyDataSetChanged()

        })
        dialog.setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
        })
        dialog.show()
    }

}
