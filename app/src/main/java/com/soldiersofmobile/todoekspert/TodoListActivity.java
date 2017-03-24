package com.soldiersofmobile.todoekspert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.soldiersofmobile.todoekspert.db.TodoDao;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoListActivity extends AppCompatActivity implements TodoManager.TodoCallback {

    public static final int REQUEST_CODE = 123;
    private static final String[] FROM = {
            TodoDao.C_CONTENT,
            TodoDao.C_DONE,
            TodoDao.C_ID
    };
    private static final int[] TO = {
            R.id.item_done_check_box,
            R.id.item_done_check_box,
            R.id.item_delete_button
    };
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.content_todo_list)
    ListView contentTodoList;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.todo_progress)
    ProgressBar todoProgress;

    @Inject
    LoginManager loginManager;
    private TodoManager todoManager;
    //private ArrayAdapter<Todo> adapter;
    //private TodoAdapter adapter;
    private SimpleCursorAdapter adapter;
    private ProgressBar footerView;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(
                final Context context,
                final Intent intent
        ) {
            Cursor cursor = todoManager.getCursor();
            adapter.swapCursor(cursor);
            Toast.makeText(context, "Refreshed", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App application = (App) getApplication();
        todoManager = application.getTodoManager();
        App.getTodoComponent(this).inject(this);

        if (loginManager.hasToLogin()) {
            goToLogin();
            return;
        }
        setContentView(R.layout.activity_todo_list);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //        adapter = new ArrayAdapter<Todo>(this,
        //                R.layout.item_todo, R.id.item_done_check_box, todoManager.getTodos());
        //        adapter = new TodoAdapter();
        //        adapter.addAll(todoManager.getTodos());

        Cursor cursor = todoManager.getCursor();

        adapter = new SimpleCursorAdapter(this, R.layout.item_todo, cursor,
                FROM, TO, 0
        );
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(
                    View view,
                    Cursor cursor,
                    int columnIndex
            ) {
                if (columnIndex == cursor.getColumnIndex(TodoDao.C_DONE)) {
                    boolean done = cursor.getInt(columnIndex) > 0;
                    CheckBox checkBox = (CheckBox) view;
                    checkBox.setChecked(done);
                    return true;
                }
                return false;
            }
        });
        contentTodoList.setAdapter(adapter);
        contentTodoList.setEmptyView(todoProgress);
        footerView = new ProgressBar(this);
        contentTodoList.addFooterView(footerView);
        contentTodoList.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(
                    int page,
                    int totalItemsCount
            ) {
                refresh();
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        todoManager.setTodoCallback(this);
        registerReceiver(receiver, new IntentFilter(RefreshIntentService.REFRESH_ACTION));
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Todo todo = (Todo) data.getParcelableExtra(AddTodoActivity.TODO);
                Toast.makeText(this, todo.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todo_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
        todoManager.setTodoCallback(null);
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Todo todo = new Todo("test", true);
                Intent intent = new Intent(this, AddTodoActivity.class);
                intent.putExtra(AddTodoActivity.TODO, todo);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.action_refresh:
                refresh();
                break;
            case R.id.action_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.confirm_logout);
                builder.setMessage(R.string.are_you_sure);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            DialogInterface dialog,
                            int which
                    ) {
                        loginManager.logout();
                        goToLogin();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.setCancelable(false);
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showTodos(List<Todo> todos) {
        //adapter.addAll(todos);
        if (todoManager.isDone()) {
            contentTodoList.removeFooterView(footerView);
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void refresh() {

        Intent intent = new Intent(this, RefreshIntentService.class);
        startService(intent);

        //todoManager.fetchTodos(loginManager.getToken());

    }
}
