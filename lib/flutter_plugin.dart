
import 'dart:typed_data';
import 'dart:async';
import 'package:camera/camera.dart';
import 'package:flutter/services.dart';
import 'package:meta/meta.dart';



class FlutterPlugin{

  static const MethodChannel _channel = const MethodChannel("flutterplugin");

  static Future<Null> jump({
    @required List<Uint8List> bytesList
    ,int width = 720,
    int height = 1280 ,
    double ratio = 0.56,
    double imageMean = 127.5,
    double imageStd = 127.5,int rotation: 90, // Android only
    int numResults = 5,
    double threshold = 0.1,
    bool asynch = true})
  async{
      await _channel.invokeMethod("jump",
         {
           "bytesList": bytesList,
           "imageHeight": height,
           "imageWidth": width,
           "ratio" : ratio,
           "imageMean": imageMean,
           "imageStd": imageStd,
           "rotation": rotation,
           "numResults": numResults,
           "threshold": threshold,
           "asynch": asynch,
         });
  }





}