package com.soldiersofmobile.todoekspert;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soldiersofmobile.todoekspert.api.TodoApi;
import com.squareup.leakcanary.LeakCanary;

import java.lang.annotation.Annotation;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private LoginManager loginManager;
    private TodoManager todoManager;

    public LoginManager getLoginManager() {
        return loginManager;
    }

    public TodoManager getTodoManager() {
        return todoManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);

        Gson gson = new GsonBuilder()
                //.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("https://parseapi.back4app.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        TodoApi todoApi = retrofit.create(TodoApi.class);
        Converter<ResponseBody, ErrorResponse> errorConverter
                = retrofit.responseBodyConverter(ErrorResponse.class,
                new Annotation[0]);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        loginManager = new LoginManager(todoApi, errorConverter, preferences);
        todoManager = new TodoManager(todoApi);
    }


}
