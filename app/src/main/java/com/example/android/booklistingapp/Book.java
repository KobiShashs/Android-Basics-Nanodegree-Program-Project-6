package com.example.android.booklistingapp;

import java.util.ArrayList;

/**
 * Created by user on 7/26/2016.
 */
public class Book {
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


    public String getmName() {
        return mName;
    }

    public String getmAuthor() {
        return mAuthor;
    }


}
