package polsl.engineer.runexplorer.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import polsl.engineer.runexplorer.Config.Extra;
import polsl.engineer.runexplorer.R;

public class RouteSearchActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, SeekBar.OnSeekBarChangeListener {

    private GoogleMap mMap;
    @BindView(R.id.radius_sb)
    public SeekBar radiusBar;
    @BindView(R.id.radius_value_tv)
    public TextView radiusValue;
    @BindView(R.id.distance_min_value_et)
    public EditText minDistanceValue;
    @BindView(R.id.distance_max_value_et)
    public EditText maxDistanceValue;
    @BindView(R.id.username_et)
    public EditText usernameValue;
    private Marker centerMarker;
    private Circle searchCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_search);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        radiusValue.setText("0km");
        radiusBar.setOnSeekBarChangeListener(this);
        radiusBar.setMax(100);
        radiusBar.setProgress(5);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        if(!permissionsGranted())
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        else{
            showCurrentPosition();
        }
    }

    private boolean permissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressWarnings("MissingPermission")
    private void showCurrentPosition(){
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCurrentPosition();
            } else {
                Toast.makeText(this, "You didn't give permission to access device location", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(centerMarker != null)
            centerMarker.remove();
        if(searchCircle != null)
            searchCircle.remove();
        centerMarker = mMap.addMarker(new MarkerOptions().position(latLng));
        searchCircle = mMap.addCircle(new CircleOptions().center(latLng).radius((double)radiusBar.getProgress()*100));
        radiusValue.setText(radiusBar.getProgress()*0.1f + "km");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(centerMarker != null){
            searchCircle.setRadius(progress*100);
            radiusValue.setText(progress/10f + "km");
        }
    }

    @OnClick(R.id.route_search_btn)
    public void search(View view){
        Intent intent = new Intent(RouteSearchActivity.this, SearchResultActivity.class);
        Integer maxDistance = null;
        Integer minDistance = null;
        Integer radius = null;
        Double lat = null;
        Double lng = null;
        String username = null;
        if(!maxDistanceValue.getText().toString().isEmpty())
            maxDistance = Integer.valueOf(maxDistanceValue.getText().toString());
        if(!minDistanceValue.getText().toString().isEmpty())
            minDistance = Integer.valueOf(minDistanceValue.getText().toString());
        if(centerMarker != null){
            lat = centerMarker.getPosition().latitude;
            lng = centerMarker.getPosition().longitude;
            radius = radiusBar.getProgress();
        }
        if(!usernameValue.getText().toString().isEmpty())
            username = usernameValue.getText().toString();
        intent.putExtra(Extra.maxDistance, maxDistance);
        intent.putExtra(Extra.minDistance, minDistance);
        intent.putExtra(Extra.radius, radius);
        intent.putExtra(Extra.lat, lat);
        intent.putExtra(Extra.lng, lng);
        intent.putExtra(Extra.username, username);
        //startActivity(intent);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
