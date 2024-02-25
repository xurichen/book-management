package com.xuri.bookmanagement;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        executorService = Executors.newSingleThreadExecutor();

        loginButton.setOnClickListener(v -> {
            final String username = usernameEditText.getText().toString();
            final String password = passwordEditText.getText().toString();
            loginUser(username, password);
        });
    }

    private void loginUser(String username, String password) {
        executorService.execute(() -> {
            boolean success = login(username, password);
            runOnUiThread(() -> {
                if (success) {
                    // Navigate to the next screen
                    Intent intent = new Intent(MainActivity.this, BookManagementActivity.class);
                    intent.putExtra("USERNAME", username);
                    intent.putExtra("PASSWORD", password);
                    startActivity(intent);
                    Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


    public boolean login(String username, String password) {
        String urlString = "http://192.168.10.100:8080/"; // URL to call for the login
        String csrfToken = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            String basicAuth = "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", basicAuth);

            int responseCode = connection.getResponseCode();
            return responseCode == 200; // HTTP OK status code
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
