package com.app.gokitchen.util;

public interface GoKitchenBluetoothHandler {
	Boolean connectBT();
	Boolean checkBTState();
	Boolean sendData(String data);
	void freeConnection ();
}
