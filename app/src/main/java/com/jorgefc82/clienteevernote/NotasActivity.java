package com.jorgefc82.clienteevernote;
/**
 * Created by Jorgefc82.
 */

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.jorgefc82.clienteevernote.adaptadores.AdaptadorListaNotas;

import java.util.ArrayList;
import java.util.List;

public class NotasActivity extends AppCompatActivity {

    private String TAG_GETNOTAS= "GetNotas";
    private String TAG_SNACKBAR= "SnackBar";
    private boolean ordenalfabetico;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Variable que servirá para ordenar la lista siempre se inicializa para orden por fecha
        ordenalfabetico =false;
        FloatingActionButton fabcrearnota = (FloatingActionButton) findViewById(R.id.fab_crearnota);
        /*Botón crear nota para implementar en siguientes commits*/
        fabcrearnota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //Se itentan recuperar notas en onCreate
        getNotas();
    }

    /*Se crea hilo en 2º plano que tratará de recoger notas*/
    private void getNotas() {
        new Thread() {
            @Override
            public void run() {
                try {
                    EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance()
                            .getEvernoteClientFactory().getNoteStoreClient();
                    noteStoreClient.findNotesAsync(new NoteFilter(), 0, 99999,
                            new EvernoteCallback<NoteList>() {
                        @Override
                        public void onSuccess(NoteList result) {
                            getListaDeNotas(result);
                        }
                        @Override
                        public void onException(Exception exception) {
                            Log.e(TAG_GETNOTAS, "Excepción recuperando notas");
                            Snackbar.make(findViewById(android.R.id.content),
                                    R.string.imposible_traer_datos, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.aceptar, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Log.v(TAG_SNACKBAR, "Pulsada acción snackbar!");
                                        }
                                    })
                            .show();
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG_GETNOTAS, "Excepción recuperando notas");
                }
            }
        }
        .start();
    }

    /*Método para recuperar Lista de notas de Evernote*/
    private void getListaDeNotas(NoteList notas) {
        List<Note> listadonotas = notas.getNotes();
        List<String> titulos_notas = new ArrayList<>();
        for(Note nota : listadonotas) {
            titulos_notas.add(nota.getTitle());
            //Log.v(TAG_GETNOTAS, nota.getTitle());
        }
        // Si se han recogido notas se rellena recycler view
        if (!titulos_notas.isEmpty()) {
            /*Se declaran instancias*/
            RecyclerView recycler;
            RecyclerView.Adapter adapter;
            RecyclerView.LayoutManager lManager;
            // Se obtiene el Recycler
            recycler = (RecyclerView) findViewById(R.id.lista_notas);
            recycler.setHasFixedSize(true);
            // Usar un administrador para LinearLayout
            lManager = new LinearLayoutManager(this);
            recycler.setLayoutManager(lManager);
            /* Si orden alfabético es verdadero se cambia el orden
                si no se quedará por defecto ordenado por fecha de la nota
             */
            if (ordenalfabetico) {
                ordenaListaAfabeticamente(titulos_notas);
            }
            // Se crea un nuevo adaptador y se le pasan los títulos de las notas
            adapter = new AdaptadorListaNotas(titulos_notas);
            recycler.setAdapter(adapter);
        }else{
            Snackbar.make(findViewById(android.R.id.content),
                    R.string.lista_notas_vacia, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.aceptar, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.v(TAG_SNACKBAR, "Pulsada acción snackbar!");
                        }
                    })
            .show();
        }
    }
    /*Método para ordenar lista de notas alfabéticamente*/
    private void ordenaListaAfabeticamente (List<String> lista){
        java.util.Collections.sort(lista);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notas, menu);
        return true;
    }

    /*En las opciones del menú desplegable se cambia valor de la variable global
      que determina el orden de la lista y se refresca la lista de notas en cada acción*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ordenporfecha:
                ordenalfabetico=false;
                getNotas();
                return true;
            case R.id.ordenalfabetico:
                ordenalfabetico=true;
                getNotas();
                return true;
            case R.id.refrescar:
                getNotas();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}