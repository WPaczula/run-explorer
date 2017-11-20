package polsl.engineer.runexplorer.API;

import polsl.engineer.runexplorer.API.Data.JWT;
import polsl.engineer.runexplorer.API.Data.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Wojtek on 20.11.2017.
 */

public interface RESTServiceEndpoints {
    @Headers("content-type: application/json")
    @POST("/Authenticate")
    Call<JWT> authenticate(@Body User user);
}
