package com.app.gokitchen.util;

import org.opencv.core.Mat;

import android.graphics.Bitmap;

public interface OCR7SegmentImageEnhacement {
	Mat deskew (Mat imginput);
	Mat eliminateLines (Mat imglines);
	Bitmap CreateBitmapFromMat(Mat intupImage);

}
