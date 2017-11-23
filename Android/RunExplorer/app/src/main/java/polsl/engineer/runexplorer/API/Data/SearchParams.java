package polsl.engineer.runexplorer.API.Data;

/**
 * Created by Wojtek on 23.11.2017.
 */

public class SearchParams {

    private Integer maxDistance;
    private Integer minDistance;
    private Integer radius;
    private Double lat;
    private Double lng;
    private String username;

    public SearchParams(Integer maxDistance, Integer minDistance, Integer radius, Double lat, Double lng, String username) {
        this.maxDistance = maxDistance;
        this.minDistance = minDistance;
        this.radius = radius;
        this.lat = lat;
        this.lng = lng;
        this.username = username;
    }

    public Integer getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(Integer maxDistance) {
        this.maxDistance = maxDistance;
    }

    public Integer getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(Integer minDistance) {
        this.minDistance = minDistance;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
