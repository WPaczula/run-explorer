package polsl.engineer.runexplorer.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import polsl.engineer.runexplorer.API.data.RouteTitleData;
import polsl.engineer.runexplorer.API.data.RouteListData;
import polsl.engineer.runexplorer.API.RESTServiceEndpoints;
import polsl.engineer.runexplorer.API.RetrofitClient;
import polsl.engineer.runexplorer.config.Connection;
import polsl.engineer.runexplorer.R;
import polsl.engineer.runexplorer.layout.RouteAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRoutesActivity extends AppCompatActivity{

    @BindView(R.id.my_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.no_my_rotes_tv)
    TextView noRoutesTextView;
    private RESTServiceEndpoints endpoints = RetrofitClient.getApiService();
    private int totalRoutesCount=0;
    private String token;
    private String username;
    private List<RouteTitleData> routeList = new ArrayList<>();

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(MyRoutesActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_routes);
        ButterKnife.bind(this);
        Hawk.init(this).build();

        token = Hawk.get(Connection.tokenKey);
        username = Hawk.get(Connection.username);

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
        Call<RouteListData> getUserRoutesCall = endpoints.getUsersRoutes(this.token, this.username, skip);
        getUserRoutesCall.enqueue(new Callback<RouteListData>() {
            @Override
            public void onResponse(Call<RouteListData> call, Response<RouteListData> response) {
                if(response.isSuccessful()){
                    totalRoutesCount = response.body().getTotalCount();
                    if(totalRoutesCount > 0){
                        noRoutesTextView.setVisibility(View.GONE);
                    } else {
                        noRoutesTextView.setVisibility(View.VISIBLE);
                    }
                    routeList.addAll(response.body().getRoutes());
                    adapter.notifyDataSetChanged();
                }else {
                    noRoutesTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<RouteListData> call, Throwable t) {
                noRoutesTextView.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Cant load routes", Toast.LENGTH_LONG).show();
            }
        });
    }
}
