package com.projects.razvan.serraproject.task

import android.os.AsyncTask
import com.projects.razvan.serraproject.R
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Created by Razvan on 1/20/2018.
 */
class SocketTask(val address: InetAddress,val PORT:Int): AsyncTask<Byte,Void,Int>() {

    private val socket:DatagramSocket = DatagramSocket()

    override fun doInBackground(vararg array: Byte?): Int {

        val buff=ByteArray(array.size)

        for(i in 0 until array.size)
            buff[i]=array[i]!!

        val packet=DatagramPacket(buff,buff.size,address,PORT)
        socket.send(packet)

        socket.close()

        return R.string.success_message
    }

}