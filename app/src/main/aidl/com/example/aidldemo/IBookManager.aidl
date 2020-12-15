// IBookManager.aidl
package com.example.aidldemo;
import com.example.aidldemo.Book;
import com.example.aidldemo.IOnNewBookArrivedListener;
// Declare any non-default types here with import statements

interface IBookManager {
    List<Book> getBooks();
    void addBook(in Book book);

    void registerListener(IOnNewBookArrivedListener listener);
    void unregisterListener(IOnNewBookArrivedListener listener);
}