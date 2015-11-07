package org.opus.beacon;

import android.location.Location;
import android.media.Image;

public class Beacon {
    private Location geoLocation;
    private double popularity;
    private String description;
    private User owner;

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    private Image image;
    private double postID;

    public void editDescription(String newDescription) {
        this.description = newDescription;
    }

    public void updatePopularity(double newPopularity)
    {
        this.popularity = newPopularity;
    }


    public Beacon(Location location, String newDescription, User newOwner, Image newImage)
    {
        this.geoLocation = location;
        this.description = newDescription;
        this.owner = newOwner;
        this.image = newImage;
        this.popularity = 0.0;
        this.ID = getNewIDFromServer();
    }


}
