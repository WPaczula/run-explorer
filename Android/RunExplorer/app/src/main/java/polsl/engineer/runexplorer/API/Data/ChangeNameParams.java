package polsl.engineer.runexplorer.API.data;

/**
 * Created by Wojtek on 26.11.2017.
 */

public class ChangeNameParams {
    private long date;
    private String newName;

    public ChangeNameParams(long date, String newName) {
        this.date = date;
        this.newName = newName;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
