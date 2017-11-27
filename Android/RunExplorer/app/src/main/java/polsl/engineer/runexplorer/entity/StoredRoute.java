package polsl.engineer.runexplorer.entity;

import com.google.gson.Gson;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

import polsl.engineer.runexplorer.API.data.Checkpoint;
import polsl.engineer.runexplorer.API.data.RouteData;

import org.greenrobot.greendao.DaoException;

/**
 * Created by Wojtek on 27.11.2017.
 */

@Entity
public class StoredRoute {
    @Generated
    @Id(autoincrement = true)
    private Long id;
    private boolean isNew;
    private String routeId;
    private String name;
    private int distance;
    private long date;
    private int time;
    private String times;
    @ToMany(joinProperties = { @JoinProperty(name = "id", referencedName = "routeId")})
    private List<StoredCheckpoint> checkpoints;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 38357284)
    private transient StoredRouteDao myDao;

    public StoredRoute(RouteData routeData){
        this.isNew = routeData.isNew();
        this.routeId = routeData.getId();
        this.name = routeData.getName();
        this.distance = routeData.getDistance();
        this.date = routeData.getDate();
        this.time = routeData.getTime();
        this.times = new Gson().toJson(routeData.getTimes());
    }

    @Generated(hash = 544931785)
    public StoredRoute(Long id, boolean isNew, String routeId, String name, int distance, long date,
            int time, String times) {
        this.id = id;
        this.isNew = isNew;
        this.routeId = routeId;
        this.name = name;
        this.distance = distance;
        this.date = date;
        this.time = time;
        this.times = times;
    }

    @Generated(hash = 256042026)
    public StoredRoute() {
    }

    public Long getId() {
        return this.id;
    }
    public boolean getIsNew() {
        return this.isNew;
    }
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }
    public String getRouteId() {
        return this.routeId;
    }
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getDistance() {
        return this.distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public long getDate() {
        return this.date;
    }
    public void setDate(long date) {
        this.date = date;
    }
    public int getTime() {
        return this.time;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public String getTimes() {
        return this.times;
    }
    public void setTimes(String times) {
        this.times = times;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 207282048)
    public List<StoredCheckpoint> getCheckpoints() {
        if (checkpoints == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StoredCheckpointDao targetDao = daoSession.getStoredCheckpointDao();
            List<StoredCheckpoint> checkpointsNew = targetDao
                    ._queryStoredRoute_Checkpoints(id);
            synchronized (this) {
                if (checkpoints == null) {
                    checkpoints = checkpointsNew;
                }
            }
        }
        return checkpoints;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 725706642)
    public synchronized void resetCheckpoints() {
        checkpoints = null;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    public void setId(Long id) {
        this.id = id;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2024402413)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getStoredRouteDao() : null;
    }
}
