package polsl.engineer.runexplorer.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import polsl.engineer.runexplorer.R;
import polsl.engineer.runexplorer.Utility.TimeAdapter;
import polsl.engineer.runexplorer.Utility.TimeConverter;

public class RoutePreviewActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    @BindView(R.id.distance_value_tv)
    public TextView distanceValue;
    @BindView(R.id.time_value_tv)
    public TextView timeValue;

    private LatLng[] routePoints = {
            new LatLng(50.249930, 18.565919),
            new LatLng(50.250253, 18.566552),
            new LatLng(50.250415, 18.566706),
            new LatLng(50.250747, 18.566952),
            new LatLng(50.250722, 18.567749),
            new LatLng(50.250677, 18.568315),
    };
    private ArrayList<Integer> times = new ArrayList<Integer>(){{add(20); add(20); add(20); add(20); add(20);}};
    private int distance = 1000;
    private int time = 21002102;

    @SuppressLint("SetTextI18n")
    private void initUI(){
        distanceValue.setText(String.valueOf(distance/1000) + "km");
        timeValue.setText(TimeConverter.convertToTimeString(time));
        RecyclerView timesRecyclerView = (RecyclerView) findViewById(R.id.times_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        timesRecyclerView.setLayoutManager(linearLayoutManager);

        TimeAdapter adapter = new TimeAdapter(this, times);
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
        initUI();

        Intent intent = getIntent();
        //TODO: shoot to the server with given ID
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.addPolyline(drawPolyline(routePoints));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(LatLng point : routePoints){
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

    private PolylineOptions drawPolyline(LatLng[] routePoints){
        PolylineOptions polylineOptions = new PolylineOptions();
        for(LatLng point : routePoints){
            polylineOptions.add(point);
        }
        polylineOptions.width(20.0f);
        return polylineOptions;
    }

    @OnClick(R.id.challenge_route_btn)
    public void startRoute(View view){
        Intent intent = new Intent(RoutePreviewActivity.this, RunActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArray("route", routePoints);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
