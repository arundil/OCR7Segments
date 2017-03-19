package com.app.gokitchen;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;


public class SplashActivity extends Activity implements TextToSpeech.OnInitListener {
	
	// Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 1000;
	private TextToSpeech textToSpeech;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
 
        setContentView(R.layout.activity_splash);
        
		textToSpeech = new TextToSpeech( this, (OnInitListener) this );
		textToSpeech.setLanguage( new Locale( "spa", "ESP" ) );
        
 
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
 
        speak("Hola, Iniciando la aplicación. Espere por favor");
    }
    
    private void speak( String str )
	{
		textToSpeech.speak( str, TextToSpeech.QUEUE_FLUSH, null );
		textToSpeech.setSpeechRate( 0.0f );
		textToSpeech.setPitch( 0.0f );
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

		if ( textToSpeech != null )
		{
			textToSpeech.stop();
			textToSpeech.shutdown();
		}
		super.onDestroy();
	}

}
