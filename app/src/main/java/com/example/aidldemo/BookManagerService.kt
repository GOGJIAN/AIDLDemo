package com.example.aidldemo

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import android.os.RemoteException
import android.util.Log
import java.io.InterruptedIOException
import java.lang.Thread.sleep
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

class BookManagerService : Service() {

    companion object{
        const val TAG = "MyBook Service"
    }

    private val mIsDestroy = AtomicBoolean(false)

    val mBookList:CopyOnWriteArrayList<Book> = CopyOnWriteArrayList()

    val mListenerList = RemoteCallbackList<IOnNewBookArrivedListener>()

    private val mBinder = object:IBookManager.Stub(){

        override fun registerListener(listener: IOnNewBookArrivedListener?) {
            mListenerList.register(listener)
            val N = mListenerList.beginBroadcast()
            Log.d(TAG,"now listener size:$N")
            mListenerList.finishBroadcast()
        }

        override fun unregisterListener(listener: IOnNewBookArrivedListener?) {
            mListenerList.unregister(listener)
            val N = mListenerList.beginBroadcast()
            Log.d(TAG,"now listener size:$N")
            mListenerList.finishBroadcast()
        }

        override fun addBook(book: Book?) {
            mBookList.add(book)
        }

        override fun getBooks(): MutableList<Book> {
            return mBookList
        }

    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        mBookList.add(Book(1,"Android"))
        mBookList.add(Book(1,"iOS"))
        Thread(ServiceWork()).start()
    }

    override fun onDestroy() {
        mIsDestroy.set(true)
        super.onDestroy()
    }

    private fun onNewBookArrived(book: Book){
        mBookList.add(book)
        val N = mListenerList.beginBroadcast()
        for (i in 0 until N){
            mListenerList.getBroadcastItem(i).onNewBookArrived(book)
        }
        mListenerList.finishBroadcast()
    }

    inner class ServiceWork : Runnable{
        override fun run() {
            while (!mIsDestroy.get()){
                try {
                    sleep(5000)
                }catch (e:InterruptedException){
                    e.printStackTrace()
                }
                val bookId = mBookList.size + 1;
                val newBook = Book(bookId,"newBook #$bookId")

                try {
                    onNewBookArrived(newBook)
                }catch (e: RemoteException){
                    e.printStackTrace()
                }
            }
        }
    }
}