package com.soldiersofmobile.todoekspert;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    public static final int PASSWORD_LENGTH = 4;
    @BindView(R.id.username_edit_text)
    EditText usernameEditText;
    @BindView(R.id.password_edit_text)
    EditText passwordEditText;
    @BindView(R.id.sign_in_button)
    Button signInButton;
    @BindView(R.id.sign_up_button)
    Button signUpButton;
    @BindView(R.id.activity_login)
    LinearLayout activityLogin;
    @BindView(R.id.progress)
    ProgressBar progress;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                signInButton.setEnabled(true);
                progress.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.sign_in_button)
    public void onClick() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean hasErrors = false;
        if (username.isEmpty()) {
            usernameEditText.setError(getString(R.string.empty_field_error));
            hasErrors = true;
        }
        int length = password.length();
        if (length < PASSWORD_LENGTH) {
            passwordEditText.setError(getString(R.string.password_lenght_error,
                    PASSWORD_LENGTH, length));
            hasErrors = true;
        }

        if (!hasErrors) {
            login(username, password);
        }


    }

    private void login(String username, String password) {


//        signInButton.setEnabled(false);
//        progress.setVisibility(View.VISIBLE);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//                    Thread.sleep(10000);
//                    handler.sendEmptyMessage(1);
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//
//                }
//            }
//        }).start();

        AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                signInButton.setEnabled(false);
                progress.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                progress.setProgress(values[0]);
            }


            @Override
            protected String doInBackground(String... params) {
                try {
                    for (int i = 0; i < 100; i++) {
                        Thread.sleep(50);
                        publishProgress(i);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return validateCredentials(params);
            }

            @Nullable
            private String validateCredentials(String[] params) {
                return params[0].equals("test") && params[1].equals("test") ? null :
                        "Invalid password";
            }

            @Override
            protected void onPostExecute(String error) {
                super.onPostExecute(error);
                signInButton.setEnabled(true);
                progress.setVisibility(View.GONE);
                if (error == null) {
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        };

        asyncTask.execute(username, password);


    }
}
