package brymian.bubbles.damian.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import brymian.bubbles.R;
import brymian.bubbles.bryant.MapsActivity;
import brymian.bubbles.damian.nonactivity.GetUserCallback;
import brymian.bubbles.damian.nonactivity.ServerRequests;
import brymian.bubbles.damian.nonactivity.User;
import brymian.bubbles.damian.nonactivity.UserStoreLocal;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button bLogin;
    EditText etUsername, etPassword;
    //TextView tvRegisterLink;
    ImageButton ibRegisterLink;
    UserStoreLocal userStoreLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bLogin = (Button) findViewById(R.id.bLogin);
        //tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);
        ibRegisterLink = (ImageButton) findViewById(R.id.ibRegisterLink);

        bLogin.setOnClickListener(this);
        //tvRegisterLink.setOnClickListener(this);
        ibRegisterLink.setOnClickListener(this);

        userStoreLocal = new UserStoreLocal(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bLogin) {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            User user = new User(username, password);

            authenticate(user);
        }
        //if (v.getId() == R.id.tvRegisterLink) {
        if (v.getId() == R.id.ibRegisterLink) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    private void authenticate(User user) {
        ServerRequests serverRequest = new ServerRequests(this);
        serverRequest.getUserDataAsyncTask(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null) {
                    showErrorMessage();
                } else {
                    logUserIn(returnedUser);
                }
            }
        });
    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setMessage(("Incorrect user details."));
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private void logUserIn(User returnedUser) {
        userStoreLocal.setUserData(returnedUser);
        userStoreLocal.setUserLoggedStatus(true);

        startActivity(new Intent(this, MapsActivity.class));
    }

    //temp
}
