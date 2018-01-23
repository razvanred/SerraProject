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
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import com.projects.razvan.serraproject.recycler.CardAdapter
import com.projects.razvan.serraproject.task.SendTask
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.net.InetAddress
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private val ROOMS = 8
        private val MAX_PORT = 49151
        private val MIN_PORT = 1024

        private val TIME_WAIT: Long = 30
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (sharedPreferences.getString(getString(R.string.preference_ip_key), "").isBlank() || sharedPreferences.getInt(getString(R.string.preference_port_key), 0) == 0) {
            writeIPAddressPort()
        }

        fab.setOnClickListener { view ->

            runOnUiThread(startLoading)

            val runnable = Runnable {
                Log.d(TAG, "${containerBtn.childCount}")

                val task = SendTask(InetAddress.getByName(sharedPreferences.getString(getString(R.string.preference_ip_key), "localhost")), sharedPreferences.getInt(getString(R.string.preference_port_key), 0))

                val sum = (0 until ROOMS)
                        .filter { containerBtn.getChildAt(it).tag == 1 }
                        .sumBy { Math.pow(2.toDouble(), it.toDouble()).toInt() }

                task.execute(sum.toByte(),
                        containerControls.layoutManager.getChildAt(0).findViewById<SeekBar>(R.id.seekBar).progress.toByte(),
                        containerControls.layoutManager.getChildAt(1).findViewById<SeekBar>(R.id.seekBar).progress.toByte(),
                        containerControls.layoutManager.getChildAt(2).findViewById<SeekBar>(R.id.seekBar).progress.toByte())

                Snackbar.make(view, getString(task.get(TIME_WAIT, TimeUnit.SECONDS)), Snackbar.LENGTH_LONG)
                        .setAction("boh", null).show()
            }

            Thread {
                runnable.run()
                runOnUiThread(stopLoading)
            }.start()

        }

        val width = resources.displayMetrics.widthPixels / (ROOMS + 1)

        for (i in 0 until ROOMS) {
            val vg = LinearLayout(this)
            layoutInflater.inflate(R.layout.btn_room, vg)
            val button = vg.findViewById<Button>(R.id.btn)
            button.layoutParams.width = width
            button.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT

            button.text = "${i + 1}"
            button.tag = 0

            button.id = i

            vg.removeAllViews()

            containerBtn.addView(button)
        }

        containerControls.setHasFixedSize(true)
        containerControls.itemAnimator = DefaultItemAnimator()
        containerControls.adapter = CardAdapter(this)
        containerControls.layoutManager = LinearLayoutManager(this)

    }

    private val startLoading = Runnable {
        progressBar.visibility = View.VISIBLE
        mainPage.alpha = 0.4f
        window.setFlags(16, 16)
    }

    private val stopLoading = Runnable {
        progressBar.visibility = View.GONE
        mainPage.alpha = 1f
        window.clearFlags(16)
    }

    fun selectThis(view: View) {

        if (view.tag == 0) {
            view.setBackgroundResource(R.drawable.rectangle)
            view.tag = 1
        } else {
            view.setBackgroundDrawable(null)
            view.tag = 0
        }
        Log.d(TAG, "You selected ${view.tag.toString().toInt()}")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.actConfigServer -> writeIPAddressPort()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /*private fun writePort(port: Int = sharedPreferences.getInt(getString(R.string.preference_port_key), 0)) {

        val view = LinearLayout(this)
        layoutInflater.inflate(R.layout.til_port, view)
        val tilPort = view.findViewById<TextInputLayout>(R.id.tilPort)

        val dialog = AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setView(view)
                .setTitle(R.string.server_ip_address)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create()


        dialog.setOnShowListener({
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

            }
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {

            }
        })

        tilPort.editText?.setText("$port")



        dialog.show()
    }*/

    private fun isIP(text: String): Boolean {
        val p = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")
        val m = p.matcher(text)
        return m.find()
    }

    private fun writeIPAddressPort(ip: String = sharedPreferences.getString(getString(R.string.preference_ip_key), ""), port: Int = sharedPreferences.getInt(getString(R.string.preference_port_key), 0)) {

        val view = LinearLayout(this)
        layoutInflater.inflate(R.layout.til_ip_port, view)
        val tilIP = view.findViewById<TextInputLayout>(R.id.tilIP)
        val tilPort = view.findViewById<TextInputLayout>(R.id.tilPort)

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
                tilIP.error = null
                tilPort.error = null

                val text = tilIP.editText?.text.toString()

                if (isIP(text)) {
                    //dialog.dismiss()
                    try {
                        val p = tilPort.editText?.text.toString().toInt()
                        if (p < MIN_PORT || p > MAX_PORT)
                            tilPort.error = getString(R.string.port_range, MIN_PORT, MAX_PORT)
                        else {

                            val editor = sharedPreferences.edit()

                            editor.apply {
                                putString(getString(R.string.preference_ip_key), tilIP.editText!!.text.toString())
                                putInt(getString(R.string.preference_port_key), tilPort.editText!!.text.toString().toInt())
                            }

                            editor.apply()

                            dialog.dismiss()
                        }
                    } catch (exc: NumberFormatException) {
                        tilPort.error = getString(R.string.fill_field)
                    }


                } else
                    tilIP.error = getString(R.string.ip_not_valid)
            }
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                tilIP.error = null
                tilPort.error = null
                if (sharedPreferences.getString(getString(R.string.preference_ip_key), "").isNotBlank()) {
                    //dialog.dismiss()

                    if (sharedPreferences.getInt(getString(R.string.preference_port_key), 0) == 0)
                        dialog.dismiss()
                    else
                        tilPort.error = getString(R.string.port_not_set)

                } else
                    tilIP.error = getString(R.string.ip_not_set)
            }
        })

        tilIP.editText?.setText(ip)

        tilPort.editText?.setText(if (port == 0) "" else "$port")

        tilPort.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    if (p0.toString().toInt() == 0)
                        tilPort.editText?.setText("")
                } catch (exc: NumberFormatException) {
                }
            }
        })

        dialog.show()
    }

}
