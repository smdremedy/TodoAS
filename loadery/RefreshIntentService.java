package pl.com.javatech.todoekspert;

import org.json.JSONArray;
import org.json.JSONObject;

import pl.com.javatech.todoekspert.TodoApplication.LoginManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class RefreshIntentService extends IntentService {

	public static final String REFRESH_ACTION = "pl.com.javatech.todoekspert.REFRESH_ACTION";
	private static final String TAG = RefreshIntentService.class.getSimpleName();
	private static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;

	public RefreshIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		Log.d(TAG, "Refresh in service");
		
		try {
			TodoApplication todoApplication = (TodoApplication)getApplication();
			TodoDao dao = todoApplication.getTodoDao();
			LoginManager loginManager = todoApplication.getLoginManager();
			String result = HttpUtils.getTodos(
					loginManager.mToken);
			JSONObject jsonObject = new JSONObject(result);
			JSONArray arrayOfTodos = jsonObject.getJSONArray("results");
			
			long createdAt = dao.getLatestCreatedAtTime(loginManager.mUserId);
			int newItems = 0;
			for (int i = 0; i < arrayOfTodos.length(); i++) {
				JSONObject todoJson = arrayOfTodos.getJSONObject(i);
				Todo todo = Todo.fromJsonObject(todoJson);
				if(todo.createdAt.getTime() > createdAt) {
					newItems++;
				}
				//dao.insertOrUpdate(todo);
				ContentValues values = new ContentValues();
		        values.put(TodoDao.C_ID, todo.objectId);
				values.put(TodoDao.C_CONTENT, todo.content);
				values.put(TodoDao.C_DONE, todo.done);
				values.put(TodoDao.C_CREATED_AT, todo.createdAt.getTime());
		        values.put(TodoDao.C_UPDATED_AT, todo.updatedAt.getTime());
				values.put(TodoDao.C_USER_ID, todo.userId);
				
				getContentResolver().insert(TodoProvider.CONTENT_URI, values);
			}
			
			if(newItems > 0 ) {
				showNotification(newItems);
			}
			
//			Intent broadcastIntent = new Intent(REFRESH_ACTION);
//			sendBroadcast(broadcastIntent);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		
	}

	private void showNotification(int newItems) {
		if (mNotificationManager == null) {
            mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mNotificationManager.cancel(NOTIFICATION_ID);
        String notificationSummary = String.format("New %d todos", newItems);
        
        

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

        builder.setAutoCancel(true);
        builder.setContentTitle("New todos have arrived");
        builder.setContentText(notificationSummary);

        builder.setSmallIcon(R.drawable.ic_launcher);

        Intent backIntent = new Intent(this, TodoListActivity.class);
        backIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, backIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentIntent(contentIntent);

        
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
		
	}

}
