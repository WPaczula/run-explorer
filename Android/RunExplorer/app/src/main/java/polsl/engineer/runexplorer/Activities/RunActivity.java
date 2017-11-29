package polsl.engineer.runexplorer.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import polsl.engineer.runexplorer.API.data.RouteData;
import polsl.engineer.runexplorer.config.Extra;
import polsl.engineer.runexplorer.R;
import polsl.engineer.runexplorer.tizen.data.TizenRouteData;
import polsl.engineer.runexplorer.tizen.SAAService.ConsumerService;
import polsl.engineer.runexplorer.tizen.SAAService.DataRecieveListener;

public class RunActivity extends AppCompatActivity implements DataRecieveListener {

    @BindView(R.id.start_btn)
    LinearLayout start;
    @BindView(R.id.stop_btn)
    LinearLayout stop;
    @BindView(R.id.connect_btn)
    LinearLayout connect;
    private String pathJSON;
    private String stopJSON = "{\"type\":\"stop\"}";
    private boolean isBound = false;
    private ConsumerService consumerService = null;
    private String ID;
    private ServiceConnection mConnection = new ServiceConnection() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.run_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickedItemInd = item.getItemId();
        switch (clickedItemInd) {
            case R.id.btn_reconnect:
                if (isBound && consumerService != null) {
                    consumerService.findPeers();
                }
                break;
            //Back icon
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return true;
    }

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

    @Override
    protected  void onDestroy(){
        if(isBound && consumerService != null){
            consumerService.closeConnection();
        }
        if(isBound){
            unbindService(mConnection);
            isBound = false;
        }
        super.onDestroy();
    }

    @OnClick(R.id.connect_btn)
    public void connect(View view){
        if (isBound && consumerService != null) {
            consumerService.findPeers();
            connect.setVisibility(View.GONE);
            start.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.stop_btn)
    public void stop(View view){
        if(isBound && consumerService != null && consumerService.sendData(stopJSON)){
            Toast.makeText(getApplicationContext(), "STOPPED", Toast.LENGTH_SHORT).show();
            stop.setVisibility(View.GONE);
            start.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getApplicationContext(), "No connection", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.start_btn)
    public void start(View view){
        if(isBound && consumerService != null && consumerService.sendData(pathJSON)){
            consumerService.addOnDataRecieveListener(this);
            Toast.makeText(getApplicationContext(), "STARTED", Toast.LENGTH_SHORT).show();
            start.setVisibility(View.GONE);
            stop.setVisibility(View.VISIBLE);
        }else {
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
            consumerService.closeConnection();
            startActivity(intent);
        }
    }
}
