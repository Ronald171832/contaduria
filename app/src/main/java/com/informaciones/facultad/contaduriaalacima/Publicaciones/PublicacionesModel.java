package com.informaciones.facultad.contaduriaalacima.Publicaciones;

import android.net.Uri;

/**
 * Created by Ronald on 02/11/2017.
 */

public class PublicacionesModel {

    public String titulo;
    public String descripcion;
    public String fecha;
    public Uri[] imagenes;

    public PublicacionesModel() {
    }

    public PublicacionesModel(String titulo, String descripcion,String fecha) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public PublicacionesModel(String titulo, String imagen, String descripcion, int likes, Uri[] imagenesURL,String fecha) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.imagenes=imagenesURL;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Uri[] getImagenes() {
        return imagenes;
    }

    public void setImagenes(Uri[] imagenes) {
        this.imagenes = imagenes;
    }

    // set imagenes
    public void setImagen(int pos,Uri url){
        this.imagenes[pos]=url;
    }

    public Uri getURLImagen(int pos){
        return this.imagenes[pos];
    }
}
