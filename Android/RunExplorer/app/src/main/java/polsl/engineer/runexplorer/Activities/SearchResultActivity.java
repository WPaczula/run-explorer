package polsl.engineer.runexplorer.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import polsl.engineer.runexplorer.API.Data.RouteListData;
import polsl.engineer.runexplorer.API.Data.RouteTitleData;
import polsl.engineer.runexplorer.API.RESTServiceEndpoints;
import polsl.engineer.runexplorer.API.RetrofitClient;
import polsl.engineer.runexplorer.Config.Connection;
import polsl.engineer.runexplorer.Layout.RouteAdapter;
import polsl.engineer.runexplorer.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultActivity extends AppCompatActivity {

    @BindView(R.id.search_recycler_view)
    public RecyclerView recyclerView;
    private RESTServiceEndpoints endpoints = RetrofitClient.getApiService();
    private int totalRoutesCount=0;
    private String token;
    private String username;
    private List<RouteTitleData> routeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        ButterKnife.bind(this);
        Hawk.init(this).build();

        Intent intent = getIntent();

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
                    getSearchResults(adapter, routeList.size());
                }
            }
        });
        getSearchResults(adapter, 0);
    }

    private void getSearchResults(final RouteAdapter adapter, int skip){
        Call<RouteListData> getUserRoutesCall = endpoints.getUsersRoutes(this.token, this.username, skip);
        getUserRoutesCall.enqueue(new Callback<RouteListData>() {
            @Override
            public void onResponse(Call<RouteListData> call, Response<RouteListData> response) {
                totalRoutesCount = response.body().getTotalCount();
                routeList.addAll(response.body().getRoutes());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<RouteListData> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Cant load routes", Toast.LENGTH_LONG).show();
            }
        });
    }
}
