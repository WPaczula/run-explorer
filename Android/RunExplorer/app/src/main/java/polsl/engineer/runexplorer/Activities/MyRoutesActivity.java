package polsl.engineer.runexplorer.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import polsl.engineer.runexplorer.Data.RouteTitleInfo;
import polsl.engineer.runexplorer.R;
import polsl.engineer.runexplorer.Utility.MyRouteAdapter;

public class MyRoutesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MyRouteAdapter adapter;
    private List<RouteTitleInfo> routeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_routes);

        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        routeList = Arrays.asList(new RouteTitleInfo("1", "test1", new Date(2017, 11, 7), new Timestamp(0,0,0,1,0,0,0), 10000),
                new RouteTitleInfo("2", "test1", new Date(2017, 11, 8), new Timestamp(0,0,0,1,20,0,0), 12000),
                new RouteTitleInfo("3", "test1", new Date(2017, 11, 9), new Timestamp(0,0,0,1,0,0,0), 10000),
                new RouteTitleInfo("4", "test1", new Date(2017, 11, 10), new Timestamp(0,0,0,1,10,0,0), 11000));

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new MyRouteAdapter(this, routeList);
        recyclerView.setAdapter(adapter);
    }
}
