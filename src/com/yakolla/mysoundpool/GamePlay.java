package com.yakolla.mysoundpool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GamePlay extends SurfaceView implements SurfaceHolder.Callback {

	

	boolean surfaceReady;
	private Bitmap bufferBitmap;
	private Canvas bufferCanvas;
	private Paint    paint;
	
	public GamePlay(Context context) {
		super(context);
		SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        
		createBufferBitmap(100, 100);
	}

	public void createBufferBitmap(int width, int height)
	{
		
        
		bufferBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		bufferCanvas = new Canvas(bufferBitmap);
		
		paint = new Paint();
	    paint.setTextSize(12.0f);
	    Typeface typeface = Typeface.create(paint.getTypeface(), Typeface.NORMAL);
	    paint.setTypeface(typeface);
	    paint.setColor(Color.BLACK);
	    
	    
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
        canvas.translate(0, 0);
        canvas.drawBitmap(bufferBitmap, 0, 0, paint);
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
