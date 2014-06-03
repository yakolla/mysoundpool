package com.yakolla.mysoundpool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.yakolla.mysoundpool.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity  {
	
	private MediaPlayer mediaPlayer;
	private SoundPool sPool; // 사운드 풀
	private AudioManager mAudioManager;
	private int	loadedCount = 0;
	private EditText txtEdit;
	private TextView txtView;
	private GamePlayView gamePlay;
	
	public static final String TAG = "MYSOUNDPOOL";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		setContentView(R.layout.main);	
		
		LinearLayout layout = (LinearLayout)findViewById(R.id.layout1);
		gamePlay = (GamePlayView)findViewById(R.id.gamePlay);
		
		
		
		
		txtEdit = (EditText)findViewById(R.id.editText1);
		txtView = (TextView)findViewById(R.id.textView1);
		mediaPlayer = new MediaPlayer();
		mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
		
		sPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		sPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
		    public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
		    	++loadedCount;
		    	Log.d(TAG, "sampleid: " + sampleId);
		    	
		    	
		    	
		    }
		});
		
		loadAssetMidiFile("steel_heart-shes_gone.mid");
		layout.requestLayout();
		gamePlay.callOnDraw();
	}
	
	
	
	public void clickMethod(View v)
	{
		float volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		sPool.play(Integer.parseInt(txtEdit.getText().toString()), volume, volume, 0, 0, 1);
		
		String id = Integer.toString(Integer.parseInt(txtEdit.getText().toString())+1);
		txtEdit.setText(id);
	    
	}
	
	public void onClickPlayBackground(View v)
	{
		mediaPlayer.start();

		startTimeForBackgroundMusic = SystemClock.uptimeMillis();
		startTime = SystemClock.uptimeMillis();
		thread.start();
	}
	void loadAssetMidiFile(String fileName) {
		
        Uri uri = Uri.parse("file:///android_asset/" + fileName);
        FileUri file = new FileUri(uri, fileName);
        byte[] data;
        int playtrack = 10;
        try {
            data = file.getData(this);
            MidiFile midifile = new MidiFile(data, uri.getLastPathSegment());
            
            MidiFileWriteResult midifileWriteResult = midifile.Write2(null, this, playtrack);
            
            FileInputStream input = openFileInput("temp.mid");            
            
            int offset = 0;
            for (Integer wroteLen : midifileWriteResult.wroteLenList)
            {
            	//sPool.load(input.getFD(), offset, wroteLen, 1);	
            		
            	offset += wroteLen;
            }
            
            input.close();
            
            
            MidiOptions midiOption = new MidiOptions(midifile);
            midiOption.mute[playtrack] = true; 
            midifile.Write(openFileOutput("background.mid", Context.MODE_PRIVATE ), midiOption);
            
            FileInputStream background = openFileInput("background.mid");
            mediaPlayer.setDataSource(background.getFD());
            //AssetFileDescriptor descriptor = descriptor = getAssets().openFd(fileName);
            //mediaPlayer.setDataSource( descriptor.getFileDescriptor(), descriptor.getStartOffset(),  descriptor.getLength() );
            
            background.close();
            
            mediaPlayer.prepare();
            
            
            pulsesPerMsec = midifile.getTime().getQuarter() * (1000.0 / midiOption.tempo);
            finishPulseTime = midifile.getTotalPulses();
            
            gamePlay.init(100, (int)finishPulseTime+10, midifileWriteResult.mevents);
            
        }
        catch (IOException e) {
            this.finish();
            return;
        }
    }
	
	
	long startTimeForBackgroundMusic;             /** Absolute time when music started playing (msec) */
    double currentPulseTime;    /** Time (in pulses) music is currently at */
    double finishPulseTime;    /** Time (in pulses) music is currently at */
    double pulsesPerMsec;
    long startTime;
    Handler timer = new Handler();
    
	Runnable TimerCallback = new Runnable() {
	      public void run() {
	        
	    	  	
	    	  	while(true)
	    	  	{
	    	  		long ctime = SystemClock.uptimeMillis();
		            long msec = ctime - startTimeForBackgroundMusic;
		    	  	long interval = ctime-startTime;
	    	  		currentPulseTime = msec * pulsesPerMsec;
		            
		            if (currentPulseTime > finishPulseTime) {
		                break;
		            }
		            
		            if (interval < 30)
		            {
		            	try {
							Thread.sleep(30-interval);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            	continue;
		            }
		            
		            //txtView.setText(""+currentPulseTime);
		            
		            gamePlay.updateScroll(0, -(int)currentPulseTime);
		            gamePlay.callOnDraw();	            
		            startTime = ctime;	
	    	  	}
	            
	            
	      }
	    };
	Thread thread = new Thread(TimerCallback);
}
