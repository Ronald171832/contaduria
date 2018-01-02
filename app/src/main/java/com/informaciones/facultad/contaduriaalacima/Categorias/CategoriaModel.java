package com.informaciones.facultad.contaduriaalacima.Categorias;

/**
 * Created by Ronald on 02/11/2017.
 */

public class CategoriaModel {

    public String titulo;
    public String fecha;
    public int visitas;

    public CategoriaModel() {
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public CategoriaModel(String titulo, String fecha, int visitas) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.visitas = visitas;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getVisitas() {
        return visitas;
    }

    public void setVisitas(int visitas) {
        this.visitas = visitas;
    }
}
