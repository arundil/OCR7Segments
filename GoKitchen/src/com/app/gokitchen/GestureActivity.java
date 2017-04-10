package com.app.gokitchen;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
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
import android.widget.Toast;

public class GestureActivity extends Activity implements OnGesturePerformedListener{
	
	GestureLibrary mLibrary;
	
	private static final String DEBUG_TAG = "GoKitchen::GestureActivity";
	
	//DrawingView dv ;
	private Paint mPaint; 
	//private RectF mCurrentViewport = new RectF(AXIS_X_MIN, AXIS_Y_MIN, AXIS_X_MAX, AXIS_Y_MAX);
    private GestureDetectorCompat mDetector;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture);
       
		mLibrary = GestureLibraries.fromRawResource(this, R.raw.gesture);
		   if (!mLibrary.load()) {
		     finish();
		   }
		 
		   GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
		   gestures.addOnGesturePerformedListener((OnGesturePerformedListener) this);
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
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		
		ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
		if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
			String result = predictions.get(0).name;

			if ("ocr".equalsIgnoreCase(result)) {
				Toast.makeText(this, "OCR Mode", Toast.LENGTH_LONG).show();
				Intent ocrActivity = new Intent(GestureActivity.this, OCRActivity.class);
				startActivity(ocrActivity);
				
			}else if ("connect".equalsIgnoreCase(result)) {
				Toast.makeText(this, "Conectado",Toast.LENGTH_SHORT).show();
			} else if ("on".equalsIgnoreCase(result)) {
				Toast.makeText(this, "ON", Toast.LENGTH_LONG).show();
			} else if("off".equalsIgnoreCase(result)) {
				Toast.makeText(this, "OFF", Toast.LENGTH_LONG).show();
			} else if ("subir temperatura".equalsIgnoreCase(result)) {
				Toast.makeText(this, "Subir Temperatura", Toast.LENGTH_LONG).show();
			} else if ("bajar temperatura".equalsIgnoreCase(result)) {
				Toast.makeText(this, "Bajar Temperatura",Toast.LENGTH_SHORT).show();
			}
			
			
		}
	}

}
