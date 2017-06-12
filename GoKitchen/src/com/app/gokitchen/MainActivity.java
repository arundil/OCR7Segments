package com.app.gokitchen;

import com.app.gokitchen.util.GoKitchenBluetoothHandler;
import com.app.gokitchen.util.GoKitchenBluetoothHandlerImpl;

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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	//private static final String TAG = "goKitchen";
	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter mBluetoothAdapter = null;
	private TextView loginfo;
	private String[] arrayLog;
	private GoKitchenBluetoothHandler BluetoothHandler = null;
	private Switch ConnectBT;
	private Switch OnOff;



	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);		
		

		// 1) Get the bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		ConnectBT = (Switch) findViewById(R.id.switchBT);
		OnOff =(Switch) findViewById(R.id.OnOff);
		ConnectBT.setChecked(false);
		OnOff.setChecked(false);
		
		arrayLog = getResources().getStringArray(R.array.textStatus);
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
			AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
			alertDialog.setTitle("Oh Oh!");
			alertDialog.setMessage(getResources().getString(R.string.gesture_noBluetooth));
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


		loginfo = (TextView)findViewById(R.id.LOG);


		Button ocrActivation = (Button) findViewById(R.id.ActivateOCR);

		ocrActivation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent OCRIntent = new Intent().setClass(
						MainActivity.this, OCRActivity.class);
				startActivity(OCRIntent);
			}
		});


		ConnectBT.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if(isChecked){
					loginfo.setText(arrayLog[1]);
					BluetoothHandler.connectBT();

					if (BluetoothHandler.checkBTState()) {
						if (BluetoothHandler.sendData("STATUS")) {
							loginfo.setText(arrayLog[2]);
							buttonView.setChecked(true);
						}
						else {
							loginfo.setText(arrayLog[0]);
							buttonView.setChecked(false);
						}
					}
					else {
						Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
						loginfo.setText(arrayLog[3]);
						buttonView.setChecked(false);
					}

				}
				else{

					BluetoothHandler.freeConnection();
					buttonView.setChecked(false);
					loginfo.setText(arrayLog[0]);
					OnOff.setChecked(false);
				}

			}

		});
		

		OnOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {

					if(ConnectBT.isChecked()) {
						
						if(isChecked){
							loginfo.setText(arrayLog[4]);
							
							if (BluetoothHandler.sendData("ON")) {
								Toast.makeText(getBaseContext(),"ON", Toast.LENGTH_SHORT).show();
								loginfo.setText(arrayLog[5]);
							}
							else {
								loginfo.setText(arrayLog[3]);
								OnOff.setChecked(!OnOff.isChecked());
								Toast.makeText(getBaseContext(),getResources().getString(R.string.gesture_ProtocolError), Toast.LENGTH_SHORT).show();
							}
						}
						else {
							loginfo.setText(arrayLog[6]);
							if (BluetoothHandler.sendData("OFF")) {
								Toast.makeText(getBaseContext(),"OFF", Toast.LENGTH_SHORT).show();
								loginfo.setText(arrayLog[5]);
							}
							else {
								loginfo.setText(arrayLog[3]);
								OnOff.setChecked(false);
								ConnectBT.setChecked(false);
								
								Toast.makeText(getBaseContext(),getResources().getString(R.string.gesture_ProtocolError), Toast.LENGTH_SHORT).show();
							}
						}
					}
					else {
						loginfo.setText(arrayLog[0]);
						Toast.makeText(getBaseContext(),getResources().getString(R.string.gesture_NotConnected), Toast.LENGTH_SHORT).show();
						OnOff.setChecked(false);
					}
				}
				else {
					loginfo.setText(arrayLog[0]);
					Toast.makeText(getBaseContext(),"Error Bluetooth", Toast.LENGTH_SHORT).show();
					OnOff.setChecked(false);
					ConnectBT.setChecked(false);
				}
			}

		});

		Button PowerUp = (Button) findViewById(R.id.buttonUPPOWER);
		PowerUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
					loginfo.setText(arrayLog[8]);
					if(ConnectBT.isChecked() && OnOff.isChecked()) {
						if (BluetoothHandler.sendData("PWUP")) {
							Toast.makeText(getBaseContext(),R.string.risePower, Toast.LENGTH_SHORT).show();
							loginfo.setText(arrayLog[9]);
						}
						else {
							loginfo.setText(arrayLog[4]);
							Toast.makeText(getBaseContext(),getResources().getString(R.string.gesture_ProtocolError), Toast.LENGTH_SHORT).show();
						}
					}
					else {
						loginfo.setText(arrayLog[0]);
						Toast.makeText(getBaseContext(),getResources().getString(R.string.gesture_NotConnected), Toast.LENGTH_SHORT).show();
					}
				}
				else {
					loginfo.setText(arrayLog[0]);
					Toast.makeText(getBaseContext(),"Error Bluetooth", Toast.LENGTH_SHORT).show();
				}


			}
		});
		
		Button PowerDown = (Button) findViewById(R.id.buttonDOWNPOWER);
		PowerDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
					loginfo.setText(arrayLog[10]);
					if(ConnectBT.isChecked() && OnOff.isChecked()) {
						if (BluetoothHandler.sendData("PWDOWN")) {
							Toast.makeText(getBaseContext(),R.string.risePower, Toast.LENGTH_SHORT).show();
							loginfo.setText(arrayLog[11]);
						}
						else {
							loginfo.setText(arrayLog[4]);
							Toast.makeText(getBaseContext(),getResources().getString(R.string.gesture_ProtocolError), Toast.LENGTH_SHORT).show();
						}
					}
					else {
						loginfo.setText(arrayLog[0]);
						Toast.makeText(getBaseContext(),getResources().getString(R.string.gesture_NotConnected), Toast.LENGTH_SHORT).show();
					}
				}
				else {
					loginfo.setText(arrayLog[0]);
					Toast.makeText(getBaseContext(),"Error Bluetooth", Toast.LENGTH_SHORT).show();
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
		//Log.d(TAG, "...In OnResume()...");

	}

	@Override
	public void onPause() {
		super.onPause();
		
		//Log.d(TAG, "...In onPause()...");
		if ((mBluetoothAdapter != null) && (mBluetoothAdapter.isEnabled()) && (BluetoothHandler != null)) {
			BluetoothHandler.freeConnection();
			OnOff.setChecked(false);
			ConnectBT.setChecked(false);
		}
	}

}
