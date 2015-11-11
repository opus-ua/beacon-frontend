package org.opus.beacon;

import android.graphics.Bitmap;
import android.location.Location;

public class Beacon {
    private Location geoLocation;
    private double popularity;
    private String description;
    private User owner;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    private Bitmap image;
    private double postID;

    public void editDescription(String newDescription) {
        this.description = newDescription;
    }

    public void updatePopularity(double newPopularity)
    {
        this.popularity = newPopularity;
    }


    public Beacon(Location location, String newDescription, User newOwner, Bitmap newImage, double postID)
    {
        this.geoLocation = location;
        this.description = newDescription;
        this.owner = newOwner;
        this.image = newImage;
        this.popularity = 0.0;
        this.postID = postID;
    }


}
