package polsl.engineer.runexplorer.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

import polsl.engineer.runexplorer.API.data.Checkpoint;

/**
 * Created by Wojtek on 27.11.2017.
 */

@Entity
public class StoredCheckpoint {
    private double lng;
    private double lat;
    private long routeId;
    @Generated(hash = 86580346)
    public StoredCheckpoint(double lng, double lat, long routeId) {
        this.lng = lng;
        this.lat = lat;
        this.routeId = routeId;
    }
    @Generated(hash = 844039145)
    public StoredCheckpoint() {
    }
    public StoredCheckpoint(Checkpoint checkpoint, long routeId){
        this.lat = checkpoint.getLat();
        this.lng = checkpoint.getLng();
        this.routeId = routeId;
    }
    public double getLng() {
        return this.lng;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
    public double getLat() {
        return this.lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public long getRouteId() {
        return this.routeId;
    }
    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }
}
