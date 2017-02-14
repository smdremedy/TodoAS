package com.soldiersofmobile.todoekspert;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.soldiersofmobile.todoekspert.api.User;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;

class LoginAsyncTask extends AsyncTask<String, Integer, String> {

    private static final LoginCallback EMPTY = new LoginCallback() {
        @Override
        public void showProgress(boolean enabled) {
            Log.w("TAG", "showProgress");
        }

        @Override
        public void handleError(String error) {

        }

        @Override
        public void updateProgress(int progress) {

        }
    };

    private LoginCallback loginCallback = EMPTY;
    private Call<User> loginCall;
    private Converter<ResponseBody, ErrorResponse> errorConverter;

    public LoginAsyncTask(Call<User> loginCall, Converter<ResponseBody, ErrorResponse> errorConverter) {

        this.loginCall = loginCall;
        this.errorConverter = errorConverter;
    }

    public void setLoginCallback(LoginCallback loginCallback) {
        if (loginCallback == null) {
            this.loginCallback = EMPTY;
        } else {
            this.loginCallback = loginCallback;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        loginCallback.showProgress(true);

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        loginCallback.updateProgress(values[0]);
    }


    @Override
    protected String doInBackground(String... params) {

        return validateCredentials(params);
    }

    @Nullable
    private String validateCredentials(String[] params) {

        try {
            Response<User> response = loginCall.execute();
            if (response.isSuccessful()) {
                User user = response.body();
                return null;
            } else {
                ResponseBody responseBody = response.errorBody();
                ErrorResponse errorResponse = errorConverter.convert(responseBody);
                return errorResponse.getError();

            }
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String error) {
        super.onPostExecute(error);
        loginCallback.showProgress(false);
        loginCallback.handleError(error);

    }
}
