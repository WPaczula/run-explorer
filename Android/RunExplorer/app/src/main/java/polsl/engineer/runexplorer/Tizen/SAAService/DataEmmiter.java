package polsl.engineer.runexplorer.Tizen.SAAService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wojtek on 19.11.2017.
 */

public class DataEmmiter {
    private List<DataRecieveListener> listeners = new ArrayList<>();

    public void addListener(DataRecieveListener listener){
        listeners.add(listener);
    }

    public void EmitData(String data){
        for(DataRecieveListener listener : listeners){
            listener.OnRecieve(data);
        }
    }
}
