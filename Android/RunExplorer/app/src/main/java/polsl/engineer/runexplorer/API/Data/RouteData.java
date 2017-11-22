package polsl.engineer.runexplorer.API.Data;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import polsl.engineer.runexplorer.Tizen.Data.TizenRouteData;

/**
 * Created by Wojtek on 22.11.2017.
 */

public class RouteData {

    private boolean isNew;
    private String name;
    private int distance;
    private int time;
    private List<Integer> times;
    private List<Checkpoint> checkpoints;

    public RouteData(TizenRouteData tizenRouteData) {
        this.isNew = tizenRouteData.isShouldBeSavedAsNew();
        this.name = "New run";
        this.distance = tizenRouteData.getDistance();
        this.time = tizenRouteData.getTime();
        this.times = tizenRouteData.getTimes();
        this.checkpoints = tizenRouteData.getCheckpoints();
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public List<Integer> getTimes() {
        return times;
    }

    public void setTimes(List<Integer> times) {
        this.times = times;
    }

    public List<LatLng> getCheckpoints() {
        List<LatLng> latLngList = new ArrayList<>();
        for(Checkpoint checkpoint : checkpoints){
            latLngList.add(new LatLng(checkpoint.getLat(), checkpoint.getLng()));
        }
        return latLngList;
    }

    public void setCheckpoints(List<Checkpoint> checkpoints) {
        this.checkpoints = checkpoints;
    }

}
