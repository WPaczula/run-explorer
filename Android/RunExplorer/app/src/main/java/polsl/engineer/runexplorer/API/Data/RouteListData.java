package polsl.engineer.runexplorer.API.data;

import java.util.List;

/**
 * Created by Wojtek on 21.11.2017.
 */

public class RouteListData {

    private int totalCount;
    private List<RouteTitleData> routes;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<RouteTitleData> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteTitleData> routes) {
        this.routes = routes;
    }

}
