package com.app.gokitchen.util;

import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;

public interface OCR7SegmentRoiDetection {
	List<MatOfPoint> findSquares (Mat inputImage);
	void extractChannel(Mat source, Mat out, int channelNum);
	MatOfPoint approxPolyDP(MatOfPoint curve, double epsilon, boolean closed);
	double angle( Point pt1, Point pt2, Point pt0 );

}
