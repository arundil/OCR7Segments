package org.ocr.sevensegments.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


public class OCR7SegmentRoiDetectionImpl implements OCR7SegmentRoiDetection {

	private int N = 5;
	
	public OCR7SegmentRoiDetectionImpl() {
	}
	
	@Override
	public List<MatOfPoint> findSquares(Mat inputImage) {
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
		return squares;
	}

	@Override
	public void extractChannel(Mat source, Mat out, int channelNum) {
        List<Mat> sourceChannels=new ArrayList<Mat>();
        List<Mat> outChannel=new ArrayList<Mat>();

        Core.split(source, sourceChannels);

        outChannel.add(new Mat(sourceChannels.get(0).size(),sourceChannels.get(0).type()));

        Core.mixChannels(sourceChannels, outChannel, new MatOfInt(channelNum,0));

        Core.merge(outChannel, out);

	}

	@Override
	public MatOfPoint approxPolyDP(MatOfPoint curve, double epsilon, boolean closed) {
        MatOfPoint2f tempMat=new MatOfPoint2f();

        Imgproc.approxPolyDP(new MatOfPoint2f(curve.toArray()), tempMat, epsilon, closed);

        return new MatOfPoint(tempMat.toArray());
	}

	@Override
	public double angle(Point pt1, Point pt2, Point pt0) {
		
    	double dx1 = pt1.x - pt0.x;
    	double dy1 = pt1.y - pt0.y;
    	double dx2 = pt2.x - pt0.x;
    	double dy2 = pt2.y - pt0.y;
    	return (dx1*dx2 + dy1*dy2)/Math.sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
	}


}
