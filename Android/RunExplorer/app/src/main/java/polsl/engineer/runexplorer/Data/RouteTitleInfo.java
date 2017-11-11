package polsl.engineer.runexplorer.Data;


import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by Wojtek on 11.11.2017.
 */

public class RouteTitleInfo {

    private String id;
    private String name;
    private Date date;
    private Timestamp time;
    private int distance;

    public RouteTitleInfo(String id, String name, Date date, Timestamp time, int distance) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public Timestamp getTime() {
        return time;
    }

    public int getDistance() {
        return distance;
    }
}
