package com.yd188.flutter_decode_qr_demo_1;

import android.content.Intent;
import android.os.Bundle;

import java.util.HashMap;
import java.util.List;

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
          ImageModel imageModel = new ImageModel(
                  bytesList,width,height,ratio,rotation,imageStd,imageMean,numResults,threshold,asynch
          );
          Intent intent = new Intent(MainActivity.this,NativeActivity.class);
          Bundle bundle = new Bundle();
          bundle.putSerializable(ImageModel.class.getSimpleName(),imageModel);
          intent.putExtra("data",bundle);

          startActivity(intent);
        }
      }
    });

  }
}
