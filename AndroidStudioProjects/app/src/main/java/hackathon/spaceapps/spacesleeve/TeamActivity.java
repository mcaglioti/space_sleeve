package hackathon.spaceapps.spacesleeve;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

public class TeamActivity extends AppCompatActivity
{
    private boolean isRegistered = false;
    private ImageView imgView = null;

    private BroadcastReceiver messageReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        //ImageView imgView = (ImageView)findViewById(R.id.team_ph);
        //imgView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.team));
/*
        if(imgView != null)
        {
            ((BitmapDrawable)imgView.getDrawable()).getBitmap().recycle();
        }
        imgView = (ImageView)findViewById(R.id.team_ph);
        imgView.setImageResource(R.drawable.team);
        */
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(messageReceiver, new IntentFilter("2"));
        System.out.println("Team resumed.");
    }

    @Override
    protected void onPause()
    {
        super.onPause();

            unregisterReceiver(messageReceiver);

        System.out.println("Team paused.");
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
