package com.jorgefc82.clienteevernote.adaptadores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.jorgefc82.clienteevernote.HandleXML;
import com.jorgefc82.clienteevernote.R;
import com.jorgefc82.clienteevernote.activities.DetallesNotaActivity;

/**
 * Created by Jorgefc82 on 12/01/2016.
 */
public class AdaptadorListaNotas extends RecyclerView.Adapter<AdaptadorListaNotas.ListaNotasViewHolder> {
    private  String TAG_ADAPTADOR = "Log_adaptador";
    private NoteList items;
    private HandleXML parseadorXML;

    public static class ListaNotasViewHolder extends RecyclerView.ViewHolder {
        // View que se setteara en cada cardview
        public TextView titulo;

        public ListaNotasViewHolder(View v) {
            super(v);
            titulo = (TextView) v.findViewById(R.id.titulo);
        }
    }

    public AdaptadorListaNotas(NoteList items) {
        //En el constructor del adaptador se recoge lista desde NotasActivity
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.getNotesSize();
    }

    @Override
    public ListaNotasViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.items_lista, viewGroup, false);
        return new ListaNotasViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ListaNotasViewHolder viewHolder, final int i) {
        // Se establecen títulos en etiquetas de los cardviews
        viewHolder.titulo.setText(items.getNotes().get(i).getTitle());
        // Se implementa método que escucha clicks en cada item del recycler
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            // Al pulsar sobre cada tarjeta se recoge el contenido de la nota relacionada
            @Override
            public void onClick(final View v) {
                EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance()
                        .getEvernoteClientFactory().getNoteStoreClient();
                try {
                    noteStoreClient.getNoteAsync(items.getNotes().get(i).getGuid(),
                            true, true, false, false, new EvernoteCallback<Note>() {
                                @Override
                                public void onSuccess(Note result) {
                    /*Se parsea el string obtenido en formato xml y se parsea para obtener tan solo
                        el texto del cuerpo de la nota
                     */
                                    /*Log.i(TAG_ADAPTADOR, "Recuperando contenido " +
                                            result);*/
                                    String xml_content = result.getContent();
                                    parseadorXML = new HandleXML(xml_content);
                                    parseadorXML.fetchXML();
                                    while(parseadorXML.parseadoCompleto);

                    //Se preparan datos para enviar a pantalla detalles a través de bundle

                                    Intent detalles= new Intent(v.getContext(),
                                            DetallesNotaActivity.class);
                                    Bundle b = new Bundle();
                                    String titulo = items.getNotes().get(i).getTitle();
                                    String contenido = parseadorXML.getDescripcion();
                                    b.putString("titulo", titulo);
                                    b.putString("contenido", contenido);
                                    detalles.putExtras(b);
                                    if (!b.isEmpty()) {
                                        // Limpia la pila de actividades al abrir la nueva
                                        detalles.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        v.getContext().startActivity(detalles);
                                    }
                                }
                                @Override
                                public void onException(Exception exception) {
                                    Log.e(TAG_ADAPTADOR, "Excepción recuperando contenido " +
                                            "de las notas");
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG_ADAPTADOR, "Excepción al hacer click sobre una tarjeta");
                }
            }
        });
    }
}

