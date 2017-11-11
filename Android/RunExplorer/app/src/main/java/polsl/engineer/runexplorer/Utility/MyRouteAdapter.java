package polsl.engineer.runexplorer.Utility;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import polsl.engineer.runexplorer.Activities.RoutePreviewActivity;
import polsl.engineer.runexplorer.Activities.RunActivity;
import polsl.engineer.runexplorer.Data.RouteTitleInfo;
import polsl.engineer.runexplorer.R;

/**
 * Created by Wojtek on 11.11.2017.
 */

public class MyRouteAdapter extends RecyclerView.Adapter<MyRouteAdapter.ViewHolder>{
    public MyRouteAdapter(Context context, List<RouteTitleInfo> routeData) {
        this.context = context;
        this.routeData = routeData;
    }

    private Context context;
    private List<RouteTitleInfo> routeData;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(routeData.get(position).getName());
        holder.date.setText(routeData.get(position).getDate().toString());
        holder.time.setText(routeData.get(position).getTime().toString());
        holder.distance.setText(String.valueOf(routeData.get(position).getDistance()));
        holder.chooseRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RoutePreviewActivity.class);
                intent.putExtra("id", routeData.get(position).getId());
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
