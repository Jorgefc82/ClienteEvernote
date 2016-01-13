package com.jorgefc82.clienteevernote.items;

/**
 * Se prepara clase para manejar los metadatos que se manejarán de cada nota
 * Aún en desuso
 *
 * Created by Jorgefc82 on 12/01/2016.
 */

public class NotaDetalles {
    private String titulo;
    private String descripcion;
    private int fecha;

    public NotaDetalles(String titulo) {
        this.titulo = titulo;
        this.descripcion = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getFecha() {
        return fecha;
    }

    public void setFecha(int fecha) {
        this.fecha = fecha;
    }
}

