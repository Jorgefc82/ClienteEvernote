package com.jorgefc82.clienteevernote;
/**
 * Created by Jorgefc82.
 */

import android.content.Intent;
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
    private EvernoteNoteStoreClient noteStoreClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Se inicializa variable notestoreclient
        noteStoreClient = EvernoteSession.getInstance()
                .getEvernoteClientFactory().getNoteStoreClient();
        // Variable que servirá para ordenar la lista siempre se inicializa para orden por fecha
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
        gestionaListaDeNotas();
    }

    /*Menú de opciones se infla*/
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
                gestionaListaDeNotas();
                return true;
            case R.id.ordenalfabetico:
                ordenalfabetico=true;
                gestionaListaDeNotas();
                return true;
            case R.id.refrescar:
                gestionaListaDeNotas();
                return true;
            case R.id.cerrarsesion:
                //Cierra sesión y vuelve a conducir a pantalla de login
                cerrarSesion();
                this.finish();
                Intent login = new Intent(this, LoginActivity.class);
                login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                this.startActivity(login);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*Método crea hilo en 2º plano que tratará de recoger lista  de notas de Evernote*/
    private void gestionaListaDeNotas() {
        new Thread() {
            @Override
            public void run() {
                try {
                    noteStoreClient.findNotesAsync(new NoteFilter(), 0, 99999,
                            new EvernoteCallback<NoteList>() {
                        @Override
                        public void onSuccess(NoteList result) {
                            gestionaListaDeNotas(result);
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
        }.start();
    }

    /*Método que gestionará los datos obtenidos de la lista de notas y como se dibujarán en la
     interfaz*/
    private void gestionaListaDeNotas(NoteList notas) {
        /*Se declaran instancias*/
        RecyclerView recycler;
        RecyclerView.Adapter adapter;
        RecyclerView.LayoutManager lManager;
        // Se obtiene el Recycler
        recycler = (RecyclerView) findViewById(R.id.lista_notas);
        recycler.setHasFixedSize(true);
        // Se usa un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);
        // Se declara array de Notes
        List<Note> listadonotas = notas.getNotes();
        // Se declara array de Strings que recogerá los títulos
        List<String> titulos_notas = new ArrayList<>();
        for(Note nota : listadonotas) {
            titulos_notas.add(nota.getTitle());
        }
        // Si se han recogido notas se rellenará recycler view con títulos
        if (!titulos_notas.isEmpty()) {
            // Se hace visible el recycler por si se hubiera ocultado previamente
            recycler.setVisibility(View.VISIBLE);
            /* Si orden alfabético es verdadero se cambia el orden
                si no se quedará por defecto ordenado por fecha de la nota */
            if (ordenalfabetico) {
                ordenaListaAfabeticamente(titulos_notas);
            }
            // Se crea un nuevo adaptador y se le pasan los títulos de las notas
            adapter = new AdaptadorListaNotas(titulos_notas);
            recycler.setAdapter(adapter);
        }else{
            /* Si no hay datos en la lista de títulos de se oculta el recyclerView en
                pantalla y se muestra mensaje en snackbar */
            recycler.setVisibility(View.GONE);
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

    /*Método para cerrar la sesión actual*/
    private void cerrarSesion (){
        if(EvernoteSession.getInstance().isLoggedIn()){
            EvernoteSession.getInstance().logOut();
        }
    }
}