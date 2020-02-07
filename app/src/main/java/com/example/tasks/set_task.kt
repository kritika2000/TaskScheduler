package com.example.tasks

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_set_task.*
import kotlinx.android.synthetic.main.activity_set_task.view.*
import kotlinx.android.synthetic.main.item_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class SetTask : AppCompatActivity() {
    val i = Intent()
    var x:Int=0
    lateinit var datepickerdialog : DatePickerDialog
    lateinit var timepickerdialog : TimePickerDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_task)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        save.isEnabled=false
        til3.setdate.setOnClickListener {
            val cal = Calendar.getInstance()
            val y = cal.get(Calendar.YEAR)
            val m = cal.get(Calendar.MONTH)
            val D = cal.get(Calendar.DATE)
            val H = cal.get(Calendar.HOUR_OF_DAY)
            val M = cal.get(Calendar.MINUTE)
            var d:String=""
            datepickerdialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    d = "$dayOfMonth/${month+1}/$year"
                    setdate.setText(d, TextView.BufferType.EDITABLE)
                },
                y,
                m,
                D
            )
            datepickerdialog.show()
        }
        til4.setTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val y = cal.get(Calendar.YEAR)
            val m = cal.get(Calendar.MONTH)
            val D = cal.get(Calendar.DATE)
            val H = cal.get(Calendar.HOUR_OF_DAY)
            val M = cal.get(Calendar.MINUTE)
            var d:String=""
            timepickerdialog = TimePickerDialog(this,TimePickerDialog.OnTimeSetListener{
                view, hourOfDay, minute  ->
                setTime.setText("$hourOfDay:$minute",TextView.BufferType.EDITABLE)
            },
                H,
                M,
                true
            )
            timepickerdialog.show()
        }
        til1.title.setOnFocusChangeListener { v, hasFocus ->
            til1.title.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    i.putExtra("NAME1", "$s")
                    if (s.toString().equals("")) {
                          save.setEnabled(false)
                    }
                    else{
                        if(!TextUtils.isEmpty(til2.task.text.toString())&&!TextUtils.isEmpty(til3.setdate.text.toString())&&!TextUtils.isEmpty(til4.setTime.text.toString())) {
                            save.setEnabled(true)
                        }
                    }
               }
            })
        }
        til2.task.setOnFocusChangeListener { v, hasFocus ->
            til2.task.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    i.putExtra("NAME2", "$s")
                    if (s.toString().equals("")) {
                            save.setEnabled(false)
                   }
                    else{
                        if(!TextUtils.isEmpty(til1.title.text.toString())&&!TextUtils.isEmpty(til3.setdate.text.toString())&&!TextUtils.isEmpty(til4.setTime.text.toString()))
                           save.setEnabled(true)
                    }
                }
            })
        }
        setdate.setOnFocusChangeListener { v, hasFocus ->
            setdate.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val date=TaskDate(s.substring(0,s.indexOf('/')).toInt(),
                        s.substring(s.indexOf('/')+1,s.lastIndexOf('/')).toInt(),
                        s.substring(s.lastIndexOf('/')+1,s.length).toInt())
                    i.putExtra("NAME3",date)
                    if (s.toString().equals("")) {
                            save.setEnabled(false)
                    }
                    else{
                        if(!TextUtils.isEmpty(til2.task.text.toString())&&!TextUtils.isEmpty(til1.title.text.toString())&&!TextUtils.isEmpty(til4.setTime.text.toString()))
                            save.setEnabled(true)
                    }
                }
            })

        }
        setTime.setOnFocusChangeListener { v, hasFocus ->
            setTime.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    i.putExtra("NAME4", "$s")
                    if (s.toString().equals("")) {
                            save.setEnabled(false)
                    }
                    else{
                        if(!TextUtils.isEmpty(til2.task.text.toString())&&!TextUtils.isEmpty(til1.title.text.toString())&&!TextUtils.isEmpty(til3.setdate.text.toString()))
                            save.setEnabled(true)
                    }
                }
            })

        }
        save.setOnClickListener {
            setResult(Activity.RESULT_OK,i)
            finish()
        }
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    fun SetPriority(view: View){
          i.putExtra("NAME5","true")
          Snackbar.make(Root,"Marked as Important",Snackbar.LENGTH_LONG)
              .setAction("UNDO",View.OnClickListener {
                  i.putExtra("NAME5","false")
              }).show()
    }
}