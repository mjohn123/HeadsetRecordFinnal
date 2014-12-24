package com.example.headsetrecorder;

import java.io.File;



import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends Activity
{
   private MediaRecorder _recorder;
  
   private AudioManager _audioManager;
   private TextView _text1, _text2;

   // stay awake
   protected PowerManager.WakeLock mWakeLock;
   int frequency = 8000;
  
   int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
   private MediaPlayer mPlayer = null;
	// Message types sent from the BluetoothRfcommClient Handler
   public static final int MESSAGE_STATE_CHANGE = 1;
   public static final int MESSAGE_READ = 2;
   public static final int MESSAGE_WRITE = 3;
   public static final int MESSAGE_DEVICE_NAME = 4;
   public static final int MESSAGE_TOAST = 5;
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
 
      _text1 = (TextView) findViewById(R.id.text_filename);
      _text2 = (TextView) findViewById(R.id.text_state);
 
      Button btn1 = (Button) findViewById(R.id.btn_start);
      btn1.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
        	 startRecord();
         }
      });

      Button btn2 = (Button) findViewById(R.id.btn_stop);
      btn2.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View v)
         {
        	 stopRecord();
         }
      }); 
      _audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
      PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
      this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag"); 
      this.mWakeLock.acquire();

   }    

  File filetotal=null;
   @Override
   protected void onDestroy()
   {
      stopRecord();
      super.onDestroy();
      //unregisterReceiver(receiver);
   }

   public void startRecord()
   {
 	  File path = new File(Environment.getExternalStorageDirectory() + "/VoiceRecord");
      if (!path.exists())
         path.mkdirs();
 
      Log.w("BluetoothReceiver.java | startRecord", "|" + path.toString() + "|");
 
      File file = null;
      try
      {
         file = File.createTempFile("voice_", ".m4a", path);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      Log.w("BluetoothReceiver.java | startRecord", "|" + file.toString() + "|");
      _text1.setText(file.toString());
 
      try
      {
         _audioManager.startBluetoothSco();
         _recorder = new MediaRecorder();
         _recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
         _recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
         _recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
         _recorder.setOutputFile(file.toString());
         _recorder.prepare();
         _recorder.start();
         _recorder.getMaxAmplitude();
         _text2.setText("recording");
         //Play during record- It did not work now 

         mPlayer = new MediaPlayer();
         mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
         _audioManager.setMode(AudioManager.STREAM_MUSIC); 
         _audioManager.setSpeakerphoneOn(false); 
         int maxVolume = _audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
         _audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_PLAY_SOUND);

        //mPlayer.setDataSource(file.toString());
         mPlayer.prepare();
         mPlayer.start();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   private void stopRecord()
   {
      try
      {
         _recorder.stop();
         _recorder.release();
         _audioManager.stopBluetoothSco();
         _text2.setText("stop");

      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
	  
   }
   public  void onScoAudioConnected()
   {
	   Log.d("D","onScoAudioConnected");
   }
  
}
