package com.projects.razvan.serraproject

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.InputFilter
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import com.projects.razvan.serraproject.recycler.CardAdapter
import com.projects.razvan.serraproject.recycler.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (sharedPreferences.getString(getString(R.string.preference_ip_key), "").isBlank()) {
            writeIPAddress()
        }

        container.setHasFixedSize(true)
        container.itemAnimator = DefaultItemAnimator()
        container.adapter=CardAdapter(this)
        container .layoutManager = LinearLayoutManager(this)


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
                    for (i in splits.indices) {
                        if (Integer.valueOf(splits[i]) > 255) {
                            return@InputFilter ""
                        }
                    }
                }
            }
            null
        }
        tilIP.editText?.filters = filters

        val dialog = AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setView(view)
                .setTitle(R.string.server_ip_address)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
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
}
