package com.example.tasks

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.color.MaterialColors.getColor
import kotlinx.android.synthetic.main.activity_set_task.view.*
import kotlinx.android.synthetic.main.item_task.*
import kotlinx.android.synthetic.main.item_task.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TodoAdapter(val todos : ArrayList<Todo>) : RecyclerView.Adapter<TodoAdapter.TaskViewHolder>() {
    override fun getItemCount(): Int {
        return todos.size
    }
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(todos.get(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemview:View=LayoutInflater.from(parent.context).inflate(
            R.layout.item_task,
            parent,
            false
        )
        return TaskViewHolder(itemview)
    }
    fun Remove(viewHolder: RecyclerView.ViewHolder) {
        val pos=viewHolder.adapterPosition
        if(pos<0||pos>=todos.size)
            return
        todos.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)
        notifyDataSetChanged()
    }
    class TaskViewHolder(itemview:View) : RecyclerView.ViewHolder(itemview) {
        fun bind(todo:Todo){
            itemView.titleid.text=todo.Title
            itemView.taskid.text=todo.Task
            val date:String="${todo.day}/${todo.month}/${todo.year}"
            itemView.dateid.text=date
            //Log.i("DATE","${todo.day}/${todo.month}/${todo.year}")
            itemView.Timeid.text=todo.Time
            if(todo.Priority=="true") {
                val color="#39FFC107"
                itemView.setBackgroundColor(Color.parseColor(color))
            }
            else{
                val color="#FFFFFF"
                itemView.setBackgroundColor(Color.parseColor(color))
            }
        }
    }

}