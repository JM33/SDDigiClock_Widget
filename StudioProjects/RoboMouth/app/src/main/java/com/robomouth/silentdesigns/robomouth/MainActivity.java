package com.robomouth.silentdesigns.robomouth;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends Activity {

    public static MouthView mMouthView;
    private static SoundManager mSoundManager;

    protected long lastUpdateTime = 0;
    protected static int updateCount = 0;
    double currentAmplitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 0.8F;
        getWindow().setAttributes(layout);

        mMouthView = new MouthView(this);

        setContentView(mMouthView);

        mSoundManager = new SoundManager();
        mSoundManager.start();

    }

    @Override
    protected void onPause(){
        super.onPause();
        mSoundManager.stop();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mSoundManager.start();
    }

//----------------



    public class SoundManager {

        private MediaRecorder mRecorder = null;

        public void start() {
            if (mRecorder == null) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile("/dev/null");
                try {
                    mRecorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mRecorder.start();
            }
        }

        public void stop() {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
        }

        public double getAmplitude() {
            if (mRecorder != null)
                return  mRecorder.getMaxAmplitude();
            else
                return 0;

        }
    }


//----------------


    private static class MouthView extends View {
        public static int mouthcolor = Color.argb(255, 0, 0, 0);
        public static int lipscolor = Color.argb(1, 255, 255, 255);

        public static int mouthMode;
        protected int[] modes;
        public static final int MODE_SILENT = -1000;
        public static final int MODE_TALK = 1000;
        public static final int MODE_WOW = 2000;
        public static final int MODE_OMG = 3000;

        public static final int MODE_SMILE = 4000;
        public static final int MODE_FROWN = 5000;
        public static final int MODE_HALLOWEEN = 6000;

        public static int mouthWidth = 500;
        public static int mouthHeight = 10;
        double lastAmplitude = 0;
        double currentAmplitude = 0;
        // CONSTRUCTOR
        public MouthView(Context context) {
            super(context);
            setFocusable(true);

            mouthMode = MODE_SILENT;

        }

        @Override
        protected void onDraw(Canvas canvas) {
            int centerX= (canvas.getWidth())/2;//for horisontal position
            int centerY=(canvas.getHeight())/2;//for vertical position

            currentAmplitude = mSoundManager.getAmplitude();
            Log.d("MainActivity", "CurrentAmplitude = " + currentAmplitude);
            //Log.d("MainActivity", "updateCount = " + updateCount);

            canvas.drawColor(mouthcolor);
            Paint p = new Paint();
            // smooths
            p.setAntiAlias(true);
            p.setColor(Color.RED);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(50);

            Paint textPaint = new Paint();
            int xPos, yPos;
            for (int i = 0; i < canvas.getWidth(); i++) {
                p.setStrokeWidth(5);
                canvas.drawPoint(i, centerY - (int) (Math.cos(((i * 10f) * updateCount * 0.01f)) * 10000), p);
            }


            if(mouthMode == MODE_TALK || mouthMode == MODE_SILENT) {
                if (currentAmplitude > 1500) {
                    mouthMode = MODE_TALK;
                } else {
                    mouthMode = MODE_SILENT;
                }
            }

            //mouthMode = MODE_TALK;

            int count = 0;
            int color = 0;
            int red,grn, blu = 0;

            count = updateCount;
            while(count >255) {
                count -= 255;
            }
            red = count;
            if(count+125 > 255){
                grn = (count+125)-255;
            }else{
                grn =count+125;
            }
            if(count+250 > 255){
                blu = (count + 250)-255;
            }else{
                blu =count+250;
            }
            switch(mouthMode){
                case MODE_SILENT:
                    // opacity
                    //p.setAlpha(0x80); //
                    p.setStrokeWidth(5);
                    count = updateCount;
                    while(count >255) {
                        count -= 255;
                    }
                    //color = (int)(Math.cos(count));
                    //p.setColor(Color.rgb(color, color+100, color+155));
                    p.setColor(Color.rgb(red, grn, blu));
                    //p.setColor(Color.);
                     for (int i = 0; i < canvas.getWidth(); i++){

                         canvas.drawPoint(i, centerY - (int)(Math.cos(( (i) * updateCount * 10f))  *50), p);
                     }
                    //canvas.drawCircle(centerX, centerY, mouthWidth, p);
                    break;

                case MODE_TALK:
                    // opacity
                   p.setAlpha(180); //
                   p.setStrokeWidth(3);


                    //color = (int)(Math.cos(count));
                    //p.setColor(Color.rgb(color, color+100, color+155));
                    p.setColor(Color.rgb(red, grn, blu));
                    if(currentAmplitude > 30000){
                        p.setStrokeWidth(45);
                        canvas.drawCircle(centerX, centerY, 450, p);
                    }else {
                        if (currentAmplitude > 20000) {
                            p.setStrokeWidth(40);
                            canvas.drawCircle(centerX, centerY, 425, p);
                        } else {
                            if (currentAmplitude > 13000) {
                                p.setStrokeWidth(30);
                                canvas.drawCircle(centerX, centerY, 400, p);
                            } else {
                                if (currentAmplitude > 7500) {
                                    p.setStrokeWidth(25);
                                    canvas.drawCircle(centerX, centerY, 375, p);
                                } else {
                                    if (currentAmplitude > 5000) {
                                        p.setStrokeWidth(20);
                                        canvas.drawCircle(centerX, centerY, 350, p);
                                    } else {
                                        if (currentAmplitude > 3000) {
                                            p.setStrokeWidth(10);
                                            canvas.drawCircle(centerX, centerY, 300, p);
                                        } else {
                                            if (currentAmplitude > 1500) {
                                                p.setStrokeWidth(5);
                                                canvas.drawCircle(centerX, centerY, 250, p);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }


                    Log.d("MainActivity", "Amplitude = " + currentAmplitude);

                    //p.setStrokeWidth(25);
                    //canvas.drawCircle(centerX, centerY, mouthWidth, p);

                    lastAmplitude = currentAmplitude;
                    currentAmplitude -= 50f;

                    break;

                case MODE_WOW:
                    // opacity
                    //p.setAlpha(0x80); //
                    textPaint = new Paint();
                    textPaint.setColor(Color.rgb(red, grn, blu));
                    textPaint.setTextAlign(Paint.Align.CENTER);
                    //textPaint.setTypeface();
                    textPaint.setTextSize(400 + (int)(Math.cos(updateCount *.1f) * 100));
                    xPos = (canvas.getWidth() / 2);
                    yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;
                    canvas.drawText("WOW", xPos, yPos, textPaint);
                    break;

                case MODE_OMG:
                    // opacity
                    //p.setAlpha(0x80); //
                    textPaint = new Paint();
                    textPaint.setColor(Color.rgb(red, grn, blu));
                    textPaint.setTextAlign(Paint.Align.CENTER);
                    //textPaint.setTypeface();
                    textPaint.setTextSize(300);
                    xPos = (canvas.getWidth() / 2);
                    yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;
                    canvas.drawText("OMG", xPos, yPos, textPaint);
                    break;
            }

            updateCount++;
            this.invalidate();
        }

        public boolean onKeyDown(int keyCode, KeyEvent event) {
            //Debug: Log.d("JOY", event.toString());
            if(event.getAction() == KeyEvent.ACTION_DOWN) {
                if(keyCode == KeyEvent.KEYCODE_BUTTON_A) {
                    mouthMode = MouthView.MODE_TALK;
                } else if(keyCode == KeyEvent.KEYCODE_BUTTON_B) {
                    mouthMode = MouthView.MODE_SILENT;
                }
                else if(keyCode == KeyEvent.KEYCODE_BUTTON_X) {
                    mouthMode = MouthView.MODE_WOW;
                }
                else if(keyCode == KeyEvent.KEYCODE_BUTTON_Y) {
                    mouthMode = MouthView.MODE_OMG;
                }
                // Trigger a redraw.
                invalidate();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }


        public boolean onKeyUp(int keyCode, KeyEvent event) {
            //Debug: Log.d("JOY", event.toString());
            if(event.getAction() == KeyEvent.ACTION_UP) {
                if(keyCode == KeyEvent.KEYCODE_BUTTON_A) {
                    mouthMode = MouthView.MODE_SILENT;
                } else if(keyCode == KeyEvent.KEYCODE_BUTTON_B) {
                    mouthMode = MouthView.MODE_SILENT;
                }
                else if(keyCode == KeyEvent.KEYCODE_BUTTON_X) {
                    mouthMode = MouthView.MODE_SILENT;
                }
                else if(keyCode == KeyEvent.KEYCODE_BUTTON_Y) {
                    mouthMode = MouthView.MODE_SILENT;
                }
                // Trigger a redraw.
                invalidate();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
    }
}
