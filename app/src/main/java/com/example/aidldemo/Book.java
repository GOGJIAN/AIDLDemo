package com.example.aidldemo;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
    int no;
    String name;
    protected Book(Parcel in) {
        no = in.readInt();
        name = in.readString();
    }

    public Book(int no, String name) {
        this.no = no;
        this.name = name;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(no);
        dest.writeString(name);
    }

    @Override
    public String toString() {
        return "Book{" +
                "no=" + no +
                ", name='" + name + '\'' +
                '}';
    }
}
