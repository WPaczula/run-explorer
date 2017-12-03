package polsl.engineer.runexplorer.utility;

import java.util.ArrayList;
import java.util.List;

import polsl.engineer.runexplorer.entity.StoredTime;
import polsl.engineer.runexplorer.entity.StoredTimeDao;

/**
 * Created by Wojtek on 03.12.2017.
 */

public class TimesConverter {
    public static List<Integer> getTimes(List<StoredTime> times){
        List<Integer> output = new ArrayList<>();
        for(StoredTime time: times){
            output.add(time.getTime());
        }
        return output;
    }

    public static void insertToDB(StoredTimeDao dao, List<Integer> times, long id){
        for(Integer time : times){
            dao.insert(new StoredTime(time, id));
        }
    }
}
