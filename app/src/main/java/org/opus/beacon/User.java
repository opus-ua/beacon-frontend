package org.opus.beacon;

/**
 * Created by connorhamblett on 11/5/15.
 */
public class User {
    private String username;
    private double userID;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
    public User(String username, double userID) {
        this.username = username;
        this.userID = userID;
    }
}
