package com.informaciones.facultad.contaduriaalacima.Informacion;

/**
 * Created by Ronald Lopez on 02/11/2017.
 */

public class InformacionModel {

    public String titulo;
    public String imagen;
    public String fecha;
    public String descripcion;

    public InformacionModel() {
    }

    public InformacionModel(String titulo, String imagen, String descripcion, String fecha) {
        this.titulo = titulo;
        this.imagen = imagen;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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
}
