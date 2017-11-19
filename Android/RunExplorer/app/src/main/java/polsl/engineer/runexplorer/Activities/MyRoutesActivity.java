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
import polsl.engineer.runexplorer.Utility.RouteAdapter;

public class MyRoutesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RouteAdapter adapter;
    private List<RouteTitleInfo> routeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_routes);

        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        routeList = Arrays.asList(new RouteTitleInfo("1", "test1", new Date(1510339274), new Timestamp(946690874), 10000),
                new RouteTitleInfo("2", "test2", new Date(1510425674), new Timestamp(946690214), 12000),
                new RouteTitleInfo("3", "test3", new Date(1507747274), new Timestamp(946689614), 10000),
                new RouteTitleInfo("4", "test4", new Date(1507833674), new Timestamp(946689134), 11000));

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RouteAdapter(this, routeList);
        recyclerView.setAdapter(adapter);
    }
}
