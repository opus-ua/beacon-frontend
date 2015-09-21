package org.opus.beacon;

import android.location.Location;
import android.media.Image;

/**
 * Created by connorhamblett on 9/13/15.
 */
public class Beacon {
    private Location geoLocation;
    private double popularity;
    private String description;
    private String user;
    private Image image;

    public void editDescription(String newDescription)
    {
        this.description = newDescription;
    }

    public void updatePopularity(double newPopularity)
    {
        this.popularity = newPopularity;
    }

    public Beacon(Location location, String newDescription, String newUser, Image newImage)
    {
        this.geoLocation = location;
        this.description = newDescription;
        this.user = newUser;
        this.image = newImage;
        this.popularity = 0.0;
    }


}
