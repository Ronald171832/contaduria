package com.informaciones.facultad.contaduriaalacima.ImgenCompleta;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.informaciones.facultad.contaduriaalacima.R;

import java.io.File;
import java.io.FileOutputStream;

public class ImagenCompleta extends AppCompatActivity {
    String url;
    ImageView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagen_completa);
        imagen = (ImageView) findViewById(R.id.iv_completa);
        url = getIntent().getStringExtra("url");
        Glide.with(ImagenCompleta.this).load(url).into(imagen);
    }

    public void guardar(View view){
        imagen.buildDrawingCache();
        Bitmap bmap = imagen.getDrawingCache();
        //guardar imagen
        Save savefile = new Save();
        savefile.SaveImage(ImagenCompleta.this, bmap);
    }
    public void compartir(View view){
        Bitmap bitmap =getBitmapFromView(imagen);
        try {
            File file = new File(this.getExternalCacheDir(),"compartir.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Compartir imagen via:"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.BLACK);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

}
