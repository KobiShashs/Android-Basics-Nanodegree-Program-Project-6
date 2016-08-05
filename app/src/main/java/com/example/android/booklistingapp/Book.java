package com.example.android.booklistingapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by user on 7/26/2016.
 */
public class Book implements Parcelable{
    private String mName;
    private String mAuthor;

    public Book(String mAuthor, String mName) {
        setmName(mName);
        setmAuthor(mAuthor);
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmAuthor(String mAuthor) {
        String originalString, finalString = "";
        originalString = mAuthor.replace("[", "");
        originalString = originalString.replace("]", "");
        originalString = originalString.replace("\"", "");
        String[] newResults = originalString.split(",");
        for (int i = 0; i < newResults.length; i++) {
            if (i < newResults.length - 1) {
                finalString += newResults[i] + ", ";
            } else {
                finalString += newResults[i];
            }

        }
        this.mAuthor = finalString;
    }
    private Book(Parcel in) {
        mName = in.readString();
        mAuthor = in.readString();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mName);
        out.writeString(mAuthor);
    }


    public String getmName() {
        return mName;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Book> CREATOR  =
            new Parcelable.Creator<Book>() {
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        public Book[] newArray(int size) {
            return new  Book[size];
        }
    };
}
