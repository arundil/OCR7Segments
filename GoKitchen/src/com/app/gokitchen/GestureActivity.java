package com.app.gokitchen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.OverScroller;

public class GestureActivity extends Activity implements  OnGestureListener,OnDoubleTapListener {

	private static final String DEBUG_TAG = "GoKitchen::GestureActivity";
	private GestureDetectorCompat mDetector;
	//private RectF mCurrentViewport = new RectF(AXIS_X_MIN, AXIS_Y_MIN, AXIS_X_MAX, AXIS_Y_MAX);



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture);
		mDetector = new GestureDetectorCompat(this,this);
		mDetector.setOnDoubleTapListener(this);	
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
