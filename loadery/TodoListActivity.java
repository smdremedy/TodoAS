package pl.com.javatech.todoekspert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import pl.com.javatech.todoekspert.TodoApplication.LoginManager;
import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;

public class TodoListActivity extends ListActivity implements LoaderCallbacks<Cursor>{

	private static final int REQUEST_CODE = 123;
	private LoginManager mLoginManager;
	private TodoDao mTodoDao;
	private SimpleCursorAdapter mSimpleCursorAdapter;
	
	private BroadcastReceiver mRefreshReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(context, "Refreshed", Toast.LENGTH_SHORT).show();
			reloadCursor();
			
		}
	};
	
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mRefreshReceiver);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		registerReceiver(mRefreshReceiver, 
				new IntentFilter(RefreshIntentService.REFRESH_ACTION));
		
		
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLoginManager = ((TodoApplication)getApplication()).getLoginManager();
		mTodoDao = new TodoDao(getApplicationContext());
//		getContentResolver().registerContentObserver(TodoProvider.CONTENT_URI, 
//				true, new ContentObserver(null) {
//			@Override
//			public void onChange(boolean selfChange) {
//				
//				super.onChange(selfChange);
//				reloadCursor();
//			}
//		});
		
		if(mLoginManager.isNotLoggedIn()) {
			
			goToLogin();
			return;
			
		}
		
		setContentView(R.layout.activity_todo_list);
		
		//mListView = (ListView) findViewById(R.id.todos_lv);

		String[] from = new String[]{TodoDao.C_CONTENT, TodoDao.C_DONE};
		
		int[] to = new int[]{R.id.todo_list_cb, R.id.todo_list_cb};
		
		mSimpleCursorAdapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.todo_list_item, null, from, to, 0);
		
		ViewBinder viewBinder = new ViewBinder() {
			
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if(view.getId() == R.id.todo_list_cb && cursor.getColumnIndex(TodoDao.C_DONE) == columnIndex) {
					int done = cursor.getInt(columnIndex);
					
					((CheckBox)view).setChecked(done > 0);
					return true;
				}
				return false;
			}
		};
		
		mSimpleCursorAdapter.setViewBinder(viewBinder);
		
		//mListView.setAdapter(mAdapter);
		setListAdapter(mSimpleCursorAdapter);
		
		reloadCursor();
		getLoaderManager().initLoader(1, null, this);
		
	}

	private void reloadCursor() {
//		Cursor c = mTodoDao.query(mLoginManager.mUserId, true);
//		String selection = TodoDao.C_USER_ID + "=?";
//		Cursor c = managedQuery(TodoProvider.CONTENT_URI,
//				null, selection, new String []{mLoginManager.mUserId},
//				TodoDao.C_UPDATED_AT + " ASC");
//		mSimpleCursorAdapter.swapCursor(c);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_logout) {
			showLogoutDialog();
			return true;
		} else if (id == R.id.action_add) {
			Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);

			intent.putExtra("key", "value");
			startActivityForResult(intent, REQUEST_CODE);
			return true;
		} else if (id == R.id.action_refresh) {
			doRefresh();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void doRefresh() {
		
		
		Intent serviceIntent = new Intent(getApplicationContext(), RefreshIntentService.class);
		startService(serviceIntent);
		
		
	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == REQUEST_CODE) {
			if(resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
			} else if (resultCode == RESULT_OK) {
				
				
				Toast.makeText(getApplicationContext(), "Added"
						, Toast.LENGTH_SHORT).show();
				reloadCursor();
			}
		}
		
		
	}

	private void showLogoutDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.are_you_sure_);
		builder.setPositiveButton(android.R.string.yes, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				mLoginManager.logout();
				
				goToLogin();
				
			}

			
		});
		builder.setNegativeButton(android.R.string.no, null);
		builder.create().show();
	}

	private void goToLogin() {
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String selection = TodoDao.C_USER_ID + "=?";
		
		return new CursorLoader(getApplicationContext(),
				TodoProvider.CONTENT_URI,
				null, selection, new String []{mLoginManager.mUserId},
				TodoDao.C_UPDATED_AT + " ASC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		Log.d("TAG", "Reloaded cursor:" + data);
		mSimpleCursorAdapter.swapCursor(data);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.d("TAG", "Reset cursor");
		mSimpleCursorAdapter.swapCursor(null);
		
	}
	
}
