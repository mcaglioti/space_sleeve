package hackathon.spaceapps.spacesleeve;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import com.thalmic.myo.scanner.ScanActivity;
import android.Manifest;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;

public class MainActivity extends AppCompatActivity
{
    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private Intent helper;

    private BroadcastReceiver messageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            finish();
        }
    };

    private boolean isMyServiceRunning(Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);

        if(!isMyServiceRunning(MyoHelperService.class))
        {
            Intent scan = new Intent(this, ScanActivity.class);
            scan.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(scan);

            helper = new Intent(this, MyoHelperService.class);
            startService(helper);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(messageReceiver, new IntentFilter("0"));
        System.out.println("Statistics resumed.");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
            unregisterReceiver(messageReceiver);
        System.out.println("Mission paused.");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (helper != null) {
            stopService(helper);
        }
    }
}