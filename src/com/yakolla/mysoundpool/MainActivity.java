package com.yakolla.mysoundpool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private MediaPlayer midi;
	private SoundPool sPool; // 사운드 풀
	private AudioManager mAudioManager;
	private int	loadedCount = 0;
	private EditText txtEdit;
	public static final String TAG = "MYSOUNDPOOL";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		setContentView(R.layout.main);	
		
		txtEdit = (EditText)findViewById(R.id.editText1);
		midi = new MediaPlayer();
		mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
		
		sPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		sPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
		    public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
		    	++loadedCount;
		    	Log.d(TAG, "sampleid: " + sampleId);
		    	
		    	
		    	
		    }
		});
		
		loadAssetMidiFile("Bach__Prelude_in_C_major.mid");
		
	}
	public void clickMethod(View v)
	{
		float volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		sPool.play(Integer.parseInt(txtEdit.getText().toString()), volume, volume, 0, 0, 1);
	    
	}
	void loadAssetMidiFile(String fileName) {
		
        Uri uri = Uri.parse("file:///android_asset/" + fileName);
        FileUri file = new FileUri(uri, fileName);
        byte[] data;
        try {
            data = file.getData(this);
            MidiFile midifile = new MidiFile(data, uri.getLastPathSegment());
            ArrayList<Integer> wroteLenList = new ArrayList<Integer>();
            
            wroteLenList = midifile.Write2(null, this);
            
            FileInputStream input = openFileInput("temp.mid");
            
            midi.setDataSource(input.getFD()); 
            
            int i = 0;
            int offset = 0;
            for (Integer wroteLen : wroteLenList)
            {
            	if (i >= 0 && i < 10) {
            		sPool.load(input.getFD(), offset, wroteLen, 1);	
            	}
            		
            	offset += wroteLen;
            	++i;
            }
            	
            
            input.close();
            
            midi.prepare();
            //midi.start();
             
            
        }
        catch (IOException e) {
            this.finish();
            return;
        }
    }
}
