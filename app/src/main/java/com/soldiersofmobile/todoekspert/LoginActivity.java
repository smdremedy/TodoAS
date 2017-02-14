package com.soldiersofmobile.todoekspert;

import android.content.Intent;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity implements LoginCallback {

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

    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loginManager = ((App) getApplication()).getLoginManager();

        if (BuildConfig.DEBUG) {
            usernameEditText.setText("test");
            passwordEditText.setText("test");
        }
    }

    @OnClick(R.id.sign_in_button)
    public void onClick() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        loginManager.login(username, password);
    }


    @Override
    protected void onStart() {
        super.onStart();
        loginManager.setLoginCallback(this);
    }

    @Override
    protected void onStop() {
        loginManager.setLoginCallback(null);
        super.onStop();
    }

    @Override
    public void showProgress(boolean enabled) {
        signInButton.setEnabled(!enabled);
        progress.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    @Override
    public void loginSuccess() {
        finish();
        Intent intent = new Intent(this, TodoListActivity.class);
        startActivity(intent);
    }

    @Override
    public void handleError(String error) {
        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUsernameEmptyError() {
        usernameEditText.setError(getString(R.string.empty_field_error));
    }

    @Override
    public void showPasswordLengthError(int length, int minPasswordLength) {
        passwordEditText.setError(getString(R.string.password_lenght_error,
                minPasswordLength, length));
    }

}
