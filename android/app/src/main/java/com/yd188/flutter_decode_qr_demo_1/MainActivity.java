package com.yd188.flutter_decode_qr_demo_1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);

    new MethodChannel(getFlutterView(),"flutterplugin").setMethodCallHandler(new MethodChannel.MethodCallHandler() {
      @Override
      public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        if (methodCall.method.equals("jump")){
          List<byte[]> bytesList = methodCall.argument("bytesList");
          int width = methodCall.argument("imageWidth");
          int height = methodCall.argument("imageHeight");
          int rotation = methodCall.argument("rotation");
          double imageStd = methodCall.argument("imageStd");
          double imageMean = methodCall.argument("imageMean");
          int numResults = methodCall.argument("numResults");
          double threshold = methodCall.argument("threshold");
          boolean asynch = methodCall.argument("asynch");
          double ratio = methodCall.argument("ratio");
          ImageModel model = new ImageModel(
                  bytesList,width,height,ratio,rotation,imageStd,imageMean,numResults,threshold,asynch
          );
          Bitmap bitmap = DecodeQRCodeUtil.getSingleton(MainActivity.this)
                  .loadUint8ListData(model.getBytesList(),model.getHeight(),model.getWidth(),model.ratio
                          ,model.getImageMean(),model.getImageStd(),model.getRotation())
                  .getBitmapFromList();

          String resultStr = decodeNewUtil(bitmap);
          result.success(resultStr);
//          Intent intent = new Intent(MainActivity.this,NativeActivity.class);
//          Bundle bundle = new Bundle();
//          bundle.putSerializable(ImageModel.class.getSimpleName(),imageModel);
//          intent.putExtra("data",bundle);
//
//          startActivity(intent);
        }
      }
    });

  }

  private String decodeNewUtil(Bitmap bitmap){
    try {

      // 获取bitmap的宽高，像素矩阵
      int width = bitmap.getWidth();
      int height = bitmap.getHeight();
      int[] pixels = new int[width * height];
      bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

      // 最新的库中，RGBLuminanceSource 的构造器参数不只是bitmap了
      RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
      BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
      Map<DecodeHintType, Object> hints = new LinkedHashMap<DecodeHintType, Object>();
      MultiFormatReader reader = new MultiFormatReader();
      Result result = reader.decode(binaryBitmap, hints);
      //Toast.makeText(MainActivity.this,result.getText(),Toast.LENGTH_SHORT).show();
      return result.getText();

    } catch (Exception e) {
      e.printStackTrace();
      return  "-1";
    }

    //return  "-1";

  }
}
