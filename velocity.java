package com.example.android.sensorvaluesrec;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class velocity extends AppCompatActivity  implements SensorEventListener, GestureDetector.OnGestureListener {
    TextView textAvtion, textVelocityX, textVelocityY,
            textMaxVelocityX, textMaxVelocityY;
    public boolean fa=false,fv=false,fd=false;
    VelocityTracker velocityTracker = null;
    GestureDetector gestureDetector;
    float maxXVelocity;
    float maxYVelocity;
    private float lastX, lastY, lastZ;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float vibrateThreshold = 0;

    private TextView currentX, currentY, currentZ;//, maxX, maxY, maxZ;

    public Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_velocity);
        textAvtion = (TextView) findViewById(R.id.action);
        textVelocityX = (TextView) findViewById(R.id.velocityx);
        textVelocityY = (TextView) findViewById(R.id.velocityy);
        textMaxVelocityX = (TextView) findViewById(R.id.maxvelocityx);
        textMaxVelocityY = (TextView) findViewById(R.id.maxvelocityy);

        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        verifyStoragePermissions(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fai! we dont have an accelerometer!
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        gestureDetector = new GestureDetector(velocity.this, velocity.this);


        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask()
        {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try
                        {
                            new PerformBackgroundTask().execute();

                        }
                        catch (Exception e)
                        {

                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10000); //execute in every 5s*


    }
    public class PerformBackgroundTask extends AsyncTask<String, Void, String> {
        public void onPreExecute() {
        }

        public String doInBackground(String... args0) {
            try {
                String status="";
            //    Toast.makeText(getApplicationContext(), "Checking", Toast.LENGTH_SHORT).show();
//                if(fa==true)
//                {
                    File FileDir =   new File(Environment.getExternalStorageDirectory(), "A");  // getDir();
                    FileInputStream fis = new FileInputStream(FileDir+"/accelerometer.txt");
                    byte b[]=new byte[fis.available()];
                    fis.read(b);
                    String a=new String(b);
                    status+=a;
                //    Toast.makeText(getApplicationContext(), a, Toast.LENGTH_SHORT).show();
                    fis.close();

          //      }
//                if (fv==true)
//                {
                  //  File FileDir1 =   new File(Environment.getExternalStorageDirectory(), "A");  // getDir();
                    FileInputStream fis1 = new FileInputStream(FileDir+"/Velocity.txt");
                    byte b1[]=new byte[fis1.available()];
                    fis1.read(b1);
                    String v=new String(b1);
                    status+=v;
              //     Toast.makeText(getApplicationContext(), v, Toast.LENGTH_SHORT).show();
                    fis1.close();

//                }
//                if(fd==true)
//                {
                //   File FileDir =   new File(Environment.getExternalStorageDirectory(), "A");  // getDir();
                    FileInputStream fis2 = new FileInputStream(FileDir+"/direction.txt");
                    byte b2[]=new byte[fis2.available()];
                    fis1.read(b2);
                    String d=new String(b2);
                    status+=d;
                //    Toast.makeText(getApplicationContext(), d, Toast.LENGTH_SHORT).show();
                    fis1.close();

//                }

                return status;
                //   }

            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }



        @Override
        protected void onPostExecute(String status) {
            if(status.equals(""))
            {

                Toast.makeText(getApplicationContext(), ">>"+status, Toast.LENGTH_SHORT).show();
            }
            else

            {




            }

        }

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
                velocityTracker.addMovement(event);
                maxXVelocity = 0;
                maxYVelocity = 0;

                textVelocityX.setText("X-velocity (pixel/s): 0");
                textVelocityY.setText("Y-velocity (pixel/s): 0");
                textMaxVelocityX.setText("max. X-velocity: 0");
                textMaxVelocityY.setText("max. Y-velocity: 0");

                break;
            case MotionEvent.ACTION_MOVE:
                velocityTracker.addMovement(event);
                velocityTracker.computeCurrentVelocity(1000);
                //1000 provides pixels per second

                float xVelocity = velocityTracker.getXVelocity();
                float yVelocity = velocityTracker.getYVelocity();

                if(xVelocity > maxXVelocity){
                    //max in right side
                    maxXVelocity = xVelocity;
                }

                if(yVelocity > maxYVelocity){
                    //Max in down side
                    maxYVelocity = yVelocity;
                }

                textVelocityX.setText("X-velocity (pixel/s): " + xVelocity);
                textVelocityY.setText("Y-velocity (pixel/s): " + yVelocity);
                textMaxVelocityX.setText("max. X-velocity: " + maxXVelocity);
                textMaxVelocityY.setText("max. Y-velocity: " + maxYVelocity);

                add_Velocity("X-velocity (pixel/s): " + xVelocity+"Y-velocity (pixel/s): " + yVelocity+"max. X-velocity: " + maxXVelocity+"max. Y-velocity: " + maxYVelocity+"\n##\n");
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                velocityTracker.recycle();
                break;
        }
        return gestureDetector.onTouchEvent(event);
       // return true;
    }
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values
        displayMaxValues();

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;
        if (deltaZ < 2)
            deltaZ = 0;

        // set the last know values of x,y,z
        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

        vibrate();

    }

    // if the change in the accelerometer value is big enough, then vibrate!
    // our threshold is MaxValue/2
    public void vibrate() {
        if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
            v.vibrate(50);
        }
    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
        add_accelerometer(Float.toString(deltaX)+"=="+Float.toString(deltaY)+"=="+Float.toString(deltaZ)+"\n##\n");


    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
         //   maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
        //    maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
        //    maxZ.setText(Float.toString(deltaZMax));
        }
    }


    @Override
    public boolean onFling(MotionEvent motionEvent1, MotionEvent motionEvent2, float X, float Y) {

        if(motionEvent1.getY() - motionEvent2.getY() > 50){

            Toast.makeText(velocity.this , " Swipe Up " , Toast.LENGTH_LONG).show();


            add_direction("Swipe Up");

            return true;
        }

        if(motionEvent2.getY() - motionEvent1.getY() > 50){

            Toast.makeText(velocity.this , " Swipe Down " , Toast.LENGTH_LONG).show();
            add_direction("Swipe Down");
            return true;
        }

        if(motionEvent1.getX() - motionEvent2.getX() > 50){

            Toast.makeText(velocity.this , " Swipe Left " , Toast.LENGTH_LONG).show();
            add_direction("Swipe Left");
            return true;
        }

        if(motionEvent2.getX() - motionEvent1.getX() > 50) {

            Toast.makeText(velocity.this, " Swipe Right ", Toast.LENGTH_LONG).show();
            add_direction("Swipe Right");
            return true;
        }
        else {

            return true ;
        }
    }
