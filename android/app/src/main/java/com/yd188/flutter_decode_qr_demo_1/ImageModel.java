package com.yd188.flutter_decode_qr_demo_1;

import java.io.Serializable;
import java.util.List;

/**
 * @author lijiaqi
 * @date 2019/7/15.
 */
public class ImageModel implements Serializable {


    List<byte[]> bytesList ;
    int width ;
    int height ;
    double ratio;
    int rotation ;
    double imageStd ;
    double imageMean ;
    int numResults ;
    double threshold ;
    boolean asynch ;


    public ImageModel(List<byte[]> bytesList, int width, int height,double ratio, int rotation, double imageStd, double imageMean, int numResults, double threshold, boolean asynch) {
        this.bytesList = bytesList;
        this.width = width;
        this.height = height;
        this.ratio = ratio;
        this.rotation = rotation;
        this.imageStd = imageStd;
        this.imageMean = imageMean;
        this.numResults = numResults;
        this.threshold = threshold;
        this.asynch = asynch;
    }

    public List<byte[]> getBytesList() {
        return bytesList;
    }

    public void setBytesList(List<byte[]> bytesList) {
        this.bytesList = bytesList;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public double getImageStd() {
        return imageStd;
    }

    public void setImageStd(double imageStd) {
        this.imageStd = imageStd;
    }

    public double getImageMean() {
        return imageMean;
    }

    public void setImageMean(double imageMean) {
        this.imageMean = imageMean;
    }

    public int getNumResults() {
        return numResults;
    }

    public void setNumResults(int numResults) {
        this.numResults = numResults;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public boolean isAsynch() {
        return asynch;
    }

    public void setAsynch(boolean asynch) {
        this.asynch = asynch;
    }
}
