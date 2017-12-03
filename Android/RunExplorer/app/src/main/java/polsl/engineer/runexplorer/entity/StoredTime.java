package polsl.engineer.runexplorer.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Wojtek on 03.12.2017.
 */

@Entity
public class StoredTime {
    private int time;
    private long routeId;
    @Generated(hash = 803251856)
    public StoredTime(int time, long routeId) {
        this.time = time;
        this.routeId = routeId;
    }
    @Generated(hash = 579939582)
    public StoredTime() {
    }
    public int getTime() {
        return this.time;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public long getRouteId() {
        return this.routeId;
    }
    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }
}
