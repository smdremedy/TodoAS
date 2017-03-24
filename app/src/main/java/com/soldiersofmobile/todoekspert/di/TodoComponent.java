package com.soldiersofmobile.todoekspert.di;

import javax.inject.Singleton;

import com.soldiersofmobile.todoekspert.LoginActivity;
import com.soldiersofmobile.todoekspert.LoginManager;
import com.soldiersofmobile.todoekspert.TodoListActivity;

import dagger.Component;

@Singleton
@Component(modules = {TodoModule.class})
public interface TodoComponent {

    LoginManager getLoginManager();

    void inject(LoginActivity loginActivity);

    void inject(TodoListActivity todoListActivity);
}
