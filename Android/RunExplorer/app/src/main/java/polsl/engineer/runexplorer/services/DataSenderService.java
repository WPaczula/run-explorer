package polsl.engineer.runexplorer.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.view.View;
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
import polsl.engineer.runexplorer.config.Connection;
import polsl.engineer.runexplorer.config.DB;
import polsl.engineer.runexplorer.entity.CheckpointConverter;
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

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Toast.makeText(this, "Trying to send data later", Toast.LENGTH_SHORT).show();
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DB.name);
            Database db = helper.getWritableDb();
            DaoSession daoSession = new DaoMaster(db).newSession();
            StoredRouteDao routeDao = daoSession.getStoredRouteDao();
            String token = Hawk.get(Connection.tokenKey);
            String usename = Hawk.get(Connection.username);
            List<StoredRoute> routes = routeDao.loadAll();
            while(routes.size() > 0){
                for(StoredRoute route : routes){
                    sendRoute(route, token, usename, routeDao);
                }
                routes = routeDao.loadAll();
                try {
                    Thread.sleep(1000 * 60 * 5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
                        if(!response.body().isSuccess()){
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
                        if (!response.body().isSuccess()) {
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
