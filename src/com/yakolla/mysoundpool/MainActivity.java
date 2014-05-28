package com.yakolla.mysoundpool;

import java.io.File;
import java.io.IOException;

import com.yakolla.mysoundpool.R;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
	
	private SoundPool sPool; // 사운드 풀
	private AudioManager mAudioManager;
	private int	loadedCount = 0;
	public static final String TAG = "MYSOUNDPOOL";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		setContentView(R.layout.main);	
		
		mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
		
		sPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		sPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
		    public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
		    	++loadedCount;
		    	Log.d(TAG, "sampleid: " + sampleId);
		    	
		    	if (loadedCount == 2)
		    	{
		    		float volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			    	soundPool.play(1, volume, volume, 0, 0, 1);
			    	soundPool.play(2, volume, volume, 0, 0, 1);			    		
		    	}
		    	
		    }
		});

		try {
			
			sPool.load(getResources().getAssets().openFd("Bach__Prelude_in_C_major.mid"), 1);
			sPool.load(getResources().getAssets().openFd("katy_perry-dark_horse.mid"), 1);	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
