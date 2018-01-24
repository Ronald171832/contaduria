package com.informaciones.facultad.contaduriaalacima.GaleriaDeImagenes;

/**
 * Created by Ronald Lopez on 02/11/2017.
 */

public class ImagenModel {

    public String titulo;
    public String link;

    public ImagenModel() {
    }
    public ImagenModel(String titulo, String link) {
        this.titulo = titulo;
        this.link = link;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
