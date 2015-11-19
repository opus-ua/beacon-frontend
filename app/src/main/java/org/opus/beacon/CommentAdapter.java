package org.opus.beacon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class CommentAdapter extends ArrayAdapter<Thread.Comment> {
    public CommentAdapter (Context context, Thread.Comment[] comments) {
        super(context, R.layout.comment_layout, comments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View theView = inflater.inflate(R.layout.comment_layout, parent, false);

        Thread.Comment comment = getItem(position);
        TextView commentText = (TextView) theView.findViewById(R.id.commentText);
        TextView commentUser = (TextView) theView.findViewById(R.id.commentUser);
        TextView numberHearts = (TextView) theView.findViewById(R.id.numCommentHearts);
        HeartButton heart = (HeartButton) theView.findViewById(R.id.commentHeart);

        if (comment.getHearted())
            heart.heart();

        commentText.setText(comment.getText());
        commentUser.setText(comment.getUsername());
        numberHearts.setText(Integer.toString(comment.getHearts()));
        heart.setImageResource(R.drawable.filled_heart);
        heart.setTag(position);
        return theView;
    }
}
