package polsl.engineer.runexplorer.API;

import java.util.List;

import polsl.engineer.runexplorer.API.Data.JWT;
import polsl.engineer.runexplorer.API.Data.Message;
import polsl.engineer.runexplorer.API.Data.NewRunData;
import polsl.engineer.runexplorer.API.Data.RouteData;
import polsl.engineer.runexplorer.API.Data.RouteTitleData;
import polsl.engineer.runexplorer.API.Data.User;
import polsl.engineer.runexplorer.API.Data.UserRoutes;
import polsl.engineer.runexplorer.API.Data.NewRouteData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Wojtek on 20.11.2017.
 */

public interface RESTServiceEndpoints {
    @Headers("content-type: application/json")
    @POST("/Authenticate")
    Call<JWT> authenticate(@Body User user);

    @Headers("content-type: application/json")
    @POST("/SignUp")
    Call<Message> signUp(@Body User user);

    @Headers("content-type: application/json")
    @GET("/Routes/{username}")
    Call<UserRoutes> getUsersRoutes(@Header("Authorization") String token,
                                    @Path("username") String username,
                                    @Query("skip") int skip);

    @Headers("content-type: application/json")
    @GET("/Routes")
    Call<RouteData> getRoute(@Header("Authorization") String token,
            @Query("routeId") String id);

    @Headers("content-type: application/json")
    @POST("/Routes/{username}")
    Call<Message> saveRun(@Header("Authorization") String token,
                          @Path("username") String username,
                          @Body NewRunData data);

    @Headers("content-type: application/json")
    @POST("/Routes")
    Call<Message> saveRoute(@Header("Authorization") String token,
                            @Body NewRouteData data);

    @Headers("content-type: application/json")
    @GET("/Search")
    Call<List<RouteTitleData>> search(@Header("Authorization") String token,
                                      @Query("maxDistance") int maxDistance,
                                      @Query("minDistance") int minDistance,
                                      @Query("username") String username,
                                      @Query("lat") double lat,
                                      @Query("lng") double lng,
                                      @Query("radius") int radius,
                                      @Query("skip") int skip);
}
