package polsl.engineer.runexplorer.API;

import polsl.engineer.runexplorer.config.Connection;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Wojtek on 20.11.2017.
 */

public class RetrofitClient {

    private static Retrofit getRetrofitInstance() {
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(interceptor).build();

        return new Retrofit.Builder()
                .baseUrl(Connection.Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static RESTServiceEndpoints getApiService() {
        return getRetrofitInstance().create(RESTServiceEndpoints.class);
    }
}