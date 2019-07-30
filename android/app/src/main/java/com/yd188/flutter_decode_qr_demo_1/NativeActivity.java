package com.yd188.flutter_decode_qr_demo_1;


import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import io.flutter.app.FlutterActivity;

import static android.provider.Telephony.Mms.Part.CHARSET;

public class NativeActivity extends FlutterActivity {

    private  MultiFormatReader multiFormatReader;
    private Map<DecodeHintType,Object> hints;

    TextView tvResult;
    ImageView ivResult;

    ImageModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native);

        multiFormatReader = new MultiFormatReader();

        hints = new EnumMap<DecodeHintType,Object>(DecodeHintType.class);

        Collection<BarcodeFormat> decodeFormats = EnumSet.noneOf(BarcodeFormat.class);

        // The prefs can't change while the thread is running, so pick them up once here.
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
        multiFormatReader.setHints(hints);

//        if (characterSet != null) {
//            hints.put(DecodeHintType.CHARACTER_SET, characterSet);
//        }




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
                .loadUint8ListData(model.getBytesList(),model.getHeight(),model.getWidth(),model.ratio
                        ,model.getImageMean(),model.getImageStd(),model.getRotation())
                .getBitmapFromList();

        tvResult = findViewById(R.id.tv_result);
        //tvResult.setText(sb.toString());
        ivResult = findViewById(R.id.iv_result);
        ivResult.setImageBitmap(bitmap);

        findViewById(R.id.btn_decode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //decodeBitmap(bitmap);
                //decodeFromData(model.getBytesList());
                if (bitmap == null){
                    Toast.makeText(NativeActivity.this,"bitmap is null",Toast.LENGTH_SHORT).show();
                    return;
                }
                decodeNewUtil(bitmap);


            }
        });

    }

    private void decodeNewUtil(Bitmap bitmap){
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
            Toast.makeText(NativeActivity.this,result.getText(),Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }



    private void decodeFromData(List<byte[]> bytes){
        byte[] data = transformData(bytes);
        Result rawResult = null;

        int width = model.width;
        int height = model.height;

        //竖屏处理
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }
        int tmp = width; // Here we are swapping, that's the difference to #11
        width = height;
        height = tmp;

        PlanarYUVLuminanceSource source = buildLuminanceSource(rotatedData, width,height);
        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = multiFormatReader.decodeWithState(bitmap);
                if (rawResult != null){
                    Toast.makeText(NativeActivity.this,"扫码结果是：" + rawResult.toString(),Toast.LENGTH_SHORT).show();
                }
            } catch (ReaderException re) {
                // continue
                Log.i("reader exception : " ,re.toString());
            } finally {
                multiFormatReader.reset();
            }
        }


    }
    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
//        Rect rect = getFramingRectInPreview();
//        if (rect == null) {
//            return null;
//        }
        // Go ahead and assume it's YUV rather than die.
        return new PlanarYUVLuminanceSource(data, width, height, 0, 0,
                width, height, false);
    }

    private byte[] transformData(List<byte[]> bytesList){

        ByteBuffer Y = ByteBuffer.wrap(bytesList.get(0));
        ByteBuffer U = ByteBuffer.wrap(bytesList.get(1));
        ByteBuffer V = ByteBuffer.wrap(bytesList.get(2));

        int Yb = Y.remaining();
        int Ub = U.remaining();
        int Vb = V.remaining();

        byte[] data = new byte[Yb + Ub + Vb];

        Y.get(data, 0, Yb);
        V.get(data, Yb, Vb);
        U.get(data, Yb + Vb, Ub);
        return data;

    }


}
