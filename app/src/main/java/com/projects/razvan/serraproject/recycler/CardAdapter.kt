package com.projects.razvan.serraproject.recycler

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import com.projects.razvan.serraproject.R
import android.widget.SeekBar.OnSeekBarChangeListener
import com.projects.razvan.serraproject.Control


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

        val control=Control.values()[position]

        holder.layout.tag = position
        holder.layout.findViewById<ImageView>(R.id.icon).setImageResource(control.icon)
        holder.layout.findViewById<TextView>(R.id.txvRoom).text=context.getString(control.description)
        holder.layout.findViewById<SeekBar>(R.id.seekBar).max=control.maxValue

        val txv=holder.layout.findViewById<TextView>(R.id.txvNumSlider)

        //holder.layout.layoutParams= LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

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
        return Control.values().size
    }
}