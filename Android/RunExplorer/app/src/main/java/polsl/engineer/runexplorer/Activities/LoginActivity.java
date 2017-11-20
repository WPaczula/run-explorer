package polsl.engineer.runexplorer.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import polsl.engineer.runexplorer.API.Data.JWT;
import polsl.engineer.runexplorer.API.Data.User;
import polsl.engineer.runexplorer.API.RESTServiceEndpoints;
import polsl.engineer.runexplorer.API.RetrofitClient;
import polsl.engineer.runexplorer.Config.API;
import polsl.engineer.runexplorer.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private RESTServiceEndpoints endpoints = RetrofitClient.getApiService();
    @BindView(R.id.username_et)
    public TextView usernameTextView;
    @BindView(R.id.password_et)
    public TextView passwordTextView;

    @OnClick(R.id.register_tv)
    public void OnNewAccount(View view){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.login_btn)
    public void onLogin(View view){
        String username = usernameTextView.getText().toString().trim();
        String password = passwordTextView.getText().toString().trim();
        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Fill username and password", Toast.LENGTH_LONG).show();
        }else {
            User user = new User(username, password);
            Call<JWT> authenticateCall = endpoints.authenticate(user);
            authenticateCall.enqueue(new Callback<JWT>() {
                @Override
                public void onResponse(Call<JWT> call, Response<JWT> response) {
                    Hawk.put(API.tokenKey, response.body().getToken());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<JWT> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Login unsuccessful", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @OnClick(R.id.register_tv)
    public void onRegister(View view){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Hawk.init(this).build();
        if(Hawk.contains(API.tokenKey)){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
