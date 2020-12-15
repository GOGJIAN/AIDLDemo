package com.example.aidldemo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    companion object{
        const val TAG = "MyBook Activity"
        const val MESSAGE_NEW_BOOK_ARRIVED = 1

    }
    val myHandler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            when(msg.what){
                MESSAGE_NEW_BOOK_ARRIVED -> {
                    Log.d(TAG,"receive new book:" + msg.obj)
                }
                else -> {
                    super.handleMessage(msg)
                }
            }
        }
    }

    var mRemoteBookManager:IBookManager? = null

    val listener = object :IOnNewBookArrivedListener.Stub(){
        override fun onNewBookArrived(newBook: Book) {
            myHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook).sendToTarget()
        }
    }

    private val mConnection:ServiceConnection = object:ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mRemoteBookManager = IBookManager.Stub.asInterface(service)
            try {
                val list = mRemoteBookManager!!.books
                Log.d(TAG,list::class.java.canonicalName!!+"\n"+list.toString())
                val book = Book(3,"flutter")
                mRemoteBookManager!!.addBook(book)
                Log.d(TAG, "add book:$book")
                val list1 = mRemoteBookManager!!.books
                Log.d(TAG,list1::class.java.canonicalName!!+"\n"+list1.toString())

                mRemoteBookManager!!.registerListener(listener)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindService(Intent(this,BookManagerService::class.java),mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        try {
            Log.d(TAG,"unregister listener$listener" )
            if(mRemoteBookManager != null && mRemoteBookManager!!.asBinder().isBinderAlive){
                mRemoteBookManager!!.unregisterListener(listener)
            }
        }catch (e:RemoteException){
            e.printStackTrace()
        }



        unbindService(mConnection)
        super.onDestroy()
    }
}