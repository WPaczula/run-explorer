package polsl.engineer.runexplorer.tizen.data;

import java.util.List;

import polsl.engineer.runexplorer.API.data.Checkpoint;

/**
 * Created by Wojtek on 21.11.2017.
 */

public class TizenRouteData {
    private List<Checkpoint> checkpoints;
    private List<Integer> times;
    private int distance;
    private int time;
    private boolean shouldBeSavedAsNew;

    public List<Integer> getTimes() {
        return times;
    }

    public void setTimes(List<Integer> times) {
        this.times = times;
    }

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(List<Checkpoint> checkpoints) {
        this.checkpoints = checkpoints;
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

    public boolean isShouldBeSavedAsNew() {
        return shouldBeSavedAsNew;
    }

    public void setShouldBeSavedAsNew(boolean shouldBeSavedAsNew) {
        this.shouldBeSavedAsNew = shouldBeSavedAsNew;
    }
}
