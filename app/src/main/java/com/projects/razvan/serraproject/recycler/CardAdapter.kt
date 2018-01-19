package com.projects.razvan.serraproject.recycler

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import com.projects.razvan.serraproject.R
import android.widget.Toast
import android.widget.SeekBar.OnSeekBarChangeListener


/**
 * Created by Razvan on 1/19/2018.
 */
class CardAdapter(private val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_room, parent, false) as CardView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // holder.setIsRecyclable(false)
        // cursor.moveToPosition(position)
        val txv = holder.layout.findViewById<TextView>(R.id.txvNumSlider)
        holder.layout.findViewById<TextView>(R.id.txvRoom).text = context.getString(R.string.room_d, position + 1)

        holder.layout.findViewById<SeekBar>(R.id.seekBar).setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // TODO Auto-generated method stub
                txv.text = "$progress"
                //t1.setTextSize(progress)
                //Toast.makeText(getApplicationContext(), progress.toString(), Toast.LENGTH_LONG).show()

            }
        })

        // elements.customizeCard(holder, context, cursor, position)
    }

    override fun getItemCount(): Int {
        return 8
    }
}