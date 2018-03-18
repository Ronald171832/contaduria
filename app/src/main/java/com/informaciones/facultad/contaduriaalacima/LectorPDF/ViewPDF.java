package com.informaciones.facultad.contaduriaalacima.LectorPDF;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.informaciones.facultad.contaduriaalacima.R;

import java.io.File;


public class ViewPDF extends AppCompatActivity {

    private PDFView visorPDF;
    private File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        visorPDF=(PDFView)findViewById(R.id.pdfView);
        Bundle args=getIntent().getExtras();
        if (args!=null){
            file=new File(args.getString("path",""));
        }
        visorPDF.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();
    }
}
