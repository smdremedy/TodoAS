package com.soldiersofmobile.todoekspert;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoAdapter extends BaseAdapter {

    private List<Todo> todos = new ArrayList<>();

    public void addAll(List<Todo> todos) {
        this.todos.addAll(todos);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return todos.size();
    }

    @Override
    public Todo getItem(int position) {
        return todos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
            view.setTag(new ViewHolder(view));
        }
        Todo todo = getItem(position);

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.itemDoneCheckBox.setText(todo.getContent());
        viewHolder.itemDoneCheckBox.setChecked(todo.isDone());
        viewHolder.itemDeleteButton.setText(todo.getObjectId());


        return view;
    }

    static class ViewHolder {
        @BindView(R.id.item_done_check_box)
        CheckBox itemDoneCheckBox;
        @BindView(R.id.item_delete_button)
        Button itemDeleteButton;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
