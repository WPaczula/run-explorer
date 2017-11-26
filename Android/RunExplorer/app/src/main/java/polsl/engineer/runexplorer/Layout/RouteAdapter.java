package polsl.engineer.runexplorer.Layout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import polsl.engineer.runexplorer.API.Data.Message;
import polsl.engineer.runexplorer.API.Data.RouteData;
import polsl.engineer.runexplorer.API.Data.RouteTitleData;
import polsl.engineer.runexplorer.API.RESTServiceEndpoints;
import polsl.engineer.runexplorer.API.RetrofitClient;
import polsl.engineer.runexplorer.Activities.RoutePreviewActivity;
import polsl.engineer.runexplorer.Config.Connection;
import polsl.engineer.runexplorer.Config.Extra;
import polsl.engineer.runexplorer.R;
import polsl.engineer.runexplorer.Utility.TimeConverter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Wojtek on 11.11.2017.
 */

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder>{
    public RouteAdapter(Context context, List<RouteTitleData> routeData) {
        this.context = context;
        this.routeData = routeData;
        this.token = Hawk.get(Connection.tokenKey);
        this.username = Hawk.get(Connection.username);
        adapterContext = this;
    }
    private RESTServiceEndpoints endpoints = RetrofitClient.getApiService();
    private String token;
    private String username;
    private Context context;
    private RouteAdapter adapterContext;
    private List<RouteTitleData> routeData;
    private DateFormat dayFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.id = routeData.get(position).getId();
        holder.name.setText(routeData.get(position).getName());
        holder.date.setText(dayFormat.format(routeData.get(position).getDate()));
        holder.time.setText(TimeConverter.convertToTimeString(routeData.get(position).getSeconds()));
        holder.distance.setText((String.valueOf(routeData.get(position).getDistance()/1000f) + "km"));
        holder.chooseRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = routeData.get(position).getId();
                Call<RouteData> getRouteCall = endpoints.getRoute(token, id, routeData.get(position).getDate());
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
        holder.deleteRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Message> deleteRunCall = endpoints.deleteRun(token, username, routeData.get(position).getDate());
                deleteRunCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            routeData.remove(position);
                            adapterContext.notifyDataSetChanged();
                        }
                        else {
                            Toast.makeText(context, "Could not delete the run", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Toast.makeText(context, "Could not delete the run", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        holder.renameRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Change name");

                final EditText input = new EditText(context);
                input.requestFocus();
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String m_Text = input.getText().toString();
                        Call<Message> renameCallback = endpoints.renameRun(token, username, routeData.get(position).getDate(), m_Text);
                        renameCallback.enqueue(new Callback<Message>() {
                            @Override
                            public void onResponse(Call<Message> call, Response<Message> response) {
                                if(response.isSuccessful()){
                                    routeData.get(position).setName(m_Text);
                                    notifyItemRangeChanged(position, routeData.size());
                                }
                            }

                            @Override
                            public void onFailure(Call<Message> call, Throwable t) {
                                Toast.makeText(context, "Could not rename run", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
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
        public Button deleteRoute;
        public Button renameRun;
        public String id;

        public ViewHolder(View itemView) {
            super(itemView);
            chooseRoute = (Button) itemView.findViewById(R.id.choose_route_card_btn);
            deleteRoute = (Button) itemView.findViewById(R.id.delete_route_card_bnt);
            renameRun = (Button) itemView.findViewById(R.id.rename_route_card_bnt);
            name = (TextView) itemView.findViewById(R.id.name_route_card_tv);
            date = (TextView) itemView.findViewById(R.id.date_route_card_tv);
            time = (TextView) itemView.findViewById(R.id.time_route_card_tv);
            distance = (TextView) itemView.findViewById(R.id.distance_route_card_tv);
        }
    }
}
