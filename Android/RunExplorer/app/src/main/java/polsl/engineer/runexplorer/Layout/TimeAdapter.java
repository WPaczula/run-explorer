package polsl.engineer.runexplorer.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import polsl.engineer.runexplorer.R;
import polsl.engineer.runexplorer.utility.TimeConverter;

/**
 * Created by Wojtek on 19.11.2017.
 */

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.ViewHolder>{

    private Context context;
    private List<Integer> timesData;

    public TimeAdapter(Context context, List<Integer> timesData) {
        this.context = context;
        this.timesData = timesData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_card, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.number.setText(String.valueOf(position+1));
        int totalSecs = timesData.get(position);
        holder.timeValue.setText(TimeConverter.convertToTimeString(totalSecs));
    }

    @Override
    public int getItemCount() {
        return timesData.size();
    }

    static class ViewHolder extends  RecyclerView.ViewHolder{
        TextView number;
        TextView timeValue;

        ViewHolder(View itemView) {
            super(itemView);
            number = (TextView) itemView.findViewById(R.id.number_tv);
            timeValue = (TextView) itemView.findViewById(R.id.time_value_tv);
        }
    }
}
