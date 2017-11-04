package com.informaciones.facultad.contaduriaalacima.Categorias;

/**
 * Created by Ronald on 02/11/2017.
 */

public class CategoriaModel {

    public String titulo;
    public String imagen;
    public String descripcion;
    public int visitas;

    public CategoriaModel() {
    }

    public CategoriaModel(String titulo, String imagen, String descripcion, int visitas) {
        this.titulo = titulo;
        this.imagen = imagen;
        this.descripcion = descripcion;
        this.visitas = visitas;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getVisitas() {
        return visitas;
    }

    public void setVisitas(int visitas) {
        this.visitas = visitas;
    }
}
