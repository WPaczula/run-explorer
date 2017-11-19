package polsl.engineer.runexplorer.Utility;

import android.annotation.SuppressLint;

/**
 * Created by Wojtek on 19.11.2017.
 */

public class TimeConverter {
    @SuppressLint("DefaultLocale")
    public static String convertToTimeString(int totalSeconds){
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
