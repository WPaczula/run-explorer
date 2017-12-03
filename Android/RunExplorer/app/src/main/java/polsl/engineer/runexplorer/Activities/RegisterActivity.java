package polsl.engineer.runexplorer.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import polsl.engineer.runexplorer.API.data.Message;
import polsl.engineer.runexplorer.API.data.User;
import polsl.engineer.runexplorer.API.RESTServiceEndpoints;
import polsl.engineer.runexplorer.API.RetrofitClient;
import polsl.engineer.runexplorer.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.rpassword_et)
    public EditText passwordEditText;
    @BindView(R.id.confirm_password_et)
    public EditText confirmPasswordEditText;
    @BindView(R.id.rlogin_et)
    public EditText loginEditText;
    private RESTServiceEndpoints endpoints = RetrofitClient.getApiService();

    @OnClick(R.id.register_btn)
    public void onRegister(View view){
        String password = passwordEditText.getText().toString();
        String passwordConfirmation = confirmPasswordEditText.getText().toString();
        if(password.equals(passwordConfirmation)){
            if(passwordValid(password)){
                String username = loginEditText.getText().toString();
                User user = new User(username, password);
                Call<Message> signUpCall = endpoints.signUp(user);
                signUpCall.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(Call<Message> call, Response<Message> response) {
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<Message> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Cant register", Toast.LENGTH_LONG).show();
                    }
                });
            }else {
                Toast.makeText(this, "Password is too shord (min 6 characters)", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Passwords differ", Toast.LENGTH_LONG).show();
        }
    }

    private boolean passwordValid(String password){
        return password.length() >= 6;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }


}
