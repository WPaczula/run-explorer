package polsl.engineer.runexplorer.Tizen.Data;

/**
 * Created by Wojtek on 21.11.2017.
 */

public class TizenCheckpoint {
    private int time;
    private double lat;
    private double lng;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
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
