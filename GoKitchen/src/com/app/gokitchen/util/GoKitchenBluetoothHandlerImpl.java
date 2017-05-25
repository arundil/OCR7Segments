package com.app.gokitchen.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

public class GoKitchenBluetoothHandlerImpl implements GoKitchenBluetoothHandler {

	private static final String TAG = "goKitchen-Bluetooth";
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothSocket mBluetoothSocket = null;
	private static String address = "B8:27:EB:B8:88:BD";
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final int REQUEST_ENABLE_BT = 1;
	OutputStream outStream;

	
	public GoKitchenBluetoothHandlerImpl() {
		mBluetoothAdapter = getBluetoothAdapter();
		
	}
	
	@Override
	public Boolean connectBT() {
		
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
		else {
			// Buscar dispositivo
			
			// return false si no se ha querido emparejar o no se ha encontrado.
			return false;
		}
		
		// From here, the device must be found and now we proceed to the connection.
		
		
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

			//errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
			return false;
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
//				errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
				return false;
			}
			return false;
		}

		// Create a data stream so we can talk to server.
		Log.d(TAG, "...Creating Socket...");

		try {
			outStream = mBluetoothSocket.getOutputStream();
		} catch (IOException e) {
//			errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
			return false;
		}

		return true;
	}

	@Override
	public Boolean checkBTState() {
		
		if (mBluetoothAdapter.isEnabled()) {
			Log.d(TAG, "...Bluetooth is enabled...");
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Boolean closeSocket() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BluetoothAdapter getBluetoothAdapter() {
		return BluetoothAdapter.getDefaultAdapter();
	}

	@Override
	public Boolean sendData(String message) {
		
		byte[] msgBuffer = message.getBytes();

		Log.d(TAG, "...Sending data: " + message + "...");

		try {
			outStream.write(msgBuffer);
			return true;
		} catch (IOException e) {
			String msg = "An exception occurred during write: " + e.getMessage();
			if (address.equals("00:00:00:00:00:00")) 
				msg = msg + ".\n\nUpdate your server address";
			msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on Bluetooth server.\n\n";

//			errorExit("Fatal Error", msg); 
			freeConnection();
			return false;
		}
		
		
	}
	
	public void freeConnection ()
	{
		
		if (outStream != null) {
			try {
				outStream.flush();
			} catch (IOException e) {
				//errorExit("Fatal Error", "In onCheckedChanged() and failed to flush output stream: " + e.getMessage() + ".");
			}
		}

		try  {
			mBluetoothSocket.close();
		} catch (IOException e2) {
//			errorExit("Fatal Error", "In onCheckedChanged() and failed to close socket." + e2.getMessage() + ".");
		}
	}
	

}
