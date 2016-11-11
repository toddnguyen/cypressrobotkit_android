/*
Copyright (c) 2016, Cypress Semiconductor Corporation
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.


For more information on Cypress BLE products visit:
http://www.cypress.com/products/bluetooth-low-energy-ble
 */

package com.example.todd.cypressrobotcontroller;

        import android.content.BroadcastReceiver;
        import android.content.ComponentName;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.ServiceConnection;
        import android.os.Bundle;
        import android.os.IBinder;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.MotionEvent;
        import android.view.View;
        import android.widget.Button;
        import android.widget.SeekBar;
        import android.widget.TextView;

/**
 * This Activity provides the user interface to control the robot.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class ControlActivity extends AppCompatActivity {

    // Objects to access the layout items for Tach, Buttons, and Seek bars
    private static TextView mIR1Text;
    private static TextView mIR2Text;
    private static TextView mUltrasonicText;
    private static SeekBar mSpeedSeekBar;

    private static Button mForwardButton;
    private static Button mBackwardButton;
    private static Button mRightButton;
    private static Button mLeftButton;
    private static Button mLineFollowingButton;
    private static Button mObstacleAvoidanceButton;

    private static int state = 0;

    // This tag is used for debug messages
    private static final String TAG = ControlActivity.class.getSimpleName();

    private static String mDeviceAddress;
    private static PSoCBleRobotService mPSoCBleRobotService;

    private static int mSpeed = 15;
    
    /**
     * This manages the lifecycle of the BLE service.
     * When the service starts we get the service object, initialize the service, and connect.
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.i(TAG, "onServiceConnected");
            mPSoCBleRobotService = ((PSoCBleRobotService.LocalBinder) service).getService();
            if (!mPSoCBleRobotService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the car database upon successful start-up initialization.
            mPSoCBleRobotService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mPSoCBleRobotService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_control);

        final Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(ScanActivity.EXTRAS_BLE_ADDRESS);

        // Bind to the BLE service
        Log.i(TAG, "Binding Service");
        Log.d("BLE", "STARTING INTENT");

        Intent RobotServiceIntent = new Intent(this, PSoCBleRobotService.class);
        bindService(RobotServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        setContentView(R.layout.activity_control);
        // Assign the various layout objects to the appropriate variables
        mIR1Text = (TextView) findViewById(R.id.IR1);
        mIR2Text = (TextView) findViewById(R.id.IR2);
        mUltrasonicText = (TextView) findViewById(R.id.Ultrasonic);
        mSpeedSeekBar = (SeekBar) findViewById(R.id.speedBar);
        mForwardButton = (Button) findViewById(R.id.forward_button);
        mBackwardButton = (Button) findViewById(R.id.backward_button);
        mRightButton = (Button) findViewById(R.id.right_button);
        mLeftButton = (Button) findViewById(R.id.left_button);
        mLineFollowingButton = (Button) findViewById(R.id.linefollowing);
        mObstacleAvoidanceButton = (Button) findViewById(R.id.obstacleavoidance);

        mForwardButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPSoCBleRobotService.moveForward(mSpeed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        mPSoCBleRobotService.stopMoving();
                        return true;
                }
                return false;
            }
        });

        mBackwardButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPSoCBleRobotService.moveBackward(mSpeed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        mPSoCBleRobotService.stopMoving();
                        return true;
                }
                return false;
            }
        });

        mRightButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPSoCBleRobotService.rotateRight(mSpeed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        mPSoCBleRobotService.stopMoving();
                        return true;
                }
                return false;
            }
        });

        mLeftButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPSoCBleRobotService.rotateLeft(mSpeed);
                        return true;
                    case MotionEvent.ACTION_UP:
                        mPSoCBleRobotService.stopMoving();
                        return true;
                }
                return false;
            }
        });

        mLineFollowingButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mPSoCBleRobotService.setLineFollowing();
            }
        });

        mObstacleAvoidanceButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mPSoCBleRobotService.setObstacleAvoidance();
            }
        });

        mSpeedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mSpeed = i;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    } /* End of onCreate method */

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mRobotUpdateReceiver, makeRobotUpdateIntentFilter());
        if (mPSoCBleRobotService != null) {
            final boolean result = mPSoCBleRobotService.connect(mDeviceAddress);
            Log.i(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mRobotUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mPSoCBleRobotService = null;
    }

    /**
     * Scale the speed read from the slider (0 to 20) to
     * what the car object expects (-100 to +100).
     *
     * @param speed Input speed from the slider
     * @return scaled value of the speed
     */
    private int scaleSpeed(int speed) {
        final int SCALE = 10;
        final int OFFSET = 100;

        return ((speed * SCALE) - OFFSET);
    }

    /**
     * Handle broadcasts from the Car service object. The events are:
     * ACTION_CONNECTED: connected to the car.
     * ACTION_DISCONNECTED: disconnected from the car.
     * ACTION_DATA_AVAILABLE: received data from the car.  This can be a result of a read
     * or notify operation.
     */
    private final BroadcastReceiver mRobotUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            switch (action) {
                case PSoCBleRobotService.ACTION_CONNECTED:
                    state = 1;
                    Log.d("BLE", "CONNECTED");
                    // No need to do anything here. Service discovery is started by the service.
                    break;
                case PSoCBleRobotService.ACTION_DISCONNECTED:
                    mPSoCBleRobotService.close();
                    break;
                case PSoCBleRobotService.ACTION_DATA_AVAILABLE:
                    // This is called after a Notify completes
                    mIR1Text.setText(String.format("%d", PSoCBleRobotService.getIR1()));
                    mIR2Text.setText(String.format("%d", PSoCBleRobotService.getIR2()));
                    mUltrasonicText.setText(String.format("%d", PSoCBleRobotService.getUltrasonic()));
                    break;
            }
        }
    };

    /**
     * This sets up the filter for broadcasts that we want to be notified of.
     * This needs to match the broadcast receiver cases.
     *
     * @return intentFilter
     */
    private static IntentFilter makeRobotUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PSoCBleRobotService.ACTION_CONNECTED);
        intentFilter.addAction(PSoCBleRobotService.ACTION_DISCONNECTED);
        intentFilter.addAction(PSoCBleRobotService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}