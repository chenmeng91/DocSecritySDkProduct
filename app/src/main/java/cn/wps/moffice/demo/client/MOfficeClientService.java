package cn.wps.moffice.demo.client;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import cn.wps.moffice.client.OfficeServiceClient;
import cn.wps.moffice.demo.util.Define;
import cn.wps.moffice.service.OfficeService;

public class MOfficeClientService extends Service
{
	protected static final String TAG = MOfficeClientService.class.getSimpleName();
	
	public static final String BROADCAST_ACTION = "cn.wps.moffice.broadcast.action.serviceevent";

	protected final Handler handler = new Handler();
	protected final Intent intent = new Intent(BROADCAST_ACTION);
	
	protected final OfficeServiceClient.Stub mBinder = new OfficeServiceClientImpl(this);
	
	public MOfficeClientService()
	{}
	
	@Override
	public void onCreate()
	{
		Log.i(TAG, "onCreate(): " + this.hashCode());
	}
	
	@Override
	public void onRebind(Intent intent) {
		Log.i(TAG, "onRebind(): " + this.hashCode() + ", " + intent.toString());
		super.onRebind(intent);
		bindOfficeService(getApplicationContext());
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		Log.i(TAG, "onBind(): " + this.hashCode() + ", " + intent.toString());
		bindOfficeService(getApplicationContext());
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent)
	{
		Log.i(TAG, "onUnbind(): " + this.hashCode() + ", " + intent.toString());
		getApplicationContext().unbindService(connection);
		super.onUnbind(intent);
		return true;
	}
	
	@Override
	public void onStart(Intent intent, int startId)
	{
		handler.removeCallbacks(sendUpdatesToUI);
		handler.postDelayed(sendUpdatesToUI, 1000);
	}
	
    protected Runnable sendUpdatesToUI = new Runnable()
    {
    	public void run()
    	{
    		displayServiceStatus();
    		handler.postDelayed(sendUpdatesToUI, 1000);
    	}
    }; 
    
    public OfficeService getOfficeService()
    {
    	return mService;
    }
    
    boolean bindOfficeService()
    {
    	return bindOfficeService(getApplicationContext());
    }
	// bind service
	private boolean bindOfficeService(Context context)
	{
		final Intent intent = new Intent(Define.OFFICE_SERVICE_ACTION);
		intent.setPackage("com.kingsoft.moffice_pro");
		intent.putExtra("DisplayView", true);
		if (!context.bindService(intent, connection, Service.BIND_AUTO_CREATE))
		{
			// bind failed, maybe wps office is not installd yet.
			context.unbindService(connection);
			return false;
		}
		
		return true;
	}
	
	OfficeService mService;
	
	/**
	 * connection of binding
	 */
	private ServiceConnection connection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			mService = OfficeService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name)
		{
			mService = null;
		}
	};
	
    private void displayServiceStatus()
    {
    	// sendBroadcast( intent );
        // Intent intent = new Intent( this, MOfficeClientActivity.class );
        // intent.setAction( Intent.ACTION_VIEW );
        // intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        // intent.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
        // startActivity( intent );
    }

	@Override
	public void onDestroy()
	{
		Log.i(TAG, "onDestroy(): " + this.hashCode());
		handler.removeCallbacks(sendUpdatesToUI);
	}
}
