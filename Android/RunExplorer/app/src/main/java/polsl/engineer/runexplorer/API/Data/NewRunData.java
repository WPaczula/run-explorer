package polsl.engineer.runexplorer.API.Data;

import java.util.List;

/**
 * Created by Wojtek on 22.11.2017.
 */

public class NewRunData {

    private String routeId;
    private String name;
    private List<Integer> times;
    private long date;
    private int time;

    public NewRunData(RouteData data){
        this.routeId = data.getId();
        this.name = data.getName();
        this.times = data.getTimes();
        this.date = data.getDate();
        this.time = data.getTime();
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getTimes() {
        return times;
    }

    public void setTimes(List<Integer> times) {
        this.times = times;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }


}
