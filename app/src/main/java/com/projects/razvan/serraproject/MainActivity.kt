package com.projects.razvan.serraproject

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.InputFilter
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.projects.razvan.serraproject.recycler.CardAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    companion object{
        private val TAG=MainActivity::class.java.simpleName
        private val ROOMS=8
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (sharedPreferences.getString(getString(R.string.preference_ip_key), "").isBlank()) {
            writeIPAddress()
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "${containerControls.layoutManager.getChildAt(0).findViewById<TextView>(R.id.txvNumSlider).text.toString().toInt().toByte()}", Snackbar.LENGTH_LONG)
                    .setAction("boh", null).show()
        }

        val width=resources.displayMetrics.widthPixels/(ROOMS+1)

        for(i in 0 until ROOMS) {
            val vg=LinearLayout(this)
            layoutInflater.inflate(R.layout.btn_room,vg)
            val button=vg.findViewById<Button>(R.id.btn)
            button.layoutParams.width = width
            button.layoutParams.height=LinearLayout.LayoutParams.WRAP_CONTENT

            button.text="${i+1}"
            button.tag=0

           button.id=i

            vg.removeAllViews()

            containerBtn.addView(button)
        }

        containerControls.setHasFixedSize(true)
        containerControls.itemAnimator = DefaultItemAnimator()
        containerControls.adapter= CardAdapter(this)
        containerControls.layoutManager = LinearLayoutManager(this)

    }

    fun selectThis(view: View){

        if(view.tag==0) {
            view.setBackgroundResource(R.drawable.rectangle)
            view.tag=1
        }
        else {
            view.setBackgroundDrawable(null)
            view.tag=0
        }
        Log.d(TAG,"You selected ${view.tag.toString().toInt()}")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
            R.id.actChangeIP-> writeIPAddress()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun isIP(text: String): Boolean {
        val p = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")
        val m = p.matcher(text)
        return m.find()
    }

    private fun writeIPAddress(ip: String=sharedPreferences.getString(getString(R.string.preference_ip_key),"")) {

        val view = LinearLayout(this)
        layoutInflater.inflate(R.layout.til_ip, view)
        val tilIP = view.findViewById<TextInputLayout>(R.id.tilIP)

        val filters = arrayOfNulls<InputFilter>(1)
        filters[0] = InputFilter { source, start, end, dest, dstart, dend ->
            if (end > start) {
                val destTxt = dest.toString()
                val resultingTxt = (destTxt.substring(0, dstart)
                        + source.subSequence(start, end)
                        + destTxt.substring(dend))
                if (!resultingTxt
                        .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?".toRegex())) {
                    return@InputFilter ""
                } else {
                    val splits = resultingTxt.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    splits.indices
                            .filter { Integer.valueOf(splits[it]) > 255 }
                            .forEach { return@InputFilter "" }
                }
            }
            null
        }
        tilIP.editText?.filters = filters

        val dialog = AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setView(view)
                .setTitle(R.string.server_ip_address)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create()

        dialog.setOnShowListener({
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val text = tilIP.editText?.text.toString()

                if (isIP(text))
                    dialog.dismiss()

                else
                    tilIP.error = getString(R.string.ip_not_valid)
            }
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                if (sharedPreferences.getString(getString(R.string.preference_ip_key), "").isNotBlank())
                    dialog.dismiss()
                else
                    tilIP.error = getString(R.string.ip_not_set)
            }
        })

        tilIP.editText?.setText(ip)

        dialog.show()
    }

}
