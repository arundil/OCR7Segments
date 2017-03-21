package com.app.gokitchen;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private final static int REQUEST_ENABLE_BT = 1;
	private ListView mArrayAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		
		//Bluetooth
		
		final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
			if (!mBluetoothAdapter.isEnabled()) {
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
			
			/*Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
			// If there are paired devices
			if (pairedDevices.size() > 0) {
			    // Loop through paired devices
			    for (BluetoothDevice device : pairedDevices) {
			        // Add the name and address to an array adapter to show in a ListView
			        ((Menu) mArrayAdapter).add(device.getName() + "\n" + device.getAddress());
			    }
			}
			else {
				// Create a BroadcastReceiver for ACTION_FOUND
				final BroadcastReceiver mReceiver = new BroadcastReceiver() {
				    public void onReceive(Context context, Intent intent) {
				        String action = intent.getAction();
				        // When discovery finds a device
				        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				            // Get the BluetoothDevice object from the Intent
				            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				            // Add the name and address to an array adapter to show in a ListView
				            ((Menu) mArrayAdapter).add(device.getName() + "\n" + device.getAddress());
				        }
				    }
				};
				// Register the BroadcastReceiver
				IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
				registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
			}*/
		}
		
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
					Toast.makeText(getBaseContext(),R.string.onbutton, Toast.LENGTH_LONG).show();
				}
				else {
					Toast.makeText(getBaseContext(),"Error on Bluetooth", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		Button PowerOff = (Button) findViewById(R.id.buttonOFF);
		PowerOff.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
					Toast.makeText(getBaseContext(),R.string.offbutton, Toast.LENGTH_LONG).show();
				}
				else {
					Toast.makeText(getBaseContext(),"Error on Bluetooth", Toast.LENGTH_LONG).show();
				}
			}
		});
		Button PowerUp = (Button) findViewById(R.id.buttonUPPOWER);
		PowerUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
					Toast.makeText(getBaseContext(),R.string.risePower, Toast.LENGTH_LONG).show();
				}
				else {
					Toast.makeText(getBaseContext(),"Error on Bluetooth", Toast.LENGTH_LONG).show();
				}
				
			}
		});
		Button PowerDown = (Button) findViewById(R.id.buttonDOWNPOWER);
		PowerDown.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
					Toast.makeText(getBaseContext(),R.string.lowerPower, Toast.LENGTH_LONG).show();
				}
				else {
					Toast.makeText(getBaseContext(),"Error on Bluetooth", Toast.LENGTH_LONG).show();
				}
			}
		});
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
	
}
