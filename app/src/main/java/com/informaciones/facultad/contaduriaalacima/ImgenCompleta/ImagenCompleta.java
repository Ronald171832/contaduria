package com.informaciones.facultad.contaduriaalacima.ImgenCompleta;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.informaciones.facultad.contaduriaalacima.Chat.PasoDeParametros;
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
        url = PasoDeParametros.URL_IMAGEN;
        Glide.with(ImagenCompleta.this).load(url).into(imagen);
        permisoMemoriaInterna();
    }

    final int REQUEST_CODE_GALLERY = 999;

    private void permisoMemoriaInterna() {
        ActivityCompat.requestPermissions(
                ImagenCompleta.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_GALLERY
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void guardar(View view) {
        AlertDialog.Builder cliente = new AlertDialog.Builder(ImagenCompleta.this);
        cliente.setTitle("Guardar");
        cliente.setMessage("Desea Guardar en su Galeria:");
        cliente.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                imagen.buildDrawingCache();
                Bitmap bmap = imagen.getDrawingCache();
                //guardar imagen
                Save savefile = new Save();
                savefile.SaveImage(ImagenCompleta.this, bmap);
            }
        });
        cliente.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        cliente.show();


    }

    public void compartir(View view) {
        AlertDialog.Builder cliente = new AlertDialog.Builder(ImagenCompleta.this);
        cliente.setTitle("Guardar");
        cliente.setMessage("Desea Compartir la Imagen:");
        cliente.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Bitmap bitmap = getBitmapFromView(imagen);
                try {
                    File file = new File(ImagenCompleta.this.getExternalCacheDir(), "compartir.png");
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
        });
        cliente.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        cliente.show();


    }


    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.BLACK);
        }
        view.draw(canvas);
        return returnedBitmap;
    }


}
