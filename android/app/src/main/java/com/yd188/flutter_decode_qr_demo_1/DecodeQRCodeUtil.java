package com.yd188.flutter_decode_qr_demo_1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;


import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author aichonghui
 * @date 2019/6/24.
 */
public class DecodeQRCodeUtil {

    private static SoftReference<Context> contextSoftReference;

    private static volatile DecodeQRCodeUtil singleton;

    public static DecodeQRCodeUtil getSingleton(Context context){
        if (singleton == null){
            synchronized (DecodeQRCodeUtil.class){
                if (singleton == null){
                    singleton = new DecodeQRCodeUtil();
                }
            }
        }
        contextSoftReference = new SoftReference<>(context);
        return singleton;
    }

    private List<byte[]> bytesList;


    //当直接从flutter 拿到宽高后，会导致 比例严重变形，尤其是高度
    //这里多拿一个比例，对高度进行调整，来进行测试
    private int width,height;
    private int adjustHeight;
    private int rotation;
    private double ratio;

    public DecodeQRCodeUtil loadUint8ListData(List<byte[]> bytesList, int imageHeight, int imageWidth,double ratio,
                                              double mean, double std, int rotation){
        //Log.i("bitmap size","byte size " + this.y.length);
        this.bytesList = bytesList;
        this.width = imageWidth;
        this.height = imageHeight;
        this.ratio = ratio;
        this.adjustHeight = (int)(Math.ceil(width / ratio));
        this.rotation = rotation;

        return this;
    }

    public Bitmap getBitmapFromList(){
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

        Bitmap bitmapRaw = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Allocation bmData = renderScriptNV21ToRGBA888(
                contextSoftReference.get(),
                width,
                adjustHeight,
                data);
        bmData.copyTo(bitmapRaw);

        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        bitmapRaw = Bitmap.createBitmap(bitmapRaw, 0, 0, bitmapRaw.getWidth(), bitmapRaw.getHeight(), matrix, true);
        return bitmapRaw;
    }

    public String decodeQRCodeForResult(){

        Map<DecodeHintType,String> hints = new HashMap<DecodeHintType,String>();
        hints.put(DecodeHintType.CHARACTER_SET,"utf-8");
        hints.put(DecodeHintType.TRY_HARDER,Boolean.TRUE.toString());
        hints.put(DecodeHintType.PURE_BARCODE,Boolean.TRUE.toString());

        Bitmap bitmapRaw = convertByteArray2Bitmap();
        Log.i("bitmap size", "  " + bitmapRaw.getWidth());
        Log.i("bitmap size", "  " + bitmapRaw.getHeight());
        Log.i("bitmap size", "  " + bitmapRaw.getByteCount());
        RGBLuminanceSource source=  new RGBLuminanceSource(bitmapRaw);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result result;
        Log.i("bitmap size"," go into try{} catch");
        try {
            result =reader.decode(binaryBitmap,hints);
            Log.i("bitmap size", " get text   "  +  result.toString());
            return result.getText();

        } catch (FormatException e) {
            return "404格式解析错误";
            //e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        return "";

    }




    private Bitmap convertByteArray2Bitmap(){
        ByteBuffer yBuffer = ByteBuffer.wrap(bytesList.get(0));
        ByteBuffer uBuffer = ByteBuffer.wrap(bytesList.get(1));
        ByteBuffer vBuffer = ByteBuffer.wrap(bytesList.get(2));

        int yb = yBuffer.remaining();
        int ub = uBuffer.remaining();
        int vb = vBuffer.remaining();

        byte[] data = new byte[yb + ub + vb];
        yBuffer.get(data,0,yb);
        vBuffer.get(data,yb,vb);
        uBuffer.get(data,yb+vb,ub);
        //YuvImage yuvImage = new YuvImage();
        Bitmap bitmapRaw = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Allocation bmData = renderScriptNV21ToRGBA888(
                contextSoftReference.get(),
                width,height,data
        );

        bmData.copyTo(bitmapRaw);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bitmapRaw = Bitmap.createBitmap(bitmapRaw,0,0,
                bitmapRaw.getWidth(),bitmapRaw.getHeight(),matrix,true);

        return bitmapRaw;
    }

    private Allocation renderScriptNV21ToRGBA888(Context context, int width, int height, byte[] nv21){
        RenderScript renderScript = RenderScript.create(context);
        ScriptIntrinsicYuvToRGB yuvToRGB = ScriptIntrinsicYuvToRGB.create(renderScript, Element.U8_4(renderScript));
        Type.Builder yuvType = new Type.Builder(renderScript,Element.U8(renderScript)).setX(nv21.length);
        Allocation in = Allocation.createTyped(renderScript,yuvType.create(),Allocation.USAGE_SCRIPT);

        Type.Builder rgbaType = new Type.Builder(renderScript, Element.RGBA_8888(renderScript)).setX(width).setY(height);
        Allocation out = Allocation.createTyped(renderScript, rgbaType.create(), Allocation.USAGE_SCRIPT);

        in.copyFrom(nv21);

        yuvToRGB.setInput(in);
        yuvToRGB.forEach(out);

        return out;

    }

}
