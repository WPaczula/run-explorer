package polsl.engineer.runexplorer.layout;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import polsl.engineer.runexplorer.API.data.RouteData;
import polsl.engineer.runexplorer.API.data.RouteTitleData;
import polsl.engineer.runexplorer.API.RESTServiceEndpoints;
import polsl.engineer.runexplorer.API.RetrofitClient;
import polsl.engineer.runexplorer.activities.RoutePreviewActivity;
import polsl.engineer.runexplorer.config.Connection;
import polsl.engineer.runexplorer.config.Extra;
import polsl.engineer.runexplorer.R;
import polsl.engineer.runexplorer.utility.TimeConverter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Wojtek on 26.11.2017.
 */

public class FoundRouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder>{
    public FoundRouteAdapter(Context context, List<RouteTitleData> routeData) {
        this.context = context;
        this.routeData = routeData;
        this.token = Hawk.get(Connection.tokenKey);
        this.username = Hawk.get(Connection.username);
    }
    private RESTServiceEndpoints endpoints = RetrofitClient.getApiService();
    private String token;
    private String username;
    private Context context;
    private List<RouteTitleData> routeData;
    private DateFormat dayFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

    @Override
    public RouteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_card, parent, false);
        return new RouteAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RouteAdapter.ViewHolder holder, final int position) {
        holder.id = routeData.get(position).getId();
        holder.name.setText(routeData.get(position).getName());
        holder.date.setText(dayFormat.format(routeData.get(position).getDate()));
        holder.time.setText(TimeConverter.convertToTimeString(routeData.get(position).getSeconds()));
        holder.distance.setText((String.valueOf(routeData.get(position).getDistance()/1000f) + "km"));
        holder.chooseRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = routeData.get(position).getId();
                Call<RouteData> getRouteCall = endpoints.getRoute(token, id, null);
                getRouteCall.enqueue(new Callback<RouteData>() {
                    @Override
                    public void onResponse(Call<RouteData> call, Response<RouteData> response) {
                        Intent intent = new Intent(context, RoutePreviewActivity.class);
                        Gson gson = new Gson();
                        RouteData data = response.body();
                        data.setId(holder.id);
                        String jsonData = gson.toJson(data);
                        intent.putExtra(Extra.routeJSON, jsonData);
                        intent.putExtra(Extra.isBeforeRun, true);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<RouteData> call, Throwable t) {
                        Toast.makeText(context, "Can't read route data", Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return routeData.size();
    }

    public static class ViewHolder extends  RecyclerView.ViewHolder{
        public TextView name;
        public TextView date;
        public TextView time;
        public TextView distance;
        public Button chooseRoute;
        public String id;

        public ViewHolder(View itemView) {
            super(itemView);
            chooseRoute = (Button) itemView.findViewById(R.id.choose_found_route_btn);
            name = (TextView) itemView.findViewById(R.id.name_found_card_tv);
            date = (TextView) itemView.findViewById(R.id.date_found_card_tv);
            time = (TextView) itemView.findViewById(R.id.time_found_card_tv);
            distance = (TextView) itemView.findViewById(R.id.distance_found_card_tv);
        }
    }
}
