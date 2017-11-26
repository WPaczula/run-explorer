package polsl.engineer.runexplorer.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import polsl.engineer.runexplorer.API.Data.Message;
import polsl.engineer.runexplorer.API.Data.NewRouteData;
import polsl.engineer.runexplorer.API.Data.NewRunData;
import polsl.engineer.runexplorer.API.Data.RouteData;
import polsl.engineer.runexplorer.API.Data.SetPathAction;
import polsl.engineer.runexplorer.API.RESTServiceEndpoints;
import polsl.engineer.runexplorer.API.RetrofitClient;
import polsl.engineer.runexplorer.Config.Connection;
import polsl.engineer.runexplorer.Config.Extra;
import polsl.engineer.runexplorer.R;
import polsl.engineer.runexplorer.Layout.TimeAdapter;
import polsl.engineer.runexplorer.Utility.TimeConverter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoutePreviewActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    @BindView(R.id.distance_value_tv)
    public TextView distanceValue;
    @BindView(R.id.time_value_tv)
    public TextView timeValue;
    @BindView(R.id.save_route_btn)
    public Button saveButton;
    @BindView(R.id.challenge_route_btn)
    public Button challengeButton;
    @BindView(R.id.back_btn)
    public Button backButton;
    private RouteData routeData;
    private Gson gson = new Gson();
    private RESTServiceEndpoints endpoints = RetrofitClient.getApiService();

    @SuppressLint("SetTextI18n")
    private void initUI() {
        distanceValue.setText(String.valueOf(routeData.getDistance() / 1000f) + "km");
        timeValue.setText(TimeConverter.convertToTimeString(routeData.getTime()));
        RecyclerView timesRecyclerView = (RecyclerView) findViewById(R.id.times_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        timesRecyclerView.setLayoutManager(linearLayoutManager);
        setTitle(routeData.getName());
        TimeAdapter adapter = new TimeAdapter(this, routeData.getTimes());
        timesRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_preview);
        ButterKnife.bind(this);
        Hawk.init(this).build();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.preview_map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        String json = intent.getStringExtra(Extra.routeJSON);
        routeData = gson.fromJson(json, RouteData.class);
        boolean isBeforeRun = intent.getBooleanExtra(Extra.isBeforeRun, true);
        if(isBeforeRun){
            challengeButton.setVisibility(View.VISIBLE);
        }else{
            saveButton.setVisibility(View.VISIBLE);
        }

        initUI();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(routeData.getLatLngs().size() != 0){
            mMap.addPolyline(drawPolyline(routeData.getLatLngs()));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for(LatLng point : routeData.getLatLngs()){
                builder.include(point);
            }

            final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 20);
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.animateCamera(cameraUpdate);
                }
            });
        }else{
            Toast.makeText(this, "Not enough points for this path", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(RoutePreviewActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private PolylineOptions drawPolyline(List<LatLng> routePoints){
        PolylineOptions polylineOptions = new PolylineOptions();
        for(LatLng point : routePoints){
            polylineOptions.add(point);
        }
        polylineOptions.width(20.0f);
        return polylineOptions;
    }

    @OnClick(R.id.save_route_btn)
    public void saveRoute(View view){
        String token = Hawk.get(Connection.tokenKey);
        String username = Hawk.get(Connection.username);
        if(routeData.isNew()){
            Call<Message> addRouteCallback = endpoints.saveRoute(token, new NewRouteData(routeData, Calendar.getInstance().getTime().getTime()));
            addRouteCallback.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    saveButton.setVisibility(View.GONE);
                    backButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Cant save run", Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Call<Message> addRunCallback = endpoints.saveRun(token, username, new NewRunData(routeData));
            addRunCallback.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    saveButton.setVisibility(View.GONE);
                    backButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(Call<Message> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Cant save run", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @OnClick(R.id.back_btn)
    public void backToMain(View view){
        Intent intent = new Intent(RoutePreviewActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.challenge_route_btn)
    public void startRoute(View view){
        Intent intent = new Intent(RoutePreviewActivity.this, RunActivity.class);
        SetPathAction action = new SetPathAction("start", routeData.getCheckpoints());
        String json = gson.toJson(action);
        intent.putExtra(Extra.pathJSON, json);
        intent.putExtra(Extra.ID, routeData.getId());
        startActivity(intent);
    }
}