public void add_direction(String data){
fd=true;
    File dir =   new File(Environment.getExternalStorageDirectory(), "A");  // getDir();

    String s1="";

    try
    {

        FileInputStream fis1 = new FileInputStream(dir+"/direction.txt");
        byte b1[]=new byte[fis1.available()];
        fis1.read(b1);
        s1=new String(b1);

        fis1.close();
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }



    try
    {

        File FileDir =   new File(Environment.getExternalStorageDirectory(), "A");  // getDir();
        if (!FileDir.exists() && !FileDir.mkdirs()) {

        }
        FileDir.mkdirs();
        String ndata=s1+"\n"+data;
        File fsrc =new File(FileDir+"/direction.txt");
        FileOutputStream fosrc =new FileOutputStream(fsrc);
        fosrc.write(ndata.getBytes());
        fosrc.close();

         //  Toast.makeText(getApplicationContext(), "Saved Successfully direction", Toast.LENGTH_SHORT).show();


    }
    catch(Exception e)
    {
        e.printStackTrace();
    }





}


    public void add_accelerometer(String data){
fa=true;
        File dir =   new File(Environment.getExternalStorageDirectory(), "A");  // getDir();

        String s1="";

        try
        {

            FileInputStream fis1 = new FileInputStream(dir+"/accelerometer.txt");
            byte b1[]=new byte[fis1.available()];
            fis1.read(b1);
            s1=new String(b1);

            fis1.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }



        try
        {

            File FileDir =   new File(Environment.getExternalStorageDirectory(), "A");  // getDir();
            if (!FileDir.exists() && !FileDir.mkdirs()) {

            }
            FileDir.mkdirs();
            String ndata=s1+"\n"+data;   // concatination new data
            File fsrc =new File(FileDir+"/accelerometer.txt");
            FileOutputStream fosrc =new FileOutputStream(fsrc);
            fosrc.write(ndata.getBytes());
            fosrc.close();

            //   Toast.makeText(getApplicationContext(), "Saved Successfully accelerometer", Toast.LENGTH_SHORT).show();


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }





    }



    public void add_Velocity(String data){
        fv=true;
        File FileDir =   new File(Environment.getExternalStorageDirectory(), "A");  // getDir();
        if (!FileDir.exists() && !FileDir.mkdirs()) {

        }
        FileDir.mkdirs();
        File dir =   new File(Environment.getExternalStorageDirectory(), "A");  // getDir();

        String s1="";

        try
        {

            FileInputStream fis1 = new FileInputStream(dir+"/Velocity.txt");
            byte b1[]=new byte[fis1.available()];
            fis1.read(b1);
            s1=new String(b1);

            fis1.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }



        try
        {


            String ndata=s1+"\n"+data;
            File fsrc =new File(FileDir+"/Velocity.txt");
            FileOutputStream fosrc =new FileOutputStream(fsrc);
            fosrc.write(ndata.getBytes());
            fosrc.close();

            //  Toast.makeText(getApplicationContext(), "Saved Successfully velocity ", Toast.LENGTH_SHORT).show();


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }





    }
    @Override
    public void onLongPress(MotionEvent arg0) {

        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {

        // TODO Auto-generated method stub

        return false;
    }

    @Override
    public void onShowPress(MotionEvent arg0) {

        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {

        // TODO Auto-generated method stub

        return false;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent motionEvent) {
//
//        // TODO Auto-generated method stub
//
//        return gestureDetector.onTouchEvent(motionEvent);
//    }

    @Override
    public boolean onDown(MotionEvent arg0) {

        // TODO Auto-generated method stub

        return false;
    }

}
