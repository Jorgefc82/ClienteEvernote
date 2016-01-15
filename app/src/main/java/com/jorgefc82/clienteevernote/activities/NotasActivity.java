package com.jorgefc82.clienteevernote.activities;
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
import android.widget.TextView;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.jorgefc82.clienteevernote.R;
import com.jorgefc82.clienteevernote.adaptadores.AdaptadorListaNotas;

public class NotasActivity extends AppCompatActivity {

    private String TAG_GETNOTAS= "Log GetNotas";
    private EvernoteNoteStoreClient noteStoreClient;
    private NoteFilter notefilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Se inicializa variable notestoreclient
        noteStoreClient = EvernoteSession.getInstance()
                .getEvernoteClientFactory().getNoteStoreClient();
        // Se instancia notefilter para establecer filtros en la recogida de los datos
        notefilter = new NoteFilter();
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
        recogeListaDeNotas(notefilter);
    }

    /*Menú de opciones se infla*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notas, menu);
        return true;
    }

    /*Acciones en menú de opciones*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ordenporfecha:
            // se limpia notefilter ya que por defecto ordena por fecha de creación o actualización
                notefilter.clear();
                recogeListaDeNotas(notefilter);
                return true;
            case R.id.ordenalfabetico:
                ordenAlfabetico();
                recogeListaDeNotas(notefilter);
                return true;
            case R.id.refrescar:
                recogeListaDeNotas(notefilter);
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
    private void recogeListaDeNotas(final NoteFilter orden) {
        new Thread() {
            @Override
            public void run() {
                try {
                    noteStoreClient.findNotesAsync(orden, 0, 99999,
                            new EvernoteCallback<NoteList>() {
                        @Override
                        public void onSuccess(NoteList result) {
                            conectaListadeNotas(result);
                        }
                        @Override
                        public void onException(Exception exception) {
                            Log.e(TAG_GETNOTAS, "Excepción recuperando notas");
                            Snackbar exceplista = Snackbar.make(findViewById(android.R.id.content),
                                    R.string.imposible_traer_datos, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.aceptar, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                        }
                                    });
                            lineasSnackBar(exceplista);
                            exceplista.show();
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG_GETNOTAS, "Excepción recuperando notas");
                }
            }
        }.start();
    }

    /*Método que conecta los datos de la lista con el adaptador y se lo pasa al recycler*/
    private void conectaListadeNotas (NoteList notas) {
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
        // Si se han recogido notas se rellenará recycler view
        if (!notas.getNotes().isEmpty()) {
            // Se hace visible el recycler por si se hubiera ocultado previamente
            recycler.setVisibility(View.VISIBLE);
            // Se crea un nuevo adaptador y se le pasa lista de notas
            adapter = new AdaptadorListaNotas(notas);
            recycler.setAdapter(adapter);
        }else{
            /* Si no hay datos en la lista de títulos de se oculta el recyclerView en
                pantalla y se muestra mensaje en snackbar */
            recycler.setVisibility(View.GONE);
            Snackbar snacklistavacia = Snackbar.make(findViewById(android.R.id.content),
                    R.string.lista_notas_vacia, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.aceptar, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                                   }
                    });
            lineasSnackBar(snacklistavacia);
            snacklistavacia.show();
        }
    }

    /*Método para cerrar la sesión actual*/
    private void cerrarSesion (){
        if(EvernoteSession.getInstance().isLoggedIn()){
            EvernoteSession.getInstance().logOut();
        }
    }

    /*Método para aumentar líneas máximas de la snackbar*/
    private void lineasSnackBar (Snackbar snackbar){
        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        // permite 10 líneas como máximo par así evitar que se corten los textos
        textView.setMaxLines(10);
    }

    /* Método establece orden alfabético en noteFilter */
    private void ordenAlfabetico(){
        notefilter.clear();
        notefilter.setAscending(true);
        notefilter.setOrder(5);
    }
}