package com.informaciones.facultad.contaduriaalacima;

/**
 * Created by Ronald on 02/11/2017.
 */

public class ItemLayout {

    public String titulo;
    public String imagen;
    public int visitas;

    public ItemLayout(String titulo, String imagen, int visitas) {
        this.titulo = titulo;
        this.imagen = imagen;
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

    public int getVisitas() {
        return visitas;
    }

    public ItemLayout() {
    }

    public void setVisitas(int visitas) {
        this.visitas = visitas;
    }
}
