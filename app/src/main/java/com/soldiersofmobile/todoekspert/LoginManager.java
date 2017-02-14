package com.soldiersofmobile.todoekspert;

import android.content.SharedPreferences;
import android.util.Log;

import com.soldiersofmobile.todoekspert.api.TodoApi;
import com.soldiersofmobile.todoekspert.api.User;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class LoginManager implements Callback<User> {

    private static final int MIN_PASSWORD_LENGTH = 4;

    private static final String TOKEN = "token";
    private static final String USER_ID = "user_id";

    private final TodoApi todoApi;
    private Converter<ResponseBody, ErrorResponse> errorConverter;
    private SharedPreferences preferences;

    private LoginCallback loginCallback = EMPTY;
    private Call<User> loginCall;

    private String userId;
    private String token;

    public LoginManager(TodoApi todoApi, Converter<ResponseBody, ErrorResponse> errorConverter, SharedPreferences preferences) {
        this.todoApi = todoApi;
        this.errorConverter = errorConverter;
        this.preferences = preferences;

        loadUser();
    }

    private void loadUser() {
        token = preferences.getString(TOKEN, "");
        userId = preferences.getString(USER_ID, "");
    }

    public boolean hasToLogin() {
        return token == null || token.isEmpty()
                || userId == null || userId.isEmpty();
    }

    public void logout() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(TOKEN);
        editor.remove(USER_ID);
        editor.apply();
        userId = null;
        token = null;
    }

    public void login(String username, String password) {
        boolean hasErrors = false;
        if (username.isEmpty()) {
            loginCallback.showUsernameEmptyError();

            hasErrors = true;
        }
        int length = password.length();
        if (length < MIN_PASSWORD_LENGTH) {
            loginCallback.showPasswordLengthError(length, MIN_PASSWORD_LENGTH);

            hasErrors = true;
        }

        if (!hasErrors && loginCall == null) {
            loginCall = todoApi.getLogin(username, password);
            loginCallback.showProgress(true);
            loginCall.enqueue(this);
        }

    }

    public void setLoginCallback(LoginCallback loginCallback) {
        this.loginCallback = loginCallback;
    }

    @Override
    public void onResponse(Call<User> call, Response<User> response) {
        loginCall = null;
        loginCallback.showProgress(false);
        if (response.isSuccessful()) {
            User user = response.body();
            saveUser(user);
            loginCallback.loginSuccess();
        } else {
            ResponseBody responseBody = response.errorBody();
            try {
                ErrorResponse errorResponse = errorConverter.convert(responseBody);
                loginCallback.handleError(errorResponse.getError());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onFailure(Call<User> call, Throwable t) {
        loginCall = null;
        loginCallback.showProgress(false);
        loginCallback.handleError(t.getMessage());
    }

    private void saveUser(User user) {
        userId = user.getObjectId();
        token = user.getSessionToken();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TOKEN, user.getSessionToken());
        editor.putString(USER_ID, user.getObjectId());
        editor.apply();
    }


    private static final LoginCallback EMPTY = new LoginCallback() {
        @Override
        public void showProgress(boolean enabled) {
            Log.w("TAG", "showProgress");
        }

        @Override
        public void loginSuccess() {

        }

        @Override
        public void handleError(String error) {

        }

        @Override
        public void showUsernameEmptyError() {

        }

        @Override
        public void showPasswordLengthError(int length, int minPasswordLength) {

        }

    };

}
