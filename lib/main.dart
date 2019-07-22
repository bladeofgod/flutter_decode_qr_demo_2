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
    try{
      onCameraSelected(widget.cameras[0]);
    }catch(e){
    print(e.toString());
}
  }

  @override
  void dispose() {
    // TODO: implement dispose
    super.dispose();
    controller.stopImageStream();
    controller.dispose();
  }


  @override
  Widget build(BuildContext context) {

    return Scaffold(
      appBar: AppBar(

        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Container(
              height: 600,
              child: CameraPreview(controller),
            ),

          ],
        ),
      ),
    );
  }


  bool isDetecting = false;

  void onCameraSelected(CameraDescription description)async{
    controller = CameraController(description, ResolutionPreset.medium);
    controller.addListener((){
      if(mounted)setState(() {

      });
      if(controller.value.hasError){
        print("camera error :${controller.value.errorDescription}");
      }
    });


    try{
      await controller.initialize().then((_){
        if(!mounted){
          return;
        }
        setState(() {

        });
      });
      controller.startImageStream((imageStream){
        if(! isDetecting){
          Future.delayed(Duration(seconds: 3)).whenComplete((){
            isDetecting = true;
            FlutterPlugin.jump(
                bytesList: imageStream.planes.map((plane){
                  return plane.bytes;
                }).toList()
            ,width: imageStream.width,height: imageStream.height,numResults:
            2,ratio:( imageStream.width / imageStream.height));
            controller.stopImageStream();
          });
        }

      });
    }on CameraException catch(e){
      print(e.toString());
    }

  }

}

















