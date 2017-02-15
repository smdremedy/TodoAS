package com.soldiersofmobile.todoekspert;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soldiersofmobile.todoekspert.api.TodoApi;
import com.soldiersofmobile.todoekspert.db.DBHelper;
import com.soldiersofmobile.todoekspert.db.TodoDao;
import com.squareup.leakcanary.LeakCanary;

import java.lang.annotation.Annotation;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

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

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new Timber.Tree() {
                @Override
                protected void log(int priority, String tag, String message, Throwable t) {

                }
            });
        }
        Gson gson = new GsonBuilder()
                //.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addNetworkInterceptor(new StethoInterceptor())
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

        TodoDao todoDao = new TodoDao(new DBHelper(this));


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        loginManager = new LoginManager(todoApi, errorConverter, preferences);
        todoManager = new TodoManager(todoApi, todoDao, loginManager);
    }


}
