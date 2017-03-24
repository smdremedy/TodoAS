package com.soldiersofmobile.todoekspert.di;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Singleton;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.soldiersofmobile.todoekspert.ErrorResponse;
import com.soldiersofmobile.todoekspert.LoginManager;
import com.soldiersofmobile.todoekspert.api.TodoApi;
import com.soldiersofmobile.todoekspert.api.model.User;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Module
public class TodoModule {

    public static final String DATE_FORMAT = "dateFormat";
    public static final String URL = "url";
    private final Context context;

    public TodoModule(Context context) {this.context = context;}

    @Singleton
    @Provides
    public LoginManager provideLoginManager(
            TodoApi todoApi,
            Converter<ResponseBody, ErrorResponse> errorConverter,
            SharedPreferences preferences
    ) {
        return new LoginManager(todoApi, errorConverter, preferences);
    }

    @Provides
    public TodoApi provideTodoApi(Retrofit retrofit) {
        return retrofit.create(TodoApi.class);
    }

    @Provides
    public Converter<ResponseBody, ErrorResponse> provideConverter(Retrofit retrofit) {
        Converter<ResponseBody, ErrorResponse> errorConverter
                = retrofit.responseBodyConverter(
                ErrorResponse.class,
                new Annotation[0]
        );
        return errorConverter;
    }

    @Qualifier
    @Documented
    @Retention(RUNTIME)
    public @interface Url {
    }

    @Named(DATE_FORMAT)
    @Provides
    public String provideDateFormat() {
        return "yyyy-MM-dd";
    }

    @Url
    @Provides
    public String provideUrl() {
        return "https://parseapi.back4app.com";
    }

    @Provides
    public Retrofit provideRetrofit(
            @Named(DATE_FORMAT) String dateFormat,
            @Url String url
    ) {
        Gson gson = new GsonBuilder()
                //.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setDateFormat(dateFormat)
                .create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }

    @Provides
    public SharedPreferences providePreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    public Context provideContext() {
        return context;
    }
}
