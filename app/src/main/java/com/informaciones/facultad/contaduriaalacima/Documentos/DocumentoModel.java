package com.informaciones.facultad.contaduriaalacima.Documentos;

/**
 * Created by Ronald on 02/11/2017.
 */

public class DocumentoModel {

    public String titulo;
    public String imagen;
    public String linkDeDescarga;
    public int descargas;

    public DocumentoModel() {
    }

    public DocumentoModel(String titulo, String imagen, String linkDeDescarga, int descargas) {
        this.titulo = titulo;
        this.imagen = imagen;
        this.linkDeDescarga = linkDeDescarga;
        this.descargas = descargas;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
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
}
