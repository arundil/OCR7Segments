package com.app.gokitchen;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import com.app.gokitchen.util.*;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import com.app.gokitchen.util.OCR7SegmentDictionary;
import com.app.gokitchen.util.OCR7SegmentDictionaryImpl;
import com.googlecode.tesseract.android.TessBaseAPI;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class OCRActivity extends Activity implements CvCameraViewListener2,TextToSpeech.OnInitListener{

	private static final String TAG = "OCVSample::Activity";
	private static final Scalar FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
	private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/GoKitchen/";
	private static final String lang = "7seg";
	protected EditText _field;
	private TextToSpeech textToSpeech;
	private OCR7SegmentDictionary dictionary = new OCR7SegmentDictionaryImpl();
	List<MatOfPoint> squares = new ArrayList<MatOfPoint>();
	private CameraBridgeViewBase mOpenCvCameraView;
	Boolean reiniciarApp = false;
	int thresh = 50, N = 5;
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
			{
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
			} break;
			default:
			{
				super.onManagerConnected(status);
			} break;
			}
		}
	};
	
	public OCRActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		textToSpeech = new TextToSpeech( this, this );
		textToSpeech.setLanguage( new Locale( "spa", "ESP" ) );


		setContentView(R.layout.activity_ocr);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.surface_view);
		
		mOpenCvCameraView.setVisibility(View.VISIBLE);

		mOpenCvCameraView.setCvCameraViewListener(this);

		/*Check the permissions, in case any were not set, set it and reboot the activity*/
		
		if (ContextCompat.checkSelfPermission(OCRActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(OCRActivity.this,
	                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
	                1);
			reiniciarApp = true;

		}
		
		if (ContextCompat.checkSelfPermission(OCRActivity.this,
                Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(OCRActivity.this,
	                new String[]{Manifest.permission.CAMERA},
	                1);
			reiniciarApp = true;
		}


		//Tesseract treatment
		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}


		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata")).exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");
				//GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/" + lang + ".traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				//while ((len = gin.read(buff)) > 0) {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				//gin.close();
				out.close();

				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
			}
		}

		_field = (EditText) findViewById(R.id.field);

		dictionary.fillDictionary();	
		speak("Cargada interfaz de voz a español");
		
		if(reiniciarApp)
			OCRActivity.this.finish();
	}
	

	@Override
	public void onInit( int status )
	{
		if ( status == TextToSpeech.LANG_MISSING_DATA | status == TextToSpeech.LANG_NOT_SUPPORTED )
		{
			Toast.makeText( this, "ERROR LANG_MISSING_DATA | LANG_NOT_SUPPORTED", Toast.LENGTH_SHORT ).show();
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (!OpenCVLoader.initDebug()) {
			Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
		} else {
			Log.d(TAG, "OpenCV library found inside package. Using it!");
			mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
		}
	}

	@Override
	public void onDestroy() {

		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();

		if ( textToSpeech != null )
		{
			textToSpeech.stop();
			textToSpeech.shutdown();
		}
		super.onDestroy();
	}

	
	/*===========================================
	 * Mat onCameraFrame (non-Javadoc)
	 * @see org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2#onCameraFrame(org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame)
	 * 
	 * ==========================================
	 */

	
	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {


		OCR7SegmentRoiDetection RoiDetection = new OCR7SegmentRoiDetectionImpl();

		if (Math.random()>0.90) {

			squares=RoiDetection.findSquares(inputFrame.rgba().clone());

		}

		Mat image = inputFrame.rgba();

		Imgproc.drawContours(image, squares, -1, new Scalar(0,0,255));

		if (!squares.isEmpty())
		{

			//subimage

			for (MatOfPoint p :squares)
			{

				Mat imageROI = image.submat(Imgproc.boundingRect(p));

				if ((imageROI.height()<=400 && imageROI.height()>=50)  && (imageROI.width()<=900 && imageROI.width()>=150)) {

					List<MatOfPoint> listaux = new LinkedList<MatOfPoint>();
					listaux.add(p);
					Mat imageROI_prepared = prepareImage4OCR(imageROI,p);        		
					Imgproc.drawContours(image, listaux, -1, FACE_RECT_COLOR,2);


					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 4;

					Bitmap bitmap = Bitmap.createBitmap(imageROI_prepared.cols(),imageROI_prepared.rows(),Bitmap.Config.ARGB_8888);
					Utils.matToBitmap(imageROI_prepared, bitmap);
					String _path = DATA_PATH + "/ocr.png";
					File file = new File(_path);
					/*Debug*/
					try {
						OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
						bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
						os.close();

					} catch (IOException e) {
						e.printStackTrace();
					}

					TessBaseAPI baseApi = new TessBaseAPI();
					baseApi.setDebug(true);
					baseApi.init(DATA_PATH, lang);
					baseApi.setImage(bitmap);
					//baseApi.setImage(file);

					final String recognizedText = baseApi.getUTF8Text();
					dictionary.UpdateElement(recognizedText, 1);
					baseApi.end();

					if ( recognizedText.length() != 0 ) {

						this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								_field.setText(_field.getText().toString().length() == 0 ? recognizedText : recognizedText);
								_field.setSelection(_field.getText().toString().length());
								textToSpeech.setLanguage( new Locale( "esp", "ESP" ) );
								//speak( _field.getText().toString() );
								String value = dictionary.evaluateDictionary();
								if (!value.equals("")){
									speak(value);
									dictionary.restartDictionary();
								}
							}
						});

					}
					break;
				}
			}
		}
		return image;
	}

	
	private Mat prepareImage4OCR (Mat rgb, MatOfPoint p)
	{
		OCR7SegmentImageEnhacement OCRImage = new OCR7SegmentImageEnhacementImpl();
		Mat ret = rgb.clone();
		Imgproc.cvtColor(ret, ret, Imgproc.COLOR_RGBA2GRAY);
		Imgproc.threshold(ret, ret, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU); //Threshold put to 127 over 255
		Mat kernel = Mat.ones(new Size(2,2),CvType.CV_8U);
		Imgproc.medianBlur(ret, ret, 5); //Smoooth filter 
		ret= OCRImage.deskew(ret);
		Log.v(TAG, "rows " + ret.rows());
		Log.v(TAG, "cols " + ret.cols());
		int cols_to_remove = (int) (ret.cols()*0.05);
		int rows_to_remove = (int) (ret.rows()*0.05);
		Mat retfinal= ret.submat(rows_to_remove, ret.rows()-rows_to_remove, cols_to_remove, ret.cols()-cols_to_remove);
		Imgproc.erode(retfinal, retfinal, kernel,new Point(),1);
		

		return retfinal;
	}


	private void speak( String str )
	{
		textToSpeech.speak( str, TextToSpeech.QUEUE_FLUSH, null );
		textToSpeech.setSpeechRate( 0.0f );
		textToSpeech.setPitch( 0.0f );
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}

}
