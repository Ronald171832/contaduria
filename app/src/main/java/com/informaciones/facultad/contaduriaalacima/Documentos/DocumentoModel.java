package com.informaciones.facultad.contaduriaalacima.Documentos;

/**
 * Created by Ronald on 02/11/2017.
 */

public class DocumentoModel {

    public String titulo;
    public String linkDeDescarga;
    public int descargas;
    public String fecha;

    public DocumentoModel() {
    }

    public DocumentoModel(String titulo, String linkDeDescarga, int descargas, String fecha) {
        this.titulo = titulo;
        this.linkDeDescarga = linkDeDescarga;
        this.descargas = descargas;
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getLinkDeDescarga() {
        return linkDeDescarga;
    }

    public void setLinkDeDescarga(String linkDeDescarga) {
        this.linkDeDescarga = linkDeDescarga;
    }

    public int getDescargas() {
        return descargas;
    }

    public void setDescargas(int descargas) {
        this.descargas = descargas;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
