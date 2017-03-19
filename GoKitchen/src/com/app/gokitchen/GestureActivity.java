package com.app.gokitchen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class GestureActivity extends Activity implements  OnGestureListener,OnDoubleTapListener {

	private static final String DEBUG_TAG = "GoKitchen::GestureActivity";
	private GestureDetectorCompat mDetector;
	DrawingView dv ;
	private Paint mPaint; 
	//private RectF mCurrentViewport = new RectF(AXIS_X_MIN, AXIS_Y_MIN, AXIS_X_MAX, AXIS_Y_MAX);



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_gesture);
        dv = new DrawingView(this);
        setContentView(dv);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
		/*mDetector = new GestureDetectorCompat(this,this);
		mDetector.setOnDoubleTapListener(this);	*/
	}
	
	 public class DrawingView extends View {

	        public int width;
	        public  int height;
	        private Bitmap  mBitmap;
	        private Canvas  mCanvas;
	        private Path    mPath;
	        private Paint   mBitmapPaint;
	        Context context;
	        private Paint circlePaint;
	        private Path circlePath;

	        public DrawingView(Context c) {
	            super(c);
	            context=c;
	            mPath = new Path();
	            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
	            circlePaint = new Paint();
	            circlePath = new Path();
	            circlePaint.setAntiAlias(true);
	            circlePaint.setColor(Color.BLUE);
	            circlePaint.setStyle(Paint.Style.STROKE);
	            circlePaint.setStrokeJoin(Paint.Join.MITER);
	            circlePaint.setStrokeWidth(4f);
	        }

	        @Override
	        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	            super.onSizeChanged(w, h, oldw, oldh);

	            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
	            mCanvas = new Canvas(mBitmap);
	        }

	        @Override
	        protected void onDraw(Canvas canvas) {
	            super.onDraw(canvas);

	            canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
	            canvas.drawPath( mPath,  mPaint);
	            canvas.drawPath( circlePath,  circlePaint);
	        }

	        private float mX, mY;
	        private static final float TOUCH_TOLERANCE = 4;

	        private void touch_start(float x, float y) {
	            mPath.reset();
	            mPath.moveTo(x, y);
	            mX = x;
	            mY = y;
	        }

	        private void touch_move(float x, float y) {
	            float dx = Math.abs(x - mX);
	            float dy = Math.abs(y - mY);
	            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
	                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
	                mX = x;
	                mY = y;

	                circlePath.reset();
	                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
	            }
	        }

	        private void touch_up() {
	            mPath.lineTo(mX, mY);
	            circlePath.reset();
	            // commit the path to our offscreen
	            mCanvas.drawPath(mPath,  mPaint);
	            // kill this so we don't double draw
	            mPath.reset();
	        }

	        @Override
	        public boolean onTouchEvent(MotionEvent event) {
	            float x = event.getX();
	            float y = event.getY();

	            switch (event.getAction()) {
	                case MotionEvent.ACTION_DOWN:
	                    touch_start(x, y);
	                    invalidate();
	                    
	            		try{
	            			Intent intent = new Intent(GestureActivity.this, OCRActivity.class);
	            			startActivity(intent);
	            		}catch (Exception e1){}
	                    
	                    break;
	                case MotionEvent.ACTION_MOVE:
	                    touch_move(x, y);
	                    invalidate();
	                    break;
	                case MotionEvent.ACTION_UP:
	                    touch_up();
	                    invalidate();
	                    break;
	            }
	            return true;
	        }
	    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gesture, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_buttons) {
			//Aqui se debe programar el menu
			try{
                Intent intent = new Intent(GestureActivity.this, MainActivity.class);
                startActivity(intent);
            }catch (Exception e){}
			return true;
		}
		if (id == R.id.action_gesture) {
			//Aqui se debe programar el otro menú
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }


	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + e.toString());
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Log.d(DEBUG_TAG, "onDoubleTap: " + e.toString());
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		Log.d(DEBUG_TAG, "onDoubleTapEvent: " + e.toString());
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		Log.d(DEBUG_TAG,"onDown: " + e.toString());
		try{
			Intent intent = new Intent(GestureActivity.this, OCRActivity.class);
			startActivity(intent);
		}catch (Exception e1){}
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		Log.d(DEBUG_TAG, "onShowPress: " + e.toString());		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Log.d(DEBUG_TAG, "onSingleTapUp: " + e.toString());
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		Log.d(DEBUG_TAG, "onScroll: " + e1.toString()+e2.toString());
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		Log.d(DEBUG_TAG, "onLongPress: " + e.toString());		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(DEBUG_TAG, "onFling: " + e1.toString()+e2.toString());
        return true;
	}
}
