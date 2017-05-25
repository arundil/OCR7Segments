package com.app.gokitchen.util;

import android.bluetooth.BluetoothAdapter;

public interface GoKitchenBluetoothHandler {
	Boolean connectBT();
	Boolean checkBTState();
	Boolean closeSocket();
	BluetoothAdapter getBluetoothAdapter();
	Boolean sendData(String data);
	void freeConnection ();
}
