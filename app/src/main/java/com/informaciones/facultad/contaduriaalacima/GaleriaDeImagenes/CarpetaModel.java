package com.informaciones.facultad.contaduriaalacima.GaleriaDeImagenes;

/**
 * Created by Ronald Lopez on 02/11/2017.
 */

public class CarpetaModel {

    public String titulo;
    public String fecha;


    public CarpetaModel() {
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public CarpetaModel(String titulo, String fecha) {
        this.titulo = titulo;
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
