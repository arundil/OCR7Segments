package com.app.gokitchen;

import java.util.ArrayList;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class GestureActivity extends Activity implements OnGesturePerformedListener{

	GestureLibrary mLibrary;

	private static final String TAG = "GoKitchen::GestureActivity";
	private static final int REQUEST_ENABLE_BT = 1;
	private GoKitchenBluetoothHandler BluetoothHandler = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	private Boolean connected= false;
	private Boolean on_off = false;


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

				BluetoothHandler.connectBT();

				if (BluetoothHandler.checkBTState()) {
					if (BluetoothHandler.sendData("STATUS")) {
						Toast.makeText(this, "Conectado",Toast.LENGTH_SHORT).show();
						connected = true;
					}
					else {
						Toast.makeText(this, "ERROR en Protocolo",Toast.LENGTH_SHORT).show();
						connected = false;
					}
				}
				else {
					Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
					connected = false;
				}

			}else if ("disconnect".equalsIgnoreCase(result)) {
				BluetoothHandler.freeConnection();
				connected = false;
				Toast.makeText(this, "Desconectado", Toast.LENGTH_LONG).show();

			} else if ("on".equalsIgnoreCase(result)) {
				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
					if (connected == true) {
						if (BluetoothHandler.sendData("ON")) {
							on_off=true;
							Toast.makeText(getBaseContext(),"ON", Toast.LENGTH_SHORT).show();
						}
						else {
							on_off=false;
							Toast.makeText(getBaseContext(),"Error on Bluetooth Protocol", Toast.LENGTH_SHORT).show();
						}

					}
					else {
						Toast.makeText(getBaseContext(),"Please connect first with the Electronic HOB", Toast.LENGTH_SHORT).show();
						on_off=false;
					}

				}
				else {
					Toast.makeText(getBaseContext(),"Error on Bluetooth", Toast.LENGTH_SHORT).show();
					on_off=false;
					connected = false;
				}


			} else if("off".equalsIgnoreCase(result)) {
				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
					if (connected == true) {
						if (BluetoothHandler.sendData("OFF")) {
							on_off=false;
							Toast.makeText(getBaseContext(),"OFF", Toast.LENGTH_SHORT).show();
						}
						else {
							Toast.makeText(getBaseContext(),"Error on Bluetooth Protocol", Toast.LENGTH_SHORT).show();
							on_off=false;
							connected = false;
						}

					}
					else {
						Toast.makeText(getBaseContext(),"Please connect first with the Electronic HOB", Toast.LENGTH_SHORT).show();
						on_off=false;
					}

				}
				else {
					Toast.makeText(getBaseContext(),"Error on Bluetooth", Toast.LENGTH_SHORT).show();
					on_off=false;
					connected = false;
				}

			}else if ("subir temperatura".equalsIgnoreCase(result)) {

				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
					if(on_off && connected) {
						if (BluetoothHandler.sendData("PWUP")) {
							Toast.makeText(getBaseContext(),R.string.risePower, Toast.LENGTH_SHORT).show();
						}
						else {
							Toast.makeText(getBaseContext(),"Error on Bluetooth Protocol", Toast.LENGTH_SHORT).show();
						}
					}
					else {
						Toast.makeText(getBaseContext(),"Please connect first with the Electronic HOB and turn it ON", Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Toast.makeText(getBaseContext(),"Error on Bluetooth", Toast.LENGTH_SHORT).show();
				}

			} else if ("bajar temperatura".equalsIgnoreCase(result)) {

				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
					if(on_off && connected) {
						if (BluetoothHandler.sendData("PWDOWN")) {
							Toast.makeText(getBaseContext(),R.string.risePower, Toast.LENGTH_SHORT).show();
						}
						else {
							Toast.makeText(getBaseContext(),"Error on Bluetooth Protocol", Toast.LENGTH_SHORT).show();
						}
					}
					else {
						Toast.makeText(getBaseContext(),"Please connect first with the Electronic HOB and turn it ON", Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Toast.makeText(getBaseContext(),"Error on Bluetooth", Toast.LENGTH_SHORT).show();
				}

			}

		}
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "...In OnResume()...");

	}

	@Override
	public void onPause() {
		super.onPause();

		Log.d(TAG, "...In onPause()...");
		if ((mBluetoothAdapter != null) && (mBluetoothAdapter.isEnabled()) && (BluetoothHandler != null)) {
			BluetoothHandler.freeConnection();
			on_off = false;
			connected = false;
		}
	}

}
