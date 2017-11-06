package com.informaciones.facultad.contaduriaalacima.Publicaciones;

import android.net.Uri;

/**
 * Created by Ronald on 02/11/2017.
 */

public class PublicacionesModel {

    public String titulo;
    public String descripcion;
    //public String imagen;
    public Uri[] imagenes;
    public int likes;

    public PublicacionesModel() {
    }

    public PublicacionesModel(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.likes=0;
    }

    public PublicacionesModel(String titulo, String imagen, String descripcion, int likes, Uri[] imagenesURL) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        //this.imagen = imagen;
        this.likes = likes;
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

    /*public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }*/

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
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
