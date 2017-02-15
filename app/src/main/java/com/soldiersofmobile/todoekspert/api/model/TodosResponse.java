package com.soldiersofmobile.todoekspert.api.model;

import com.soldiersofmobile.todoekspert.Todo;

import java.util.List;

public class TodosResponse {

    private List<Todo> results;

    public List<Todo> getResults() {
        return results;
    }

    public void setResults(List<Todo> results) {
        this.results = results;
    }
}
