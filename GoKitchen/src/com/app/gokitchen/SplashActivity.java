package com.app.gokitchen;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Window;
import android.widget.Toast;


public class SplashActivity extends Activity implements TextToSpeech.OnInitListener {
	
	// Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 1000;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
 
        setContentView(R.layout.activity_splash);
         
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
 
                // Start the next activity
                Intent mainIntent = new Intent().setClass(
                        SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
 
                // Close the activity so the user won't able to go back this
                // activity pressing Back button
                finish();
            }
        };
        
        
        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
 
    }
    

	@Override
	public void onInit(int status) {
		
		if ( status == TextToSpeech.LANG_MISSING_DATA | status == TextToSpeech.LANG_NOT_SUPPORTED )
		{
			Toast.makeText( this, "ERROR LANG_MISSING_DATA | LANG_NOT_SUPPORTED", Toast.LENGTH_SHORT ).show();
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
