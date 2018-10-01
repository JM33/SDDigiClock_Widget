package com.robomouth.silentdesigns.robomouth;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
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
        setVolumeControlStream(AudioManager.MODE_IN_COMMUNICATION);
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
        private MediaPlayer   mPlayer = null;
        private String mFileName = "";
        public boolean mStartPlaying = false;

        boolean isRecording = false;
        AudioManager audioManager;
        AudioRecord record = null;
        AudioTrack track = null;

        public void onCreate(){

        }

        public void start() {
            // Record to the external cache directory for visibility
            mFileName = getExternalCacheDir().getAbsolutePath();
            mFileName += "/audiorecordtest.3gp";

            if (mRecorder == null) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile(mFileName);
                try {
                    mRecorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mRecorder.start();
            }

            audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            audioManager.setSpeakerphoneOn(true);


            /*
            initRecordAndTrack();

            (new Thread()
            {
                @Override
                public void run()
                {
                    recordAndPlay();
                }
            }).start();
            startRecordAndPlay();
            */

            startPlaying();
        }

        private void initRecordAndTrack()
        {
            int min = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            record = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    min);
            if (AcousticEchoCanceler.isAvailable())
            {
                AcousticEchoCanceler echoCancler = AcousticEchoCanceler.create(record.getAudioSessionId());
                echoCancler.setEnabled(true);
            }
            int maxJitter = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            track = new AudioTrack(AudioManager.MODE_IN_COMMUNICATION, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, maxJitter,
                    AudioTrack.MODE_STREAM);
        }

        private void recordAndPlay()
        {
            short[] lin = new short[1024];
            int num = 0;
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            while (true)
            {
                if (isRecording)
                {
                    //Log.d("SoundManager", "recordAndPlay");
                    num = record.read(lin, 0, 1024);
                    track.write(lin, 0, num);
                }
            }
        }

        private void startRecordAndPlay()
        {
            record.startRecording();
            track.play();
            isRecording = true;
        }

        private void stopRecordAndPlay()
        {
            record.stop();
            track.pause();
            isRecording = false;
        }

        public void stop() {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }

            if (mPlayer != null) {
                mPlayer.release();
                mPlayer = null;
            }
        }

        public double getAmplitude() {
            if (mRecorder != null)
                return  mRecorder.getMaxAmplitude();
            else
                return 0;

        }

        private void onPlay(boolean start) {
            if (start) {
                startPlaying();
            } else {
                stopPlaying();
            }
        }

        private void startPlaying() {
            mPlayer = new MediaPlayer();
            try {
                //audioManager.setSpeakerphoneOn(true);
                //mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setDataSource(mFileName);
                //PlaybackParams playbackParams  = new PlaybackParams();
                //mPlayer.set
                mPlayer.prepare();
                mPlayer.start();
                Log.e("SoundManager", "mediaplayer started");
            } catch (IOException e) {
                Log.e("SoundManager", "mediaplayer prepare() failed");
            }
        }

        private void stopPlaying() {
            mPlayer.release();
            mPlayer = null;
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
        public static final int MODE_SPOOKY = 3000;

        public static final int MODE_SMILE = 4000;
        public static final int MODE_FROWN = 5000;
        public static final int MODE_HALLOWEEN = 6000;

        public static float mouthWidth = 500;
        public static int mouthHeight = 10;
        double lastAmplitude = 0;
        double currentAmplitude = 0;
        long mouthCloseTime = -1;
        Typeface tf;

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
            //Log.d("MainActivity", "CurrentAmplitude = " + currentAmplitude);
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
                }
            }

            //mouthMode = MODE_SPOOKY;

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
            red = 255;
            grn = 120;
            blu = 0;
            switch(mouthMode){
                case MODE_SILENT:
                    // opacity
                    //p.setAlpha(0x80); //
                    p.setStrokeWidth(20);
                    count = updateCount;
                    while(count >255) {
                        count -= 255;
                    }
                    //color = (int)(Math.cos(count));
                    //p.setColor(Color.rgb(color, color+100, color+155));
                    p.setColor(Color.rgb(red, grn, blu));
                    //p.setColor(Color.);
                     for (int i = 0; i < canvas.getWidth(); i++){

                         canvas.drawPoint(i, centerY - (int)(Math.sin(((count *0.5f) -(0.005f*i)))  *50), p);
                     }
                    //canvas.drawCircle(centerX, centerY, mouthWidth, p);
                    break;

                case MODE_TALK:
                    if(!mSoundManager.mStartPlaying){
                        mSoundManager.mStartPlaying = true;
                        mSoundManager.onPlay(mSoundManager.mStartPlaying);
                    }
                    // opacity
                   p.setAlpha(50); //
                   p.setStrokeWidth(1);

                    count = updateCount;
                    while(count >255) {
                        count -= 255;
                    }
                    //color = (int)(Math.cos(count));
                    //p.setColor(Color.rgb(color, color+100, color+155));
                    p.setColor(Color.rgb(red, grn, blu));
                    //p.setColor(Color.);
                    for (int i = 0; i < canvas.getWidth(); i++){

                        canvas.drawPoint(i, centerY - (int)(Math.sin(((count *0.5f) -(0.005f*i)))  *50), p);
                    }

                    if(currentAmplitude > 30000){
                        p.setStrokeWidth(60);
                        mouthWidth = 450;
                        mouthCloseTime = -1;
                    }else {
                        if (currentAmplitude > 20000) {
                            p.setStrokeWidth(55);
                            mouthWidth = 425;
                            mouthCloseTime = -1;
                        } else {
                            if (currentAmplitude > 13000) {
                                p.setStrokeWidth(50);
                                mouthWidth = 400;
                                mouthCloseTime = -1;
                            } else {
                                if (currentAmplitude > 7500) {
                                    p.setStrokeWidth(45);
                                    mouthWidth = 375;
                                    mouthCloseTime = -1;
                                } else {
                                    if (currentAmplitude > 5000) {
                                        p.setStrokeWidth(40);
                                        mouthWidth = 350;
                                        mouthCloseTime = -1;
                                    } else {
                                        if (currentAmplitude > 3000) {
                                            p.setStrokeWidth(35);
                                            mouthWidth = 300;
                                            mouthCloseTime = -1;
                                        } else {
                                            if (currentAmplitude > 1500) {
                                                p.setStrokeWidth(25);
                                                mouthWidth = 250;
                                                mouthCloseTime = -1;
                                            }
                                            else{
                                                if(mouthCloseTime == -1){
                                                    mouthCloseTime = System.currentTimeMillis();
                                                }
                                                mouthWidth = Lerp.lerp(mouthWidth, 0, 250, System.currentTimeMillis() - mouthCloseTime);
                                                p.setStrokeWidth(15);
                                                if(mouthWidth < 10){
                                                    mouthWidth = 0;
                                                    mouthCloseTime = -1;
                                                    mouthMode = MODE_SILENT;
                                                }
                                                Log.d("MainActivity", "Mouth Width = " + mouthWidth);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    RectF mouthRect = new RectF(centerX - mouthWidth * 1.75f, centerY - mouthWidth, centerX + mouthWidth  *1.75f , centerY + mouthWidth);
                    //Log.d("MainActivity", "Mouth Width = " + mouthWidth);

                    canvas.drawArc(mouthRect, 0, 360, false, p);
                    //canvas.drawCircle(centerX, centerY, mouthWidth, p);
                    //Log.d("MainActivity", "Amplitude = " + currentAmplitude);

                    //p.setStrokeWidth(25);


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

                case MODE_SPOOKY:
                    // opacity
                    //p.setAlpha(0x80); //
                    textPaint = new Paint();
                    textPaint.setColor(Color.rgb(red, grn, blu));
                    textPaint.setTextAlign(Paint.Align.CENTER);
                    tf = Typeface.createFromAsset(this.getContext().getAssets(), "fonts/ShockShimmy.ttf");
                    textPaint.setTypeface(tf);
                    textPaint.setTextSize(400);
                    count = updateCount*25;
                    while(count > canvas.getWidth()*2){
                        count -= canvas.getWidth() * 2;
                    }
                    xPos = (int) (count - (canvas.getWidth()*0.5f));
                    yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2) ) ;
                    canvas.drawText("SPOOKY", xPos, yPos, textPaint);

                    tf = Typeface.createFromAsset(this.getContext().getAssets(), "fonts/SILBT.TTF");
                    textPaint.setTypeface(tf);
                    textPaint.setTextSize(400);
                    count = updateCount*25;
                    while(count > canvas.getWidth()*2){
                        count -= canvas.getWidth() * 2;
                    }
                    xPos = (int) (canvas.getWidth() -  (count - (canvas.getWidth()*0.5f)));
                    yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2) -350) ;
                    canvas.drawText("A A A", xPos, yPos, textPaint);
                    canvas.drawText("D   E", canvas.getWidth()*0.5f, yPos +700, textPaint);
                    break;

                case MODE_HALLOWEEN:
                    // opacity
                    //p.setAlpha(0x80); //
                    textPaint = new Paint();
                    textPaint.setColor(Color.rgb(red, grn, blu));
                    textPaint.setTextAlign(Paint.Align.CENTER);
                    tf = Typeface.createFromAsset(this.getContext().getAssets(), "fonts/NamelessHarbor.ttf");
                    textPaint.setTypeface(tf);
                    textPaint.setTextSize(250 + (int)(Math.cos(updateCount *.1f) * 50));
                    xPos = (canvas.getWidth() / 2);
                    yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;
                    canvas.drawText("HAPPY", xPos, yPos + textPaint.ascent(), textPaint);
                    canvas.drawText("HALLOWEEN", xPos, yPos - textPaint.ascent(), textPaint);
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
                    mouthMode = MouthView.MODE_HALLOWEEN;
                }
                else if(keyCode == KeyEvent.KEYCODE_BUTTON_Y) {
                    mouthMode = MouthView.MODE_SPOOKY;
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

    /////////////////////////////////////////-----------------------------------------------------------------

    /*
 * Thread to manage live recording/playback of voice input from the device's microphone.
 */
    private class Audio extends Thread
    {
        private boolean stopped = false;

        /**
         * Give the thread high priority so that it's not canceled unexpectedly, and start it
         */
        private Audio()
        {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            start();
        }

        @Override
        public void run()
        {
            Log.i("Audio", "Running Audio Thread");
            AudioRecord recorder = null;
            AudioTrack track = null;
            short[][]   buffers  = new short[256][160];
            int ix = 0;

        /*
         * Initialize buffer to hold continuously recorded audio data, start recording, and start
         * playback.
         */
            try
            {
                int N = AudioRecord.getMinBufferSize(8000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
                recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, N*10);
                track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, N*10, AudioTrack.MODE_STREAM);
                recorder.startRecording();
                track.play();
            /*
             * Loops until something outside of this thread stops it.
             * Reads the data from the recorder and writes it to the audio track for playback.
             */
                while(!stopped)
                {
                    Log.i("Map", "Writing new data to buffer");
                    short[] buffer = buffers[ix++ % buffers.length];
                    N = recorder.read(buffer,0,buffer.length);
                    track.write(buffer, 0, buffer.length);
                }
            }
            catch(Throwable x)
            {
                Log.w("Audio", "Error reading voice audio", x);
            }
        /*
         * Frees the thread's resources after the loop completes so that it can be run again
         */
            finally
            {
                recorder.stop();
                recorder.release();
                track.stop();
                track.release();
            }
        }

        /**
         * Called from outside of the thread in order to stop the recording/playback loop
         */
        private void close()
        {
            stopped = true;
        }

    }
}
