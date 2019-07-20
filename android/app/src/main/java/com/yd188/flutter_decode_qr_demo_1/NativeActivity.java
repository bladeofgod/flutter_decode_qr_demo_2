package com.yd188.flutter_decode_qr_demo_1;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

import io.flutter.app.FlutterActivity;

public class NativeActivity extends FlutterActivity {
    TextView tvResult;
    ImageView ivResult;

    ImageModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);

//        byte[] datay = getIntent().getByteArrayExtra("datay");
//        byte[] datau= getIntent().getByteArrayExtra("datau");
//        byte[] datav = getIntent().getByteArrayExtra("datav");
//
//        StringBuffer sb = new StringBuffer();
//        sb.append("data y  :" + Arrays.toString(datay) + "\n");
//        sb.append("data u  :" +Arrays.toString(datau)+ "\n");
//        sb.append("data v  :" +Arrays.toString(datav)+ "\n");
        model =(ImageModel) getIntent().getBundleExtra("data").getSerializable(ImageModel.class.getSimpleName());

        Bitmap bitmap = DecodeQRCodeUtil.getSingleton(this)
                .loadUint8ListData(model.getBytesList(),model.getHeight(),model.getWidth()
                        ,model.getImageMean(),model.getImageStd(),model.getRotation())
                .getBitmapFromList();

        tvResult = findViewById(R.id.tv_result);
        //tvResult.setText(sb.toString());
        ivResult = findViewById(R.id.iv_result);
        ivResult.setImageBitmap(bitmap);

    }
}
