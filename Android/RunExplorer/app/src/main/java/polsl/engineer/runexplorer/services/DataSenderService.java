package polsl.engineer.runexplorer.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;

import org.greenrobot.greendao.database.Database;

import java.util.List;

import polsl.engineer.runexplorer.API.RESTServiceEndpoints;
import polsl.engineer.runexplorer.API.RetrofitClient;
import polsl.engineer.runexplorer.API.data.Message;
import polsl.engineer.runexplorer.API.data.NewRouteData;
import polsl.engineer.runexplorer.API.data.NewRunData;
import polsl.engineer.runexplorer.API.data.RouteData;
import polsl.engineer.runexplorer.R;
import polsl.engineer.runexplorer.config.Connection;
import polsl.engineer.runexplorer.config.DB;
import polsl.engineer.runexplorer.entity.DaoMaster;
import polsl.engineer.runexplorer.entity.DaoSession;
import polsl.engineer.runexplorer.entity.StoredRoute;
import polsl.engineer.runexplorer.entity.StoredRouteDao;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DataSenderService extends IntentService {

    public DataSenderService() {
        super("DataSenderService");
    }
    private RESTServiceEndpoints endpoints = RetrofitClient.getApiService();
    private String token;
    private String username;
    private StoredRouteDao routeDao;

    @Override
    public void onCreate(){
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DB.name);
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();
        routeDao = daoSession.getStoredRouteDao();
        Hawk.init(this).build();
        token = Hawk.get(Connection.tokenKey);
        username = Hawk.get(Connection.username);
        Notification notification = new NotificationCompat.Builder(this)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("Your routes are trying to be sent")
                .build();
        startForeground(100,
                notification);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            List<StoredRoute> routes = routeDao.loadAll();
            while(routes.size() > 0){
                for(StoredRoute route : routes){
                    sendRoute(route, token, username, routeDao);
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                routes = routeDao.loadAll();
            }
            Toast.makeText(this, "Your routes have been sent", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRoute(final StoredRoute storedRoute, String token, String username, final StoredRouteDao dao){
        RouteData routeData = new RouteData(storedRoute);
        if(routeData.isNew()){
            Call<Message> addRouteCallback = endpoints.saveRoute(token, new NewRouteData(routeData));
            addRouteCallback.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    if(response.isSuccessful()){
                        if(response.body().isSuccess()){
                            dao.delete(storedRoute);
                        }
                    }
                }
                @Override
                public void onFailure(Call<Message> call, Throwable t) {}
            });
        }else{
            Call<Message> addRunCallback = endpoints.saveRun(token, username, new NewRunData(routeData));
            addRunCallback.enqueue(new Callback<Message>() {
                @Override
                public void onResponse(Call<Message> call, Response<Message> response) {
                    if(response.isSuccessful()) {
                        if (response.body().isSuccess()) {
                            dao.delete(storedRoute);
                        }
                    }
                }
                @Override
                public void onFailure(Call<Message> call, Throwable t) {}
            });
        }
    }
}
