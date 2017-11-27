package polsl.engineer.runexplorer.API.data;

import java.util.List;

/**
 * Created by Wojtek on 23.11.2017.
 */

public class NewRouteData {
    public NewRouteData(RouteData data) {
        this.points = data.getCheckpoints();
        this.name = data.getName();
        this.time = data.getTime();
        this.distance = data.getDistance();
        this.times = data.getTimes();
        this.date = data.getDate();
    }

    private List<Checkpoint> points;
    private String name;
    private int time;
    private int distance;
    private List<Integer> times;
    private long date;

    public List<Checkpoint> getPoints() {
        return points;
    }

    public void setPoints(List<Checkpoint> points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
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
}
