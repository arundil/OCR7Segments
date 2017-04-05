package com.app.gokitchen;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = "goKitchen";
	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothSocket mBluetoothSocket = null;
	private OutputStream outStream = null;
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static String address = "B8:27:EB:B8:88:BD";


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);		


		// 1) Get the bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
			AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
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
			checkBTState();
		}

		/*TODO Bloquear los botones*/

		Button ocrActivation = (Button) findViewById(R.id.ActivateOCR);

		ocrActivation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent OCRIntent = new Intent().setClass(
						MainActivity.this, OCRActivity.class);
				startActivity(OCRIntent);
			}
		});

		Button PowerOn = (Button) findViewById(R.id.buttonON);
		PowerOn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
					Toast.makeText(getBaseContext(),R.string.onbutton, Toast.LENGTH_SHORT).show();
					sendData("LED");
				}
				else {
					Toast.makeText(getBaseContext(),"Error on Bluetooth", Toast.LENGTH_SHORT).show();
				}
			}
		});

		Button PowerOff = (Button) findViewById(R.id.buttonOFF);
		PowerOff.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
					Toast.makeText(getBaseContext(),R.string.offbutton, Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(getBaseContext(),"Error on Bluetooth", Toast.LENGTH_SHORT).show();
				}
			}
		});
		Button PowerUp = (Button) findViewById(R.id.buttonUPPOWER);
		PowerUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
					Toast.makeText(getBaseContext(),R.string.risePower, Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(getBaseContext(),"Error on Bluetooth", Toast.LENGTH_SHORT).show();
				}

			}
		});
		Button PowerDown = (Button) findViewById(R.id.buttonDOWNPOWER);
		PowerDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
					Toast.makeText(getBaseContext(),R.string.lowerPower, Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(getBaseContext(),"Error on Bluetooth", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	protected void onDestroy() {
		super.onDestroy();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
			return true;
		}
		if (id == R.id.action_gesture) {
			//Aqui se debe programar el otro menú
			try{
				Intent intent = new Intent(MainActivity.this, GestureActivity.class);
				startActivity(intent);
			}catch (Exception e){}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();

		Log.d(TAG, "...In onResume - Attempting client connect...");

		// Set up a pointer to the remote node using it's address.
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		String deviceHardwareAddress = "";
		if (pairedDevices.size() > 0) {
			// There are paired devices. Get the name and address of each paired device.
			for (BluetoothDevice device : pairedDevices) {
				if (device.getName().equals("raspberrypi")) {
					deviceHardwareAddress = device.getAddress(); // MAC address
					break;
				}
			}
		}
		BluetoothDevice device = null;
		if (!(deviceHardwareAddress == ""))
			 device = mBluetoothAdapter.getRemoteDevice(deviceHardwareAddress);
		else
			device = mBluetoothAdapter.getRemoteDevice(address);
		
		// Two things are needed to make a connection:
		//   A MAC address, which we got above.
		//   A Service ID or UUID.  In this case we are using the
		//     UUID for SPP.
		try {
			mBluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
			errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
		}

		// Discovery is resource intensive.  Make sure it isn't going on
		// when you attempt to connect and pass your message.
		mBluetoothAdapter.cancelDiscovery();

		// Establish the connection.  This will block until it connects.
		Log.d(TAG, "...Connecting to Remote...");
		try {
			mBluetoothSocket.connect();
			Log.d(TAG, "...Connection established and data link opened...");
		} catch (IOException e) {
			try {
				mBluetoothSocket.close();
			} catch (IOException e2) {
				errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
			}
		}

		// Create a data stream so we can talk to server.
		Log.d(TAG, "...Creating Socket...");

		try {
			outStream = mBluetoothSocket.getOutputStream();
		} catch (IOException e) {
			errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		Log.d(TAG, "...In onPause()...");

		if (outStream != null) {
			try {
				outStream.flush();
			} catch (IOException e) {
				errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
			}
		}

		try     {
			mBluetoothSocket.close();
		} catch (IOException e2) {
			errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
		}
	}

	private void checkBTState() {
		// Check for Bluetooth support and then check to make sure it is turned on

		if (mBluetoothAdapter.isEnabled()) {
			Log.d(TAG, "...Bluetooth is enabled...");
		} else {
			//Prompt user to turn on Bluetooth
			Intent enableBtIntent = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

		}
	}

	private void sendData(String message) {
		byte[] msgBuffer = message.getBytes();

		Log.d(TAG, "...Sending data: " + message + "...");

		try {
			outStream.write(msgBuffer);
		} catch (IOException e) {
			String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
			if (address.equals("00:00:00:00:00:00")) 
				msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code";
			msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

			errorExit("Fatal Error", msg);       
		}
	}

	private void errorExit(String title, String message){
		Toast msg = Toast.makeText(getBaseContext(),
				title + " - " + message, Toast.LENGTH_SHORT);
		msg.show();
		//finish();
	}

}
