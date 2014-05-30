package com.yakolla.mysoundpool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.yakolla.mysoundpool.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private MediaPlayer midi;
	private SoundPool sPool; // 사운드 풀
	private AudioManager mAudioManager;
	private int	loadedCount = 0;
	public static final String TAG = "MYSOUNDPOOL";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		setContentView(R.layout.main);	
		
		midi = new MediaPlayer();
		mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
		
		sPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		sPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
		    public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
		    	++loadedCount;
		    	Log.d(TAG, "sampleid: " + sampleId);
		    	
		    	float volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			    soundPool.play(sampleId, volume, volume, 0, 0, 1);
			    
		    	
		    }
		});
		
		loadAssetMidiFile("Bach__Prelude_in_C_major.mid");
		
	}
	
	void loadAssetMidiFile(String fileName) {
		
        Uri uri = Uri.parse("file:///android_asset/" + fileName);
        FileUri file = new FileUri(uri, fileName);
        byte[] data;
        try {
            data = file.getData(this);
            MidiFile midifile = new MidiFile(data, uri.getLastPathSegment());
            
            FileOutputStream destOutput = openFileOutput("temp.mid", Context.MODE_PRIVATE);
            midifile.Write2(destOutput, null);
            destOutput.close();
            
            FileInputStream input = openFileInput("temp.mid");
            
            midi.setDataSource(input.getFD());            
            //sPool.load(input.getFD(), 0, 0, 1);	
            
            input.close();
            
            midi.prepare();
            midi.start();
             
            
        }
        catch (IOException e) {
            this.finish();
            return;
        }
    }
}
