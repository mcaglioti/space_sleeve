package hackathon.spaceapps.spacesleeve;

import android.content.Intent;
import android.app.Service;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.XDirection;
import android.os.IBinder;
import android.widget.Toast;
import android.util.Log;

public class MyoHelperService extends Service
{
    private static final String TAG = "MyoHelperService";
    private Intent[] activityViews = new Intent[3];
    private Integer currentScreen = 0;
    private boolean lockstate = true;
    private Toast mToast;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener()
    {
        @Override
        public void onConnect(Myo myo, long timestamp)
        {
            myo.lock();
            currentScreen = 0;
            sendBroadcast(new Intent(currentScreen.toString()));
            activityViews[currentScreen].addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(activityViews[currentScreen]);
            showToast("Space Sleeve connected.");
        }

        @Override
        public void onDisconnect(Myo myo, long timestamp)
        {
            showToast("Space Sleeve disconnected.");
        }

        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) throws java.lang.IllegalArgumentException
        {
            showToast("Space Sleeve on.");

            if (myo.getArm() == Arm.LEFT)
            {
                //System.out.println(R.string.arm_left);
            }
            else if (myo.getArm() == Arm.RIGHT)
            {
                //System.out.println(R.string.arm_right);
            }
        }

        // onArmUnsync() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
        // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
        // when Myo is moved around on the arm.
        @Override
        public void onArmUnsync(Myo myo, long timestamp) throws java.lang.IllegalArgumentException
        {
            showToast("Space Sleeve removed.");
        }

        // onUnlock() is called whenever a synced Myo has been unlocked. Under the standard locking
        // policy, that means poses will now be delivered to the listener.
        @Override
        public void onUnlock(Myo myo, long timestamp)
        {
            showToast("Space Sleeve gestures unlocked.");
        }

        // onLock() is called whenever a synced Myo has been locked. Under the standard locking
        // policy, that means poses will no longer be delivered to the listener.
        @Override
        public void onLock(Myo myo, long timestamp)
        {
            showToast("Space Sleeve gestures locked.");
        }

        // onOrientationData() is called whenever a Myo provides its current orientation,
        // represented as a quaternion.
        /*
        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
            float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
            float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
            float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));
            // Adjust roll and pitch for the orientation of the Myo on the arm.
            if (myo.getXDirection() == XDirection.TOWARD_ELBOW) {
                roll *= -1;
                pitch *= -1;
            }
            // Next, we apply a rotation to the text view using the roll, pitch, and yaw.
            //System.out.println(roll);
            //System.out.println(pitch);
            //System.out.println(yaw);
        }
        */

        // onPose() is called whenever a Myo provides a new pose.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) throws java.lang.IllegalArgumentException {
            // Handle the cases of the Pose enumeration, and change the text of the text view
            // based on the pose we receive.
            if(pose == Pose.DOUBLE_TAP)
            {
                lockstate = !lockstate;
                if (!lockstate)
                {
                    showToast("Space Sleeve gestures unlocked.");
                }
            }
            else
            {
                if(!lockstate)
                {
                    switch (pose)
                    {
                        case UNKNOWN:
                            System.out.println("Unknown position.");
                            break;
                        case REST:
                        case FIST:
                            break;
                        case WAVE_IN:
                            sendBroadcast(new Intent(currentScreen.toString()));
                            currentScreen++;
                            currentScreen = currentScreen % 3;
                            activityViews[currentScreen].addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(activityViews[currentScreen]);
                            System.out.println(getString(R.string.pose_wavein));
                            break;
                        case WAVE_OUT:
                            sendBroadcast(new Intent(currentScreen.toString()));
                            currentScreen--;
                            currentScreen = (currentScreen % 3 + 3) % 3;
                            activityViews[currentScreen].addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(activityViews[currentScreen]);
                            System.out.println(getString(R.string.pose_waveout));
                            break;
                        case FINGERS_SPREAD:
                            System.out.println(getString(R.string.pose_fingersspread));
                            break;
                        default:
                            System.out.println("No movement registered.");
                    }
                    System.out.println(lockstate);
                }
                else
                {
                    showToast("Space Sleeve gestures locked.");
                }
            }
        }
    };

    @Override
    public void onCreate()
    {
        super.onCreate();

        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            System.out.println("Couldn't initialize hub.");
            return;
        }

        hub.setLockingPolicy(Hub.LockingPolicy.NONE);

        activityViews[0] = new Intent(this, MainActivity.class);
        activityViews[1] = new Intent(this, MissionActivity.class);
        activityViews[2] = new Intent(this, TeamActivity.class);

        hub.addListener(mListener);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // We don't want any callbacks when the Service is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);
        Hub.getInstance().shutdown();
    }

    private void showToast(String text) {
        Log.w(TAG, text);
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }
}