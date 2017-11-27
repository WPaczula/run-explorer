package polsl.engineer.runexplorer.API.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import polsl.engineer.runexplorer.entity.CheckpointConverter;
import polsl.engineer.runexplorer.entity.StoredRoute;
import polsl.engineer.runexplorer.tizen.data.TizenRouteData;

/**
 * Created by Wojtek on 22.11.2017.
 */

public class RouteData {

    private boolean isNew;
    private String id;
    private String name;
    private int distance;
    private long date;
    private int time;
    private List<Integer> times;
    private List<Checkpoint> checkpoints;

    public RouteData(TizenRouteData tizenRouteData, long date) {
        this.isNew = tizenRouteData.isShouldBeSavedAsNew();
        this.name = "New run";
        this.distance = tizenRouteData.getDistance();
        this.time = tizenRouteData.getTime();
        this.times = tizenRouteData.getTimes();
        this.date = date;
        this.checkpoints = tizenRouteData.getCheckpoints();
    }

    public RouteData(StoredRoute storedRoute){
        this.isNew = storedRoute.getIsNew();
        this.name = storedRoute.getName();
        this.distance = storedRoute.getDistance();
        this.id = storedRoute.getRouteId();
        this.time = storedRoute.getTime();
        Type listType = new TypeToken<ArrayList<Integer>>(){}.getType();
        this.times = new Gson().fromJson(storedRoute.getTimes(), listType);
        this.checkpoints = CheckpointConverter.getCheckpoints(storedRoute.getCheckpoints());
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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

    public List<Checkpoint> getCheckpoints(){
        return checkpoints;
    }

    public List<LatLng> getLatLngs() {
        List<LatLng> latLngList = new ArrayList<>();
        for(Checkpoint checkpoint : checkpoints){
            latLngList.add(new LatLng(checkpoint.getLat(), checkpoint.getLng()));
        }
        return latLngList;
    }

    public void setCheckpoints(List<Checkpoint> checkpoints) {
        this.checkpoints = checkpoints;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
