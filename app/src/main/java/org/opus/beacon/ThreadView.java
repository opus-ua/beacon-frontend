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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ThreadView extends Activity {

    private Thread activeThread;
    private String postID;
    private Context context;
    private BeaconRestClient mClient;
    private CommentAdapter mAdapter;
    private int mBeaconID;
    private View mThreadHeader = null;
    private static final String TAG = "ThreadView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thread_view);
        Intent activityThatCalled = getIntent();
        mBeaconID = activityThatCalled.getExtras().getInt("beaconID");
        postID = Integer.toString(mBeaconID);
        context = this;
        try {
            Auth auth = new Auth(context);
            mClient = new BeaconRestClient(auth.getId(), auth.getSecret());
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
        TextView heartNumText = (TextView) findViewById(R.id.numHeaderHearts);
        heartNumText.setText(Integer.toString(activeThread.getHearts()));
        new heartPost().execute(Integer.toString(activeThread.getId()), action);
        mAdapter.notifyDataSetChanged();
    }

    public void submitComment(View view) {
        final EditText commentTextInput = (EditText) findViewById(R.id.comment_textbox);
        String commentText = commentTextInput.getText().toString();

        if (commentText == "")
            return;

        commentTextInput.setText("");
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentTextInput.getWindowToken(), 0);
        JsonMsg.PostCommentRequest commReq = new JsonMsg.PostCommentRequest(mBeaconID, commentText);
        new PostComment().execute(commReq);
    }

    private void toastError(String err) {
        Toast toast = Toast.makeText(context, err, Toast.LENGTH_SHORT);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setTextColor(Color.WHITE);
        v.setBackgroundColor(0x00000000);
        toast.show();
    }

    private class PostComment extends AsyncTask<JsonMsg.PostCommentRequest, Void, RestException> {
        @Override
        protected RestException doInBackground(JsonMsg.PostCommentRequest... params) {
           try {
                mClient.postComment(params[0]);
                return null;
           } catch (RestException err) {
                return err;
           }
        }

        @Override
        protected void onPostExecute(RestException err) {
            if (err == null) {
                new GetActiveThread().execute();
            } else if(err != null && err.shouldInformUser()) {
                toastError(err.getMessage());
            }
        }
    }

    private class GetActiveThread extends AsyncTask <Void, Void, Thread> {
        private boolean loaded = false;
        @Override
        protected Thread doInBackground(Void... params) {
            try {
                Thread t = mClient.getThread(postID);
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
        protected void onPostExecute(Thread thread) {
            Bitmap threadImage = thread.getImage();
            activeThread = thread;
            mAdapter = new CommentAdapter(context, activeThread.getComments());
            ListView comments = (ListView) findViewById(R.id.commentListView);
            View header = (View) getLayoutInflater().inflate(R.layout.header, null);
            ImageView threadImageView = (ImageView) header.findViewById(R.id.headerImage);
            TextView threadDesc = (TextView) header.findViewById(R.id.headerDesc);
            TextView threadUser = (TextView) header.findViewById(R.id.headerUser);
            TextView numHearts = (TextView) header.findViewById(R.id.numHeaderHearts);
            threadImageView.setImageBitmap(threadImage);
            threadDesc.setText(thread.getText());
            threadUser.setText(thread.getUsername());
            numHearts.setText(Integer.toString(thread.getHearts()));

            if (mThreadHeader != null) {
                comments.removeHeaderView(mThreadHeader);
            }
            comments.addHeaderView(header);
            mThreadHeader = header;
            comments.setAdapter(mAdapter);
            if (thread.getHearted()) {
                HeartButton threadHeart = (HeartButton) findViewById(R.id.headerHeart);
                threadHeart.heart();
            }
        }
    }

    private class heartPost extends AsyncTask <String,Void,RestException> {
        @Override
        protected RestException doInBackground(String... params){
            try {
                if (params[1] == "heart")
                    mClient.heartPost(params[0]);
                else
                    mClient.unheartPost(params[0]);
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
