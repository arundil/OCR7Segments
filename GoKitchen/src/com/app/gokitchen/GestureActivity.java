package com.app.gokitchen;

import java.util.ArrayList;
import java.util.Locale;

import com.app.gokitchen.util.GoKitchenBluetoothHandler;
import com.app.gokitchen.util.GoKitchenBluetoothHandlerImpl;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class GestureActivity extends Activity implements OnGesturePerformedListener,TextToSpeech.OnInitListener{

	GestureLibrary mLibrary;

	//private static final String TAG = "GoKitchen::GestureActivity";
	private static final int REQUEST_ENABLE_BT = 1;
	private GoKitchenBluetoothHandler BluetoothHandler = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	private Boolean connected= false;
	private Boolean on_off = false;
	private TextToSpeech textToSpeech;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture);

		mLibrary = GestureLibraries.fromRawResource(this, R.raw.gesture);
		if (!mLibrary.load()) {
			finish();
		}
		
		textToSpeech = new TextToSpeech( this, this );
		textToSpeech.setLanguage( new Locale( "spa", "ESP" ) );

		GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
		gestures.addOnGesturePerformedListener((OnGesturePerformedListener) this);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
			AlertDialog alertDialog = new AlertDialog.Builder(GestureActivity.this).create();
			alertDialog.setTitle("Oh Oh!");
			alertDialog.setMessage("Your device does not support bluetooth");
			alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			alertDialog.show();
		}
		else {
			BluetoothHandler = new GoKitchenBluetoothHandlerImpl();
		}
	}

	public void onInit( int status )
	{
		if ( status == TextToSpeech.LANG_MISSING_DATA | status == TextToSpeech.LANG_NOT_SUPPORTED )
		{
			Toast.makeText( this, "ERROR LANG_MISSING_DATA | LANG_NOT_SUPPORTED", Toast.LENGTH_SHORT ).show();
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
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {

		ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
		if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
			String result = predictions.get(0).name;

			if ("ocr".equalsIgnoreCase(result)) {
				Toast.makeText(this, "OCR Mode", Toast.LENGTH_LONG).show();
				textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
				speak(getResources().getString(R.string.audio_ORCMode));
				Intent ocrActivity = new Intent(GestureActivity.this, OCRActivity.class);
				startActivity(ocrActivity);

			}else if ("connect".equalsIgnoreCase(result)) {

				BluetoothHandler.connectBT();

				if (BluetoothHandler.checkBTState()) {
					if (BluetoothHandler.sendData("STATUS")) {
						textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
						speak("Conectado a la Vitrocerámica");
						Toast.makeText(this, "Conectado",Toast.LENGTH_SHORT).show();
						connected = true;
					}
					else {
						Toast.makeText(this, "ERROR en Protocolo",Toast.LENGTH_SHORT).show();
						connected = false;
						textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
						speak("Se ha producido un error al enviar del dato. Por favor, verífique el receptor y vuelva a conectarse.");
					}
				}
				else {
					textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
					speak("Su bluetooth está desconectado o su dispositivo no está emparejado. Por favor configúrelo y vuelva a intentarlo");
					Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
					connected = false;
				}

			}else if ("disconnect".equalsIgnoreCase(result)) {
				textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
				speak("Ok, Todo desconectado y apagado.");
				BluetoothHandler.freeConnection();
				connected = false;
				Toast.makeText(this, "Desconectado", Toast.LENGTH_LONG).show();

			} else if ("on".equalsIgnoreCase(result)) {
				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
					if (connected == true) {
						if (BluetoothHandler.sendData("ON")) {
							on_off=true;
							textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
							speak("Vitrocerámica encendida");
							Toast.makeText(getBaseContext(),"ON", Toast.LENGTH_SHORT).show();
						}
						else {
							on_off=false;
							Toast.makeText(getBaseContext(),"Error on Bluetooth Protocol", Toast.LENGTH_SHORT).show();
							textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
							speak("Se ha producido un error al enviar del dato. Por favor, verífique el receptor y vuelva a conectarse.");
						}

					}
					else {
						Toast.makeText(getBaseContext(),"Please connect first with the Electronic HOB", Toast.LENGTH_SHORT).show();
						on_off=false;
						textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
						speak("Por favor, conéctese primero con la vitrocerámica.");
					}

				}
				else {
					Toast.makeText(getBaseContext(),"Error on Bluetooth", Toast.LENGTH_SHORT).show();
					on_off=false;
					connected = false;
					textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
					speak("Se ha producido un error, conéctese de nuevo y vuelva a intentarlo.");
				}


			} else if("off".equalsIgnoreCase(result)) {
				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
					if (connected == true) {
						if (BluetoothHandler.sendData("OFF")) {
							on_off=false;
							textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
							speak("Vitrocerámica apagada");
							Toast.makeText(getBaseContext(),"OFF", Toast.LENGTH_SHORT).show();
						}
						else {
							Toast.makeText(getBaseContext(),"Error on Bluetooth Protocol", Toast.LENGTH_SHORT).show();
							textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
							speak("Se ha producido un error al enviar del dato. Por favor, verífique el receptor y vuelva a conectarse.");
							on_off=false;
							connected = false;
						}

					}
					else {
						Toast.makeText(getBaseContext(),"Please connect first with the Electronic HOB", Toast.LENGTH_SHORT).show();
						on_off=false;
						textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
						speak("Por favor, conéctese primero con la vitrocerámica.");
					}

				}
				else {
					Toast.makeText(getBaseContext(),"Error on Bluetooth", Toast.LENGTH_SHORT).show();
					textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
					speak("Se ha producido un error, conéctese de nuevo y vuelva a intentarlo.");
					on_off=false;
					connected = false;
				}

			}else if ("subir temperatura".equalsIgnoreCase(result)) {

				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
					if(on_off && connected) {
						if (BluetoothHandler.sendData("PWUP")) {
							textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
							speak("Subiendo la potencia");
							Toast.makeText(getBaseContext(),R.string.risePower, Toast.LENGTH_SHORT).show();
						}
						else {
							textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
							speak("Se ha producido un error al enviar del dato. Por favor, verífique el receptor y vuelva a conectarse.");
							Toast.makeText(getBaseContext(),"Error on Bluetooth Protocol", Toast.LENGTH_SHORT).show();
						}
					}
					else {
						Toast.makeText(getBaseContext(),"Please connect first with the Electronic HOB and turn it ON", Toast.LENGTH_SHORT).show();
						textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
						speak("Por favor, conéctese primero con la vitrocerámica y luego enciéndala con el comando ON.");
					}
				}
				else {
					Toast.makeText(getBaseContext(),"Error on Bluetooth", Toast.LENGTH_SHORT).show();
					textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
					speak("Se ha producido un error, conéctese de nuevo y vuelva a intentarlo.");
				}

			} else if ("bajar temperatura".equalsIgnoreCase(result)) {

				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
					if(on_off && connected) {
						if (BluetoothHandler.sendData("PWDOWN")) {
							textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
							speak("Bajando la potencia");
							Toast.makeText(getBaseContext(),R.string.risePower, Toast.LENGTH_SHORT).show();
						}
						else {
							textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
							speak("Se ha producido un error al enviar del dato. Por favor, verífique el receptor y vuelva a conectarse.");
							Toast.makeText(getBaseContext(),"Error on Bluetooth Protocol", Toast.LENGTH_SHORT).show();
						}
					}
					else {
						Toast.makeText(getBaseContext(),"Please connect first with the Electronic HOB and turn it ON", Toast.LENGTH_SHORT).show();
						textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
						speak("Por favor, conéctese primero con la vitrocerámica y luego enciéndala con el comando ON.");
					}
				}
				else {
					Toast.makeText(getBaseContext(),"Error on Bluetooth", Toast.LENGTH_SHORT).show();
					textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
					speak("Se ha producido un error, conéctese de nuevo y vuelva a intentarlo.");
				}

			}
			else {
				Toast.makeText(getBaseContext(),"No se ha reconocido el trazo", Toast.LENGTH_SHORT).show();
				textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
				speak("Comando gesture no reconocido. Dibújelo de nuevo");
			}

		}
	}

	protected void onDestroy() {

		if ( textToSpeech != null )
		{
			textToSpeech.stop();
			textToSpeech.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		//Log.d(TAG, "...In OnResume()...");

	}

	@Override
	public void onPause() {
		super.onPause();

		//Log.d(TAG, "...In onPause()...");
		if ((mBluetoothAdapter != null) && (mBluetoothAdapter.isEnabled()) && (BluetoothHandler != null)) {
			BluetoothHandler.freeConnection();
			on_off = false;
			connected = false;
		}
	}
	
	private void speak( String str )
	{
		textToSpeech.speak( str, TextToSpeech.QUEUE_FLUSH, null );
		textToSpeech.setSpeechRate( 0.0f );
		textToSpeech.setPitch( 0.0f );
	}

}
