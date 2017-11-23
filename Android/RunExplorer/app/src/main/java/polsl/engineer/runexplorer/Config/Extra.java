package polsl.engineer.runexplorer.Config;

import java.util.HashMap;
import java.util.Map;

import polsl.engineer.runexplorer.Activities.MyRoutesActivity;
import polsl.engineer.runexplorer.Activities.RunActivity;

/**
 * Created by Wojtek on 22.11.2017.
 */

public class Extra {
    public final static String routeJSON = "routeJSON";
    public final static String parent = "parent";
    public final static String myRoutes = "MyRoutes";
    public final static String newRoute = "NewRoute";
    public final static String isBeforeRun = "beforeRun";
    public final static String pathJSON = "pathJSON";
    public final static String ID = "id";
    public final static String radius = "radius";
    public final static String minDistance = "minDistance";
    public final static String maxDistance = "maxDistance";
    public final static String username = "username";
    public final static String lat = "lat";
    public final static String lng = "lng";
    public final static Map<String, Class> activities;
    static
    {
        activities = new HashMap<>();
        activities.put(newRoute, RunActivity.class);
        activities.put(myRoutes, MyRoutesActivity.class);
    }
}
