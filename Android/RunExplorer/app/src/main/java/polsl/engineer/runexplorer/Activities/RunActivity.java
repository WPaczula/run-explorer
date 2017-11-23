package polsl.engineer.runexplorer.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.sql.Date;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.OnClick;
import polsl.engineer.runexplorer.API.Data.Checkpoint;
import polsl.engineer.runexplorer.API.Data.RouteData;
import polsl.engineer.runexplorer.Config.Connection;
import polsl.engineer.runexplorer.Config.Extra;
import polsl.engineer.runexplorer.R;
import polsl.engineer.runexplorer.Tizen.Data.TizenRouteData;
import polsl.engineer.runexplorer.Tizen.SAAService.ConsumerService;
import polsl.engineer.runexplorer.Tizen.SAAService.DataRecieveListener;

public class RunActivity extends AppCompatActivity implements DataRecieveListener {

    private String pathJSON;
    private String stopJSON = "{\"type\":\"stop\"}";
    private boolean isBound = false;
    private ConsumerService consumerService = null;
    private String ID;
    private TizenRouteData routeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        ID = intent.getStringExtra(Extra.ID);
        pathJSON = intent.getStringExtra(Extra.pathJSON);
        isBound = bindService(new Intent(RunActivity.this, ConsumerService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            consumerService = ((ConsumerService.LocalBinder) service).getService();
            Toast.makeText(getApplicationContext(), "Service connected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            consumerService = null;
            isBound = false;
            Toast.makeText(getApplicationContext(), "Service disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    @OnClick(R.id.stop_btn)
    public void stop(View view){
        if(isBound && consumerService.sendData(stopJSON)){
            Toast.makeText(getApplicationContext(), "STOPPED", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "No connection", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.start_btn)
    public void start(View view){
        if(isBound && consumerService.sendData(pathJSON)){
            consumerService.addOnDataRecieveListener(this);
            Toast.makeText(getApplicationContext(), "STARTED", Toast.LENGTH_SHORT).show();
        }else
        {
            Toast.makeText(getApplicationContext(), "No connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnRecieve(String data) {
        if(!data.equals("Route set")){
            Gson gson = new Gson();
            TizenRouteData tizenRouteData = gson.fromJson(data, TizenRouteData.class);
            RouteData routeData = new RouteData(tizenRouteData, Calendar.getInstance().getTime().getTime());
            routeData.setId(ID);
            Intent intent = new Intent(RunActivity.this, RoutePreviewActivity.class);
            intent.putExtra(Extra.routeJSON, gson.toJson(routeData));
            intent.putExtra(Extra.isBeforeRun, false);
            startActivity(intent);
        }
    }
}
