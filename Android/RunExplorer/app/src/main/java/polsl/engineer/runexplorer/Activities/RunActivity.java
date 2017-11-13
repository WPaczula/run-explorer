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

import polsl.engineer.runexplorer.R;
import polsl.engineer.runexplorer.SAAService.ConsumerService;

public class RunActivity extends AppCompatActivity {

    private String JSON = "{\"type\":\"start\"," +
            "\"route\": [{\"lat\": 50.249930, \"lng\": 18.565919}," +
            "{\"lat\": 50.250253, \"lng\": 18.566552}," +
            "{\"lat\": 50.250415, \"lng\": 18.566706}," +
            "{\"lat\": 50.250747, \"lng\": 18.566952}," +
            "{\"lat\": 50.250722, \"lng\": 18.567749}," +
            "{\"lat\": 50.250677, \"lng\": 18.568315}]}";
    private String stopJSON = "{\"type\":\"stop\"}";
    private boolean isBound = false;
    private ConsumerService consumerService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        Object[] route =  getIntent().getParcelableArrayExtra("route");
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

    public void stop(View view){
        if(isBound && consumerService.sendData(stopJSON)){
            Toast.makeText(getApplicationContext(), "STOPPED", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "No connection", Toast.LENGTH_SHORT).show();
        }
    }

    private String mess;
    public static void getInfo(String message){
        mess
    }

    public void start(View view){
        if(isBound && consumerService.sendData(JSON)){
            Toast.makeText(getApplicationContext(), "STARTED", Toast.LENGTH_SHORT).show();
        }else
        {
            Toast.makeText(getApplicationContext(), "No connection", Toast.LENGTH_SHORT).show();
        }
    }
}
