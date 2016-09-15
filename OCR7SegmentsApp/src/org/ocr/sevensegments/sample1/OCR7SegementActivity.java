package org.ocr.sevensegments.sample1;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.samples.tutorial1.R;

import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.WriteFile;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.EditText;

public class OCR7SegementActivity extends Activity implements CvCameraViewListener2 {
    
	
	private static final String TAG = "OCVSample::Activity";
    private static final Scalar FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/SimpleAndroidOCR/";
    private static final String lang = "segment7";
    protected EditText _field;
    
    List<MatOfPoint> squares = new ArrayList<MatOfPoint>();

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;
    
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

    public OCR7SegementActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.tutorial1_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
        
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

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	//Mat img = getROI(inputFrame.rgba());
    	/*Imgproc.rectangle(img, new Point(100,100), new Point(300,300), FACE_RECT_COLOR, 3);
        return img;*/
    	
    	if (Math.random()>0.90) {

    		squares=findSquares(inputFrame.rgba().clone());

        }

        Mat image = inputFrame.rgba();

        Imgproc.drawContours(image, squares, -1, new Scalar(0,0,255));
        
        if (!squares.isEmpty())
        {
        	//subimage
        	Mat imageROI = image.submat(Imgproc.boundingRect(squares.get(0)));
        	
 
        	BitmapFactory.Options options = new BitmapFactory.Options();
        	options.inSampleSize = 4;
        	
        	Bitmap bitmap = Bitmap.createBitmap(imageROI.cols(),imageROI.rows(),Bitmap.Config.ARGB_8888);
        	Utils.matToBitmap(imageROI, bitmap);
        	//Binarize.otsuAdaptiveThreshold(pixs, sizeX, sizeY, smoothX, smoothY, scoreFraction)
        	String _path = DATA_PATH + "/ocr.png";
        	File file = new File(_path);
           	/*Debug*/
        	Bitmap blancoynegro = Bitmap.createBitmap(imageROI.cols(),imageROI.rows(),Bitmap.Config.ARGB_8888);;
        	try {
				OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
				blancoynegro = WriteFile.writeBitmap(Binarize.otsuAdaptiveThreshold(ReadFile.readBitmap(bitmap)));
				blancoynegro.compress(Bitmap.CompressFormat.PNG, 0, os);
				os.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	TessBaseAPI baseApi = new TessBaseAPI();
    		baseApi.setDebug(true);
    		baseApi.init(DATA_PATH, lang);
    		baseApi.setImage(file);
    		
    		final String recognizedText = baseApi.getUTF8Text();
    		baseApi.end();
    		
    		if ( recognizedText.length() != 0 ) {
    			//EditText field;	
    			//field = (EditText) findViewById(R.id.field);
    			this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						_field.setText(_field.getText().toString().length() == 0 ? recognizedText : recognizedText);
						_field.setSelection(_field.getText().toString().length());
					}
				});
    		}
        }
        
    	return image;
    }
    
    
    private List<MatOfPoint> findSquares (Mat inputImage)
    {
    	List<MatOfPoint> squares = new LinkedList<MatOfPoint>();
    	
    	//Image that we need
    	Mat smallerImg=new Mat(new Size(inputImage.width()/2, inputImage.height()/2),inputImage.type());
    	Mat gray = new Mat(inputImage.size(),inputImage.type());
    	Mat gray0 = new Mat(inputImage.size(),CvType.CV_8U);
    	
    	//down-scale and upscale the image to filter out the noise
    	Imgproc.pyrDown(inputImage, smallerImg,smallerImg.size());
    	Imgproc.pyrUp(smallerImg, inputImage,inputImage.size());
    	
    	//Find rectangules
    	//for (int c =0; c<3; c++)
    	//{
    		extractChannel(inputImage,gray,1);
    		
    		//threshold
    		for (int l = 0; l<N; l++)
    		{
    			Imgproc.threshold(gray, gray0, (l+1)*255/N, 255, Imgproc.THRESH_BINARY);
    			List<MatOfPoint> contours=new LinkedList<MatOfPoint>();
    			Imgproc.findContours(gray0, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
    			
    			MatOfPoint approx=new MatOfPoint();
    			for( int i = 0; i < contours.size(); i++ )
    			{
    				 approx = approxPolyDP(contours.get(i),  Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true)*0.02, true);
    				 
    				 if( approx.toArray().length == 4 &&
    	                     Math.abs(Imgproc.contourArea(approx)) > 1000 &&
    	                     Imgproc.isContourConvex(approx) )
    				 {
    					 double maxCosine = 0;

                         for( int j = 2; j < 5; j++ )
                         {
                             // find the maximum cosine of the angle between joint edges
                             double cosine = Math.abs(angle(approx.toArray()[j%4], approx.toArray()[j-2], approx.toArray()[j-1]));
                             maxCosine = Math.max(maxCosine, cosine);
                         }
                         
                         if( maxCosine < 0.3 )
                             squares.add(approx);
    				 }
    			}
    		}
    	//}
    	//if (squares.size()>1)
    		//Log.i(TAG, Long.toString(squares.get(0).));
    	return squares;
    }
    
    private void extractChannel(Mat source, Mat out, int channelNum) {
        List<Mat> sourceChannels=new ArrayList<Mat>();
        List<Mat> outChannel=new ArrayList<Mat>();

        Core.split(source, sourceChannels);

        outChannel.add(new Mat(sourceChannels.get(0).size(),sourceChannels.get(0).type()));

        Core.mixChannels(sourceChannels, outChannel, new MatOfInt(channelNum,0));

        Core.merge(outChannel, out);
    }
    
    private MatOfPoint approxPolyDP(MatOfPoint curve, double epsilon, boolean closed) {
        MatOfPoint2f tempMat=new MatOfPoint2f();

        Imgproc.approxPolyDP(new MatOfPoint2f(curve.toArray()), tempMat, epsilon, closed);

        return new MatOfPoint(tempMat.toArray());
    }
    
    // helper function:
    // finds a cosine of angle between vectors
    // from pt0->pt1 and from pt0->pt2
    private double angle( Point pt1, Point pt2, Point pt0 ) {
    	double dx1 = pt1.x - pt0.x;
    	double dy1 = pt1.y - pt0.y;
    	double dx2 = pt2.x - pt0.x;
    	double dy2 = pt2.y - pt0.y;
    	return (dx1*dx2 + dy1*dy2)/Math.sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
    }

}
