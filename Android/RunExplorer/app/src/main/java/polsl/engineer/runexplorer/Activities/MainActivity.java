package polsl.engineer.runexplorer.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import polsl.engineer.runexplorer.API.Data.Checkpoint;
import polsl.engineer.runexplorer.API.Data.SetPathAction;
import polsl.engineer.runexplorer.Config.Extra;
import polsl.engineer.runexplorer.R;

public class MainActivity extends AppCompatActivity {

    public void routeLists(View view){
        Intent intent = new Intent(MainActivity.this, MyRoutesActivity.class);
        startActivity(intent);
    }

    public void searchRoute(View view){
        Intent intent = new Intent(MainActivity.this, RouteSearchActivity.class);
        startActivity(intent);
    }

    public void logout(View view){
        Hawk.deleteAll();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void newRoute(View view){
        Intent intent = new Intent(MainActivity.this, RunActivity.class);
        intent.putExtra(Extra.ID, "");
        Gson gson = new Gson();
        String json = gson.toJson(new SetPathAction("start", new ArrayList<Checkpoint>()));
        intent.putExtra(Extra.pathJSON, json);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Hawk.init(this).build();
    }
}
