package polsl.engineer.runexplorer.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import polsl.engineer.runexplorer.R;

public class MainActivity extends AppCompatActivity {

    @OnClick(R.id.route_preview_button)
    public void previewRoute(View view){
        Intent intent = new Intent(MainActivity.this, MyRoutesActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
