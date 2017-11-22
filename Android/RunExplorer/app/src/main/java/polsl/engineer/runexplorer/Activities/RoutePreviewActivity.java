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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import polsl.engineer.runexplorer.API.Data.RouteData;
import polsl.engineer.runexplorer.Config.Extra;
import polsl.engineer.runexplorer.R;
import polsl.engineer.runexplorer.Layout.TimeAdapter;
import polsl.engineer.runexplorer.Utility.TimeConverter;

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
    private RouteData routeData;
    private Class parentActivity;
    private Gson gson = new Gson();

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(RoutePreviewActivity.this, MainActivity.class);
        if(parentActivity.equals(MyRoutesActivity.class)){
            intent = new Intent(RoutePreviewActivity.this, MyRoutesActivity.class);
        }
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    private void initUI() {
        distanceValue.setText(String.valueOf(routeData.getDistance() / 1000) + "km");
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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.preview_map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        String json = intent.getStringExtra(Extra.routeJSON);
        parentActivity = Extra.activities.get(intent.getStringExtra(Extra.parent));
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

        // Add a marker in Sydney and move the camera
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
        if(routeData.isNew()){
            //TODO: addroute
        }else{
            //TODO: addrun
        }
    }

    @OnClick(R.id.challenge_route_btn)
    public void startRoute(View view){
        Intent intent = new Intent(RoutePreviewActivity.this, RunActivity.class);
        String json = gson.toJson(routeData.getCheckpoints());
        intent.putExtra(Extra.pathJSON, json);
        intent.putExtra(Extra.ID, routeData.getId());
        startActivity(intent);
    }
}
