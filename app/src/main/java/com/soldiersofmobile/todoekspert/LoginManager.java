package com.soldiersofmobile.todoekspert;

import android.content.SharedPreferences;

import com.soldiersofmobile.todoekspert.api.TodoApi;
import com.soldiersofmobile.todoekspert.api.User;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class LoginManager {


    private final TodoApi todoApi;
    private Converter<ResponseBody, ErrorResponse> errorConverter;
    private SharedPreferences preferences;

    //private LoginAsyncTask loginAsyncTask;

    private LoginCallback loginCallback;
    private Call<User> loginCall;

    public LoginManager(TodoApi todoApi, Converter<ResponseBody, ErrorResponse> errorConverter, SharedPreferences preferences) {
        this.todoApi = todoApi;
        this.errorConverter = errorConverter;
        this.preferences = preferences;
    }

    public void login(String username, String password) {

        loginCall = todoApi.getLogin(username, password);
//        loginAsyncTask = new LoginAsyncTask(loginCall, errorConverter);
//        loginAsyncTask.setLoginCallback(loginCallback);
//
//        loginAsyncTask.execute(username, password);
        loginCallback.showProgress(true);
        loginCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                loginCallback.showProgress(false);
                if (response.isSuccessful()) {
                    User user = response.body();
                    saveUser(user);

                    loginCallback.handleError(null);
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
                loginCallback.showProgress(false);
                loginCallback.handleError(t.getMessage());
            }
        });
    }

    private void saveUser(User user) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", user.getSessionToken());
        editor.putString("user_id", user.getObjectId());
        editor.apply();

    }

    public void setLoginCallback(LoginCallback loginCallback) {
        this.loginCallback = loginCallback;
//        if (loginAsyncTask != null) {
//            loginAsyncTask.setLoginCallback(loginCallback);
//        }

    }
}
