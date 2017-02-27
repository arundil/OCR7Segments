package com.app.gokitchen.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class OCR7SegmentImageEnhacementImpl implements OCR7SegmentImageEnhacement {

	private static final String TAG = "OCVSample::Activity";
	private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/SimpleAndroidOCR/";

	public  OCR7SegmentImageEnhacementImpl() {
	}

	@Override
	public Mat deskew(Mat imginput) {

		Mat ret = imginput.clone();

		Size size = imginput.size();
		Core.bitwise_not(imginput, ret);
		Point center = new Point(imginput.width()/2, imginput.height()/2);
		Mat lines = new Mat();
		Imgproc.HoughLinesP(ret, lines, 1, Math.PI / 180,100,size.width/ 2.f,20);
		double angle = 0;
		for (int i= 0; i<lines.height(); i++){
			for (int j= 0; j<lines.width(); j++) {
				angle += Math.atan2(lines.get(i, j)[3] - lines.get(i, j)[1], lines.get(i, j)[2] - lines.get(i, j)[0]);
			}
		}

		angle /= lines.size().area();
		angle = angle * 180 / Math.PI;
		Log.v(TAG, "ANGLE " + angle );
		if (angle >1 || angle <1){
			Mat rotation = Imgproc.getRotationMatrix2D(center, angle, 1.0);
			Imgproc.warpAffine(imginput, ret, rotation, size);
			return ret;
		}
		return imginput;
	}

	@Override
	public Mat eliminateLines(Mat imglines) {
		Mat bw = new Mat();
		//Imgproc.threshold(imglines, bw, 127, 255, Imgproc.THRESH_BINARY_INV); //Threshold put to 127 over 255
		Core.bitwise_not(imglines,bw);
		Mat horizontal = bw.clone();
		Mat vertical = bw.clone();

		int verticalsize = vertical.rows() / 30;
		int horizontalsize = horizontal.cols() / 30;
		Log.v(TAG, "HorizontalSize " + horizontalsize );
		Mat horizontalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(horizontalsize,1));   	
		Imgproc.erode(horizontal, horizontal, horizontalStructure, new Point(-1, -1),1);
		Imgproc.dilate(horizontal, horizontal, horizontalStructure, new Point(-1, -1),1);

		/*Debug*/
		Bitmap bitmap1 = Bitmap.createBitmap(horizontal.cols(),horizontal.rows(),Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(horizontal, bitmap1);
		String _path1 = DATA_PATH + "/Horizontal.png";
		File file1 = new File(_path1);
		try {
			OutputStream os = new BufferedOutputStream(new FileOutputStream(file1));
			bitmap1.compress(Bitmap.CompressFormat.PNG, 0, os);
			os.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*Fin debug*/

		Mat verticalStructure = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,  new Size(1,verticalsize));
		Log.v(TAG, "VerticalSize " + verticalsize );

		Imgproc.erode(vertical, vertical, verticalStructure, new Point(-1, -1),1);
		Imgproc.dilate(vertical, vertical, verticalStructure, new Point(-1, -1),1);

		/*Debug*/
		Bitmap bitmap2 = Bitmap.createBitmap(vertical.cols(),vertical.rows(),Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(vertical, bitmap2);
		String _path2 = DATA_PATH + "/Vertical.png";
		File file2 = new File(_path2);
		try {
			OutputStream os = new BufferedOutputStream(new FileOutputStream(file2));
			bitmap2.compress(Bitmap.CompressFormat.PNG, 0, os);
			os.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*Fin debug*/


		Mat edges = new Mat();
		//Imgproc.adaptiveThreshold(vertical, edges, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 3, -2);
		//Imgproc.dilate(edges, edges, kernel);
		Mat smooth_V = new Mat();
		vertical.copyTo(smooth_V);
		Imgproc.blur(smooth_V, smooth_V,new Size(2, 2));
		smooth_V.copyTo(vertical, edges);
		Imgproc.threshold(vertical, vertical, 127, 255, Imgproc.THRESH_BINARY_INV); //Threshold put to 127 over 255

		return vertical;
	}

	@Override
	public Bitmap CreateBitmapFromMat(Mat intupImage) {
		// TODO Auto-generated method stub
		return null;
	}

}
