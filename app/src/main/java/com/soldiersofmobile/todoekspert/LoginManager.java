package com.soldiersofmobile.todoekspert;

import com.soldiersofmobile.todoekspert.api.TodoApi;
import com.soldiersofmobile.todoekspert.api.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;

public class LoginManager {


    private final TodoApi todoApi;
    private Converter<ResponseBody, ErrorResponse> errorConverter;

    private LoginAsyncTask loginAsyncTask;

    private LoginCallback loginCallback;
    private Call<User> loginCall;

    public LoginManager(TodoApi todoApi, Converter<ResponseBody, ErrorResponse> errorConverter) {
        this.todoApi = todoApi;
        this.errorConverter = errorConverter;
    }

    public void login(String username, String password) {

        loginCall = todoApi.getLogin(username, password);
        loginAsyncTask = new LoginAsyncTask(loginCall, errorConverter);
        loginAsyncTask.setLoginCallback(loginCallback);

        loginAsyncTask.execute(username, password);
    }

    public void setLoginCallback(LoginCallback loginCallback) {
        this.loginCallback = loginCallback;
        if (loginAsyncTask != null) {
            loginAsyncTask.setLoginCallback(loginCallback);
        }

    }
}
