package com.app.gokitchen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class GestureActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture);
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
}
