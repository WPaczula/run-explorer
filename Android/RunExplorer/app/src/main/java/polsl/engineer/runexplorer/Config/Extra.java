package polsl.engineer.runexplorer.Config;

import java.util.HashMap;
import java.util.Map;

import polsl.engineer.runexplorer.Activities.MyRoutesActivity;
import polsl.engineer.runexplorer.Activities.RunActivity;

/**
 * Created by Wojtek on 22.11.2017.
 */

public class Extra {
    public static String routeJSON = "routeJSON";
    public static String parent = "parent";
    public static String myRoutes = "MyRoutes";
    public static String newRoute = "NewRoute";
    public static Map<String, Class> activities;
    static
    {
        activities = new HashMap<>();
        activities.put(newRoute, RunActivity.class);
        activities.put(myRoutes, MyRoutesActivity.class);
    }
}
