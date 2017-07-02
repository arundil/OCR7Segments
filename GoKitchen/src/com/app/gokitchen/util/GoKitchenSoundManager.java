package com.app.gokitchen.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class GoKitchenSoundManager {
	private Context pContext;
	private SoundPool sndPool;
	private float rate = 1.0f;
	private float leftVolume = 1.0f;
	private float rightVolume = 1.0f;
	
	
	//La clase SoundPool administra y ejecuta todos los recursos de audio de la aplicacion.
	
	//Nuestro constructor, que determina la configuracion de audio del contexto de nuestra aplicacion
	public GoKitchenSoundManager (Context appContext)
	{
	  sndPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 100);
	  pContext = appContext;
	}
	//Obtiene el sonido y retorna el id del mismo
	public int load(int idSonido)
	{
		return sndPool.load(pContext, idSonido, 1);
	}
	//Ejecuta el sonido, toma como parametro el id del sonido a ejecutar.
	public void play(int idSonido)
	{
		sndPool.play(idSonido, leftVolume, rightVolume, 1, 0, rate); 	
	}
	
	// Libera memoria de todos los objetos del sndPool que ya no son requeridos.
	public void unloadAll()
	{
		sndPool.release();		
	}
}
