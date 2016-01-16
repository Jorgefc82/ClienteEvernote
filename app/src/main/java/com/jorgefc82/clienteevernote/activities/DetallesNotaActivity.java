package com.jorgefc82.clienteevernote.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.EvernoteUtil;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.type.Note;
import com.jorgefc82.clienteevernote.R;

public class DetallesNotaActivity extends AppCompatActivity {
    /* Se declaran como globales los componentes que serán comunes tanto para vista crear nota
    como para vista visualizar nota*/
    private TextView titulo;
    private TextView contenido;
    private Toolbar toolbar;
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_nota);
        toolbar = (Toolbar) findViewById(R.id.toolbar_detalles_notas);
        titulo = (TextView) findViewById(R.id.titulodetalles);
        contenido = (TextView) findViewById(R.id.contenidodetalles);
        i = this.getIntent();
        /*Se comprueba desde que actividad ha sido lanzada la actual y se inicializa la vista
        para cada caso*/
        inicializaViews();
    }

    /*Método recoge información proveniente del adaptador y la setea en los textviews*/
    private void inicializaViews() {
        // Se recogen datos
        Bundle b;
        b = i.getExtras();
        /* Si se ha lanzado la actividad desde crear nota se hacen visibles los componentes
        necesarios para crearla y se cambia titulo en toolbar */
        if (b.getBoolean("crearnota")){
            crearNota();
        /* Si no se deja el modo en vista detalles de la nota, se  recoge información proveniente
         del adaptador y la setea en los textviews*/
        }else{
            toolbar.setTitle(R.string.title_activity_detalles_nota);
            setSupportActionBar(toolbar);
            titulo.setText(b.getString("titulo"));
            contenido.setText(b.getString("contenido"));
        }
    }

    /*Método que crea nota*/
    private void crearNota() {
        // Se incializan y setean componentes de la interfaz necesarios para crear nota
        titulo.setText(R.string.cabecera_nota);
        contenido.setText(R.string.contenido_nota);
        toolbar.setTitle(R.string.crear_nota);
        final EditText edita_titulo = (EditText) findViewById(R.id.editText_titulo);
        final EditText edita_contenido = (EditText) findViewById(R.id.editText_contenido);
        edita_titulo.setVisibility(View.VISIBLE);
        edita_contenido.setVisibility(View.VISIBLE);
        //Para cambiar título setSupportActionBar debe ir después de setTitle
        setSupportActionBar(toolbar);
        FloatingActionButton fabguardarnota = (FloatingActionButton) findViewById(R.id.fab_guardarnota);
        fabguardarnota.setVisibility(View.VISIBLE);
        // Se llama a guardar nota a través del botón flotante
        fabguardarnota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardaNota(edita_titulo,edita_contenido,DetallesNotaActivity.this);
            }
        });
    }

    /*Método que guarda la nota en NoteStore del cliente*/
    private void guardaNota (EditText editatitulo, EditText  editacontenido, final Activity activity){
        final String TAG_GUARDARNOTA= "Guarda notas";
        // Se requiere campo título para guardar nota, si no se muestra toast
        if(String.valueOf(editatitulo.getText()).equals("")){
            Toast.makeText(getApplicationContext(), getString(R.string.no_se_puede_guardar),
                    Toast.LENGTH_LONG).show();
        }else{
            EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().
                    getEvernoteClientFactory().getNoteStoreClient();
            Note note = new Note();
            note.setTitle(String.valueOf(editatitulo.getText()));
            note.setContent(EvernoteUtil.NOTE_PREFIX + (String.valueOf(editacontenido.getText()))
                    + EvernoteUtil.NOTE_SUFFIX);

            noteStoreClient.createNoteAsync(note, new EvernoteCallback<Note>() {
                @Override
                public void onSuccess(Note result) {
                    Toast.makeText(getApplicationContext(), result.getTitle() + " "
                            + getString(R.string.nota_creada), Toast.LENGTH_LONG).show();
            /*Se finaliza la actividad una vez guardada la nota y se devuelve resultado true a
            NotasActivity para que se actualice la vista con la nueva nota creada*/
                    i.putExtra("RESULTADO", true);
                    setResult(RESULT_OK, i);
                    activity.finish();
                }

                @Override
                public void onException(Exception exception) {
                    Log.e(TAG_GUARDARNOTA, "Error creando nota", exception);
                    Toast.makeText(getApplicationContext(), R.string.error_crea_nota,
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
