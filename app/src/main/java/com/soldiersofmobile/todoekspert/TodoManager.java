package com.soldiersofmobile.todoekspert;

import android.util.Log;

import com.soldiersofmobile.todoekspert.api.TodoApi;
import com.soldiersofmobile.todoekspert.api.model.TodosResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodoManager implements Callback<TodosResponse> {

    private TodoApi todoApi;

    private int limit = 10;
    private int skip = 0;
    private Call<TodosResponse> call;
    private List<Todo> todos = new ArrayList<>();

    private boolean done;

    public boolean isDone() {
        return done;
    }

    private TodoCallback todoCallback;

    public List<Todo> getTodos() {
        return todos;
    }

    public void setTodoCallback(TodoCallback todoCallback) {
        this.todoCallback = todoCallback;
    }

    interface TodoCallback {
        void showTodos(List<Todo> todos);
    }

    public TodoManager(TodoApi todoApi) {

        this.todoApi = todoApi;
    }

    public void fetchTodos(String token) {

        call = todoApi.getTodos(token, limit, skip);
        call.enqueue(this);

    }

    @Override
    public void onResponse(Call<TodosResponse> call, Response<TodosResponse> response) {
        if (response.isSuccessful()) {
            TodosResponse todosResponse = response.body();

            List<Todo> results = todosResponse.getResults();

            if (results.size() == 0) {
                done = true;
            }
            for (Todo todo : results) {
                Log.d("TAG", todo.toString());
            }

            todos.addAll(results);
            skip += results.size();
            if (todoCallback != null) {
                todoCallback.showTodos(results);
            }
        }
    }

    @Override
    public void onFailure(Call<TodosResponse> call, Throwable t) {

    }
}
