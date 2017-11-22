package polsl.engineer.runexplorer.API.Data;

import java.util.List;

/**
 * Created by Wojtek on 23.11.2017.
 */

public class SetPathAction {

    private String type;
    private List<Checkpoint> route;

    public SetPathAction(String type, List<Checkpoint> checkpoints){
        this.type = type;
        this.route = checkpoints;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Checkpoint> getRoute() {
        return route;
    }

    public void setRoute(List<Checkpoint> route) {
        this.route = route;
    }
}
