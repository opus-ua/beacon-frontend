package org.opus.beacon;

import android.content.Context;
import android.content.SharedPreferences;

public class Auth {
    public Auth(Context context) throws Exception {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key),
                context.MODE_PRIVATE);
        userId = prefs.getInt(context.getString(R.string.preference_user_id), -1);
        userSecret = prefs.getString(context.getString(R.string.preference_secret), null);
        username = prefs.getString(context.getString(R.string.preference_username), null);

        if (userId == -1 || userSecret == null || username == null)
            throw new Exception("Not authenticated.");
    }

    public User toUser() {
        return new User(username, userId);
    }

    private int userId;
    private String userSecret;
    private String username;

    public String getId() {return Integer.toString(userId);}
    public String getSecret() {return userSecret;}
    public int getIntId() {return userId;}
}
