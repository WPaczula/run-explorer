package polsl.engineer.runexplorer.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import polsl.engineer.runexplorer.API.data.RouteListData;
import polsl.engineer.runexplorer.API.data.RouteTitleData;
import polsl.engineer.runexplorer.API.data.SearchParams;
import polsl.engineer.runexplorer.API.RESTServiceEndpoints;
import polsl.engineer.runexplorer.API.RetrofitClient;
import polsl.engineer.runexplorer.config.Connection;
import polsl.engineer.runexplorer.config.Extra;
import polsl.engineer.runexplorer.layout.FoundRouteAdapter;
import polsl.engineer.runexplorer.layout.RouteAdapter;
import polsl.engineer.runexplorer.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultActivity extends AppCompatActivity {

    @BindView(R.id.search_recycler_view)
    public RecyclerView recyclerView;
    @BindView(R.id.no_found_routes_tv)
    public TextView noRoutesFoundTextView;
    private RESTServiceEndpoints endpoints = RetrofitClient.getApiService();
    private int totalRoutesCount=0;
    private String token;
    private List<RouteTitleData> routeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        ButterKnife.bind(this);
        Hawk.init(this).build();

        Intent intent = getIntent();
        Gson gson = new Gson();
        String paramsJSON = intent.getStringExtra(Extra.searchParams);
        final SearchParams params = gson.fromJson(paramsJSON, SearchParams.class);
        token = Hawk.get(Connection.tokenKey);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        final FoundRouteAdapter adapter = new FoundRouteAdapter(this, routeList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(linearLayoutManager.findLastCompletelyVisibleItemPosition() == routeList.size()-1 && routeList.size() != totalRoutesCount){
                    getSearchResults(adapter, routeList.size(), params);
                }
            }
        });
        getSearchResults(adapter, 0, params);
    }

    private void getSearchResults(final FoundRouteAdapter adapter, int skip, SearchParams params){
        Call<RouteListData> getSearchResults = endpoints.search(token,
                                                                params.getMaxDistance(),
                                                                params.getMinDistance(),
                                                                params.getUsername(),
                                                                params.getLat(),
                                                                params.getLng(),
                                                                params.getRadius(),
                                                                skip);
        getSearchResults.enqueue(new Callback<RouteListData>() {
            @Override
            public void onResponse(Call<RouteListData> call, Response<RouteListData> response) {
                if(response.isSuccessful()){
                    totalRoutesCount = response.body().getTotalCount();
                    if(totalRoutesCount > 0){
                        noRoutesFoundTextView.setVisibility(View.GONE);
                    } else {
                        noRoutesFoundTextView.setVisibility(View.VISIBLE);
                    }
                    routeList.addAll(response.body().getRoutes());
                    adapter.notifyDataSetChanged();
                }else {
                    noRoutesFoundTextView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<RouteListData> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Cant load routes", Toast.LENGTH_LONG).show();
                noRoutesFoundTextView.setVisibility(View.VISIBLE);
            }
        });
    }
}
