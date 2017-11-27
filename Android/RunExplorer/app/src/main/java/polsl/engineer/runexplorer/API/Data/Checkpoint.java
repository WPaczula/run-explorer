package polsl.engineer.runexplorer.API.data;

import polsl.engineer.runexplorer.entity.StoredCheckpoint;

/**
 * Created by Wojtek on 21.11.2017.
 */

public class Checkpoint {
    private double lat;
    private double lng;

    public Checkpoint(StoredCheckpoint checkpoint){
        this.lat = checkpoint.getLat();
        this.lng = checkpoint.getLng();
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
