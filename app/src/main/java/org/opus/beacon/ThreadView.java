package org.opus.beacon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ThreadView extends Activity {

    private Thread activeThread;
    private String postID;
    private Context context;
    private BeaconRestClient client;
    private CommentAdapter mAdapter;
    private static final String TAG = "ThreadView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thread_view);
        Intent activityThatCalled = getIntent();
        int beaconID = activityThatCalled.getExtras().getInt("beaconID");
        postID = Integer.toString(beaconID);
        context = this;
        try {
            Auth auth = new Auth(context);
            client = new BeaconRestClient(auth.getId(), auth.getSecret());
        } catch(Exception e) {
            finish();
            return;
        }
        new GetActiveThread().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void onCommentHeart (View view)
    {
        HeartButton heart = (HeartButton) view;
        int position = (int) heart.getTag();
        Thread.Comment[] comments = activeThread.getComments();
        Thread.Comment heartedComment = comments[position];
        int numHearts = heartedComment.getHearts();
        String action;
        if (heart.isHearted()) {
            heart.unheart();
            heartedComment.setHearted(false);
            numHearts--;
            action = "unheart";
        } else {
            heart.heart();
            heartedComment.setHearted(true);
            numHearts++;
            action = "heart";
        }
        heartedComment.setHearts(numHearts);
        new heartPost().execute(Integer.toString(heartedComment.getId()), action);
        mAdapter.notifyDataSetChanged();
    }

    public void onThreadHeart (View view) {
        HeartButton heart = (HeartButton) view;
        int numHearts = activeThread.getHearts();
        String action;
        if (heart.isHearted()) {
            heart.unheart();
            numHearts--;
            action = "unheart";
        } else {
            heart.heart();
            numHearts++;
            action = "heart";
        }
        activeThread.setHearts(numHearts);
        TextView heartNumText = (TextView) findViewById(R.id.numThreadHearts);
        heartNumText.setText(Integer.toString(activeThread.getHearts()));
        new heartPost().execute(Integer.toString(activeThread.getId()), action);
        mAdapter.notifyDataSetChanged();
    }

    private class GetActiveThread extends AsyncTask <Void, Void, Thread> {
        private boolean loaded = false;
        @Override
        protected Thread doInBackground(Void... params) {
            try {
                Thread t = client.getThread(postID);
                loaded = true;
                return t;
            } catch(final RestException e) {
                if (e.shouldInformUser()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT);
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            v.setTextColor(Color.WHITE);
                            v.setBackgroundColor(0x00000000);
                            toast.show();
                        }
                    });
                    finish();
                }
                return new Thread();
            }
        }

        @Override
        protected void onPostExecute(Thread s) {
            if (loaded) {
                ImageView contentValue = (ImageView) findViewById(R.id.threadImage);
                TextView threadDesc = (TextView) findViewById(R.id.threadDesc);
                TextView threadUser = (TextView) findViewById(R.id.threadUser);
                TextView numHearts = (TextView) findViewById(R.id.numThreadHearts);
                LinearLayout beaconContainer = (LinearLayout) findViewById(R.id.beaconContainer);
                Bitmap threadImage = s.getImage();
                float aspect = (float) threadImage.getHeight() / (float) threadImage.getWidth();
                float newHeight = beaconContainer.getWidth() * aspect;
                if (threadImage != null) {
                    contentValue.setImageBitmap(threadImage);
                }
                threadDesc.setText(s.getText());
                threadUser.setText(s.getUsername());
                numHearts.setText(Integer.toString(s.getHearts()));
                activeThread = s;
                mAdapter = new CommentAdapter(context, activeThread.getComments());
                ListView comments = (ListView) findViewById(R.id.commentListView);
                comments.setAdapter(mAdapter);
            }
        }
    }

    private class heartPost extends AsyncTask <String,Void,RestException> {
        @Override
        protected RestException doInBackground(String... params){
            try {
                if (params[1] == "heart")
                    client.heartPost(params[0]);
                else
                    client.unheartPost(params[0]);
                return null;
            } catch (RestException e) {
                return e;
            }
        }
        @Override
        protected void onPostExecute(RestException err) {
            if(err != null && err.shouldInformUser()) {
                Toast toast = Toast.makeText(context, err.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
