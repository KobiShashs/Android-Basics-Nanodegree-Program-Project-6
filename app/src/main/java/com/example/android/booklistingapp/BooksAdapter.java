package com.example.android.booklistingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 7/26/2016.
 */
public class BooksAdapter extends ArrayAdapter<Book> {
    public BooksAdapter(Context context, ArrayList<Book> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        //Book Object
        Book currentBook = getItem(position);

        //Book title
        TextView textViewBookName = (TextView) listItemView.findViewById(R.id.book_name_text_view);
        textViewBookName.setText(currentBook.getmName());

        //Book author
        TextView textViewBookAuthor = (TextView) listItemView.findViewById(R.id.book_author_text_view);
        textViewBookAuthor.setText(currentBook.getmAuthor());

        // Return the whole list item layout (containing 2 TextViews) so that it can be shown in
        // the ListView.
        return listItemView;
    }
}
