package org.opus.beacon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;


public class ThreadView extends Activity {

    private Thread activeThread;
    private String postID;
    private User user;
    private Context context;
    private RestClient backend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thread_view);
        Intent activityThatCalled = getIntent();
        int beaconID = activityThatCalled.getExtras().getInt("beaconID");
        postID = Integer.toString(beaconID);
        context = this;
        backend = new RestClient();
        new GetActiveThread().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new GetActiveThread().execute();
    }

    public void onCommentHeart (View view)
    {
        ImageButton heart = (ImageButton) view;
        int position = (int) heart.getTag();
        Thread.Comment[] comments = activeThread.getComments();
        Thread.Comment heartedComment = comments[position];
        heart.setColorFilter(Color.rgb(255,106,106));
        heartedComment.setHearts(1 + heartedComment.getHearts());
        TextView hearts = (TextView) findViewById(R.id.numCommentHearts);
        hearts.setText(Integer.toString(heartedComment.getHearts()));
        heart.setEnabled(false);
        new heartPost().execute(Integer.toString(heartedComment.getId()));
    }

    public void onThreadHeart (View view) {
        ImageButton heart = (ImageButton) view;
        heart.setColorFilter(Color.rgb(255, 106, 106));
        activeThread.setHearts(1 + activeThread.getHearts());
        TextView hearts = (TextView) findViewById(R.id.numHeaderHearts);
        hearts.setText(Integer.toString(activeThread.getHearts()));
        heart.setEnabled(false);
        new heartPost().execute(Integer.toString(activeThread.getId()));
    }

    private class GetActiveThread extends AsyncTask <Void, Void, Thread> {
        @Override
        protected Thread doInBackground(Void... params) {
            return backend.getThread(postID);
        }

        @Override
        protected void onPostExecute(Thread s) {
            Bitmap threadImage = s.getImage();
            activeThread = s;
            CommentAdapter adapter = new CommentAdapter(context, activeThread.getComments());
            ListView comments = (ListView) findViewById(R.id.commentListView);
            View header = (View)getLayoutInflater().inflate(R.layout.header, null);
            ImageView threadImageView = (ImageView) header.findViewById(R.id.headerImage);
            TextView threadDesc = (TextView) header.findViewById(R.id.headerDesc);
            TextView threadUser = (TextView) header.findViewById(R.id.headerUser);
            TextView numHearts = (TextView) header.findViewById(R.id.numHeaderHearts);
            threadImageView.setImageBitmap(threadImage);
            threadDesc.setText(s.getText());
            threadUser.setText(Integer.toString(s.getUser()));
            numHearts.setText(Integer.toString(s.getHearts()));
            comments.addHeaderView(header);
            comments.setAdapter(adapter);
        }
    }

    private class heartPost extends AsyncTask <String,Void,String> {
        @Override
        protected String doInBackground(String... params){
            String result = backend.heartPost(params[0],context);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            Toast toast = Toast.makeText(context, result, Toast.LENGTH_SHORT);
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.WHITE);
            v.setBackgroundColor(0x00000000);
            toast.show();
        }
    }
}
