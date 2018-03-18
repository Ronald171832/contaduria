package com.informaciones.facultad.contaduriaalacima.LectorPDF;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.informaciones.facultad.contaduriaalacima.PantallaPrincipal.GestionSuperUsuario;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Created by user on 12/03/2018.
 */

public class ModelPDF {

    private Context context;
    private File pdfFile;
    private com.itextpdf.text.Document document;
    private PdfWriter pdfWriter;
    private Paragraph parrafo;
    private Font fontTitle=new Font(Font.FontFamily.TIMES_ROMAN,20,Font.BOLD);
    private Font fontSubTitle=new Font(Font.FontFamily.TIMES_ROMAN,18,Font.BOLD);
    private Font fontText=new Font(Font.FontFamily.TIMES_ROMAN,12,Font.BOLD);
    private Font fontHighText=new Font(Font.FontFamily.TIMES_ROMAN,15,Font.BOLD, BaseColor.BLUE);

    public ModelPDF(Context context){
        this.context=context;
    }

    public void openDocument(){
        crearPDF();
        try {
            document=new com.itextpdf.text.Document(PageSize.LETTER);
            pdfWriter=PdfWriter.getInstance(document,new FileOutputStream(pdfFile));
            document.open();
        } catch (Exception e){
            Log.e("openDocument",e.toString());
        }
    }

    public void addMetaData(String titulo,String subject,String autor){
        document.addTitle(titulo);
        document.addSubject(subject);
        document.addAuthor(autor);
    }


    public void addTitulos(String titulo,String subtitulo,String fecha){
        parrafo=new Paragraph();
        addSubParrafo(new Paragraph(titulo,fontTitle));
        addSubParrafo(new Paragraph(subtitulo,fontSubTitle));
        addSubParrafo(new Paragraph("Generado: "+fecha,fontHighText));
        parrafo.setSpacingAfter(30);
        try {
            document.add(parrafo);
        } catch (DocumentException e) {
            Log.e("addTitulos",e.toString());
        }
    }

    public void addParrafo(String texto){
        try{
            parrafo=new Paragraph(texto,fontText);
            parrafo.setSpacingAfter(5);
            parrafo.setSpacingBefore(30);
            document.add(parrafo);
        } catch (Exception e){
            Log.e("addParrafo",e.toString());

        }
    }

    public void crearTabla(String[] headers,List<String[]> detalle){
        parrafo=new Paragraph();
        parrafo.setFont(fontText);
        PdfPTable pdfPTable=new PdfPTable(headers.length);
        pdfPTable.setWidthPercentage(100);
        PdfPCell celda;
        int indexC=0;
        while (indexC<headers.length){
            celda=new PdfPCell(new Phrase(headers[indexC++],fontSubTitle));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setBackgroundColor(BaseColor.BLUE);
            pdfPTable.addCell(celda);
        }
        try {
            pdfPTable.setWidths(new int[]{10,30,20,25,50});
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < detalle.size(); i++) {

            String[] row=detalle.get(i);
            for (int j = 0; j < row.length; j++) {
                celda=new PdfPCell(new Phrase(row[j]));
                celda.setHorizontalAlignment(Element.ALIGN_CENTER);
                celda.setFixedHeight(50);
                pdfPTable.addCell(celda);
            }
        }
        parrafo.add(pdfPTable);
        try {
            document.add(parrafo);
        } catch (DocumentException e) {
            Log.e("crearTabla: ",e.toString());
        }
    }

    private void addSubParrafo(Paragraph parrafoHijo) {
        parrafoHijo.setAlignment(Element.ALIGN_CENTER);
        parrafo.add(parrafoHijo);
    }

    private void crearPDF(){
        File folder=new File(Environment.getExternalStorageDirectory().toString(),"Contaduria Listado Alumnos");
        if (!folder.exists()){
            folder.mkdirs();
        }
        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String fechaHora = hourdateFormat.format(date);
        String nombrePDF="Listado_Estudiantes_Fac_Contaduria_"+fechaHora+".pdf";
        pdfFile=new File(folder,nombrePDF);

    }

    public void closeDocument() {
        if (document.isOpen()) {
            document.close();
        }
    }

    public void viewPDF(){
        Intent intent=new Intent(context, ViewPDF.class);
        intent.putExtra("path",pdfFile.getAbsolutePath());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    public void appVidewPDF(Activity activity){
        if (pdfFile.exists()){
            Uri uri=Uri.fromFile(pdfFile);
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri,"application/pdf");
            try {
                activity.startActivity(intent);
            } catch (ActivityNotFoundException e){
                activity.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=com.adobe.reader&hl=es")));
                Toast.makeText(activity.getApplicationContext(),"NO cuenta con una aplicacion capaz de leer el archivo PDF",Toast.LENGTH_LONG).show();
            }
        }

    }

}
