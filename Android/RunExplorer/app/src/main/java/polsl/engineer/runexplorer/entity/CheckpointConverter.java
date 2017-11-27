package polsl.engineer.runexplorer.entity;

import java.util.ArrayList;
import java.util.List;

import polsl.engineer.runexplorer.API.data.Checkpoint;

/**
 * Created by Wojtek on 27.11.2017.
 */

public class CheckpointConverter {
    public static List<Checkpoint> getCheckpoints(List<StoredCheckpoint> checkpoints){
        List<Checkpoint> output = new ArrayList<>();
        for(StoredCheckpoint checkpoint: checkpoints){
            output.add(new Checkpoint(checkpoint));
        }
        return output;
    }

    public static void insertToDB(StoredCheckpointDao dao, List<Checkpoint> checkpoints, long id){
        for(Checkpoint checkpoint : checkpoints){
            dao.insert(new StoredCheckpoint(checkpoint, id));
        }
    }
}
