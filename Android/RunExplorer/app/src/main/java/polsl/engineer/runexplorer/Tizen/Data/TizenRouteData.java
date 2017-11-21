package polsl.engineer.runexplorer.Tizen.Data;

import java.util.List;

/**
 * Created by Wojtek on 21.11.2017.
 */

public class TizenRouteData {
    private List<TizenCheckpoint> checkpoints;
    private int distance;
    private int time;
    private boolean shouldBeSavedAsNew;

    public List<TizenCheckpoint> getCheckpointList() {
        return checkpoints;
    }

    public void setCheckpointList(List<TizenCheckpoint> checkpointList) {
        this.checkpoints = checkpointList;
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
