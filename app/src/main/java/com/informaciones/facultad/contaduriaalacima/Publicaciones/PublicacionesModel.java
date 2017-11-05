package com.informaciones.facultad.contaduriaalacima.Publicaciones;

/**
 * Created by Ronald on 02/11/2017.
 */

public class PublicacionesModel {

    public String titulo;
    public String descripcion;
    public String imagen;
    public String[] imagenes;
    public int likes;

    public PublicacionesModel() {
    }

    public PublicacionesModel(String titulo,  String imagen,String descripcion, int likes) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.likes = likes;
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
