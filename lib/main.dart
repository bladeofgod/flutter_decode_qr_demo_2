import 'package:flutter/material.dart';
import 'devices_holder.dart';
import 'package:camera/camera.dart';
import 'dart:typed_data';
import 'flutter_plugin.dart';


void main()async{

  try{
    await availableCameras().then((camerasList){
      DevicesHolder.singleton.assembleCameraInfo(camerasList);
    });
  }on CameraException catch(e){
    print(e.toString());
  }

  runApp(MyApp());
}

class MyApp extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(

        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);
  final String title;

  List<CameraDescription> cameras = DevicesHolder.singleton.cameras;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {

  CameraController controller;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    if(widget.cameras == null  || widget.cameras.length < 1){
        print("No Cameara is found");
    }else{
      onCameraSelected(widget.cameras[0]);
    }

  }

  @override
  void dispose() {
    // TODO: implement dispose
    controller?.dispose();
    super.dispose();

  }


  @override
  Widget build(BuildContext context) {

    return Scaffold(
      appBar: AppBar(

        title: Text(widget.title),
      ),
      body: Center(
        child: Stack(
          children: <Widget>[
            CameraPreview(controller),
          ],
        ),
      ),
    );
  }


  bool isDetecting = false;

  void onCameraSelected(CameraDescription description){
    controller = CameraController(description, ResolutionPreset.low);
//    controller.addListener((){
//      if(mounted)setState(() {
//
//      });
//      if(controller.value.hasError){
//        print("camera error :${controller.value.errorDescription}");
//      }
//    });

    controller.initialize().then((_){
      if(!mounted){
        return;
      }
      setState(() {

      });

      controller.startImageStream((CameraImage imageStream){
        if(! isDetecting){
          isDetecting = true;

          Future.delayed(Duration(seconds: 3)).then((_){
            FlutterPlugin.jump(
                bytesList: imageStream.planes.map((plane){
                  return plane.bytes;
                }).toList()
                ,width: imageStream.width,
                height: imageStream.height
                ,numResults: 2,
                ratio:( imageStream.width / imageStream.height));
          });

          //Lost connection to device. 可能的原因： channel 传输数据过大

//              .then((result){
//            if("1" == result){
//              print("${result}");
//              isDetecting = false;
//            }
//          });
//            Future.delayed(new Duration(seconds: 3)).whenComplete((){
//
//
//            });

          //controller.stopImageStream();
        }

      });

    });


//    try{
//
//
//    }on CameraException catch(e){
//      print(e.toString());
//    }

  }

}

















