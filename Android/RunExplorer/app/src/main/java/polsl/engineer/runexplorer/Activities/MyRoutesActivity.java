package polsl.engineer.runexplorer.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import polsl.engineer.runexplorer.API.Data.RouteBasicData;
import polsl.engineer.runexplorer.API.Data.UserRoutes;
import polsl.engineer.runexplorer.API.RESTServiceEndpoints;
import polsl.engineer.runexplorer.API.RetrofitClient;
import polsl.engineer.runexplorer.Config.API;
import polsl.engineer.runexplorer.R;
import polsl.engineer.runexplorer.Layout.RouteAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRoutesActivity extends AppCompatActivity{

    @BindView(R.id.my_recycler_view)
    RecyclerView recyclerView;
    private RESTServiceEndpoints endpoints = RetrofitClient.getApiService();
    private int totalRoutesCount=0;
    private String token;
    private String username;
    private List<RouteBasicData> routeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_routes);
        ButterKnife.bind(this);
        Hawk.init(this).build();

        token = Hawk.get(API.tokenKey);
        username = Hawk.get(API.username);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        final RouteAdapter adapter = new RouteAdapter(this, routeList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(linearLayoutManager.findLastCompletelyVisibleItemPosition() == routeList.size()-1 && routeList.size() != totalRoutesCount){
                    getRoutes(adapter, routeList.size());
                }
            }
        });
        getRoutes(adapter, 0);
    }

    private void getRoutes(final RouteAdapter adapter, int skip){
        Call<UserRoutes> getUserRoutesCall = endpoints.getUsersRoutes(this.token, this.username, skip);
        getUserRoutesCall.enqueue(new Callback<UserRoutes>() {
            @Override
            public void onResponse(Call<UserRoutes> call, Response<UserRoutes> response) {
                totalRoutesCount = response.body().getTotalCount();
                routeList.addAll(response.body().getRoutes());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<UserRoutes> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Cant load routes", Toast.LENGTH_LONG).show();
            }
        });
    }
}
