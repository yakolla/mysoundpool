package com.yakolla.mysoundpool;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GamePlayView extends SurfaceView implements SurfaceHolder.Callback {

	

	
	boolean surfaceReady;
	Bitmap bufferBitmap;
	Canvas bufferCanvas;
	Paint    paint;
	ArrayList<MidiEvent> mevents;
	int		scrollX;
	int		scrollY;
	int		noteNumberMin;
	int		noteNumberMax;
	
	public GamePlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        
		
	}
	
	public GamePlayView(Context context) {
		super(context);
		SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        
		
	}


	public void init(int width, int height, ArrayList<MidiEvent> mevents)
	{		
        
		bufferBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		bufferCanvas = new Canvas(bufferBitmap);
		
		paint = new Paint();
	    paint.setTextSize(12.0f);
	    Typeface typeface = Typeface.create(paint.getTypeface(), Typeface.NORMAL);
	    paint.setTypeface(typeface);
	    paint.setColor(Color.WHITE);
	    
	    this.mevents = mevents;
	    
	    scrollX = 0;
	    scrollY = 0;
	    
	    noteNumberMin = 1000;
	    noteNumberMax = -1000;
	    for (MidiEvent ev : mevents)
    	{
	    	if (ev.EventFlag != MidiFile.EventNoteOn)
	    		continue;
	    	
    		if (noteNumberMin > ev.Notenumber)
    		{
    			noteNumberMin = ev.Notenumber;
    		}
    		
    		if (noteNumberMax < ev.Notenumber)
    		{
    			noteNumberMax = ev.Notenumber;
    		}    		
    	}
	}
	
	public void updateScroll(int xdelta, int ydelta)
	{
		scrollX = xdelta;
		scrollY = ydelta;
	}
	
	/** Obtain the drawing canvas and call onDraw() */
    public void callOnDraw() {
        if (!surfaceReady) {
            return;
        }
        SurfaceHolder holder = getHolder();
        Canvas canvas = holder.lockCanvas();
        if (canvas == null) {
            return;
        }
        onDraw(canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    /** Draw the SheetMusic. */
    @Override
    protected void onDraw(Canvas canvas) {        

        // We want (scrollX - bufferX, scrollY - bufferY) 
        // to be (0,0) on the canvas
    	canvas.translate(scrollX, scrollY);
    	canvas.drawColor(Color.BLACK);
        //canvas.drawBitmap(bufferBitmap, scrollX, scrollX, paint);
        
    	for (int i = 0; i < mevents.size(); ++i)
    	{
    		MidiEvent evi = mevents.get(i);
    		if (evi.EventFlag != MidiFile.EventNoteOn)
    			continue;
    		
    		for (int j = i+1; j < mevents.size(); ++j)
    		{
    			MidiEvent evj = mevents.get(j);
    			int height = 0;
    			if (evj.EventFlag == MidiFile.EventNoteOn && evi.Notenumber == evj.Notenumber)
            	{     
    				height = (evj.StartTime-evi.StartTime);
    				i = j;
            	}
    			else if (evj.EventFlag == MidiFile.EventNoteOff && evi.Notenumber == evj.Notenumber)
    			{
    				height = (evj.StartTime-evi.StartTime);
    			}
    			else
    			{
    				continue;
    			}
    			
    			int noteNumber = (int)evi.Notenumber; 
    			float x = (float)(noteNumber-noteNumberMin)/(noteNumberMax-noteNumberMin)*100.0f;
        		canvas.drawRect(x,  evi.StartTime,  x+5,  evj.StartTime, paint);
        		break;
    		}
    	}
        
    }
    
	public void
    surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        callOnDraw();
    }

    /** Surface is ready for shading the notes */
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceReady = true;
    }

    /** Surface has been destroyed */
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceReady = false;
    }

}
