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
 * http://stackoverflow.com/questions/3832254/how-can-i-make-my-arrayadapter-follow-the-viewholder-pattern
 */
public class BooksAdapter extends ArrayAdapter<Book> {

    public BooksAdapter(Context context, ArrayList<Book> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        ViewHolder holder; // to reference the child views for later actions

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

            // cache view fields into the holder
            holder = new ViewHolder();
            holder.bookNameText = (TextView) listItemView.findViewById(R.id.book_name_text_view);
            holder.authorNameText = (TextView) listItemView.findViewById(R.id.book_author_text_view);
            // associate the holder with the view for later lookup
            listItemView.setTag(holder);
        } else {
            // view already exists, get the holder instance from the view
            holder = (ViewHolder) listItemView.getTag();
        }
        Book book = getItem(position);
        holder.bookNameText.setText(book.getmName());
        holder.authorNameText.setText(book.getmAuthor());
        // Return the whole list item layout (containing 2 TextViews) so that it can be shown in
        // the ListView.
        return listItemView;
    }

    // somewhere else in your class definition
    static class ViewHolder {
        TextView bookNameText;
        TextView authorNameText;
    }
}
