package polsl.engineer.runexplorer.API.Data;

/**
 * Created by Wojtek on 20.11.2017.
 */

public class Message {
    private boolean success;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
