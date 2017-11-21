package polsl.engineer.runexplorer.API.Data;

import java.util.List;

/**
 * Created by Wojtek on 21.11.2017.
 */

public class UserRoutes {

    private int totalCount;
    private List<RouteBasicData> routes;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<RouteBasicData> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteBasicData> routes) {
        this.routes = routes;
    }

}
