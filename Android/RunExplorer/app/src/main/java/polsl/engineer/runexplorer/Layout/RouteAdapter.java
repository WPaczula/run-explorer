package polsl.engineer.runexplorer.Layout;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import polsl.engineer.runexplorer.API.Data.RouteTitleData;
import polsl.engineer.runexplorer.Activities.RoutePreviewActivity;
import polsl.engineer.runexplorer.R;

/**
 * Created by Wojtek on 11.11.2017.
 */

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder>{
    public RouteAdapter(Context context, List<RouteTitleData> routeData) {
        this.context = context;
        this.routeData = routeData;
    }

    private Context context;
    private List<RouteTitleData> routeData;
    private DateFormat dayFormat = new SimpleDateFormat("dd/MM/yyyy");
    private DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(routeData.get(position).getName());
        holder.date.setText(dayFormat.format(routeData.get(position).getDate()));
        holder.time.setText(timeFormat.format(routeData.get(position).getSeconds()));
        holder.distance.setText((String.valueOf(routeData.get(position).getDistance()/1000) + "km"));
        holder.chooseRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RoutePreviewActivity.class);
                RouteTitleData chosenRoute = routeData.get(position);
                intent.putExtra("id", chosenRoute.getId());
                intent.putExtra("distance", chosenRoute.getDistance());
                intent.putExtra("time", chosenRoute.getSeconds());
                context.startActivity(intent);
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

        public ViewHolder(View itemView) {
            super(itemView);
            chooseRoute = (Button) itemView.findViewById(R.id.choose_route_btn);
            name = (TextView) itemView.findViewById(R.id.name_card_tv);
            date = (TextView) itemView.findViewById(R.id.date_card_tv);
            time = (TextView) itemView.findViewById(R.id.time_card_tv);
            distance = (TextView) itemView.findViewById(R.id.distance_card_tv);
        }
    }
}
