package com.jorgefc82.clienteevernote.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.jorgefc82.clienteevernote.R;

public class DetallesNotaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_nota);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Se recoge información emitida desde el adaptador
        inicializaViews();
    }

    /*Método recoge información proveniente del adaptador y la setea en los textviews*/
    private void inicializaViews() {
        Intent i = this.getIntent();
        Bundle b;
        b = i.getExtras();
        TextView titulo;
        TextView contenido;
        titulo = (TextView) findViewById(R.id.titulodetalles);
        contenido = (TextView) findViewById(R.id.contenidodetalles);
        if (!b.isEmpty()) {
            titulo.setText(b.getString("titulo"));
            contenido.setText(b.getString("contenido"));
        }
    }
}
