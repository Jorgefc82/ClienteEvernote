package com.jorgefc82.clienteevernote.adaptadores;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jorgefc82.clienteevernote.R;

import java.util.List;

/**
 * Created by Jorgefc82 on 12/01/2016.
 */
public class AdaptadorListaNotas extends RecyclerView.Adapter<AdaptadorListaNotas.ListaNotasViewHolder> {
    private List<String> items;

    public static class ListaNotasViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public TextView titulo;

        public ListaNotasViewHolder(View v) {
            super(v);
            titulo = (TextView) v.findViewById(R.id.titulo);
        }
    }

    public AdaptadorListaNotas(List<String> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public ListaNotasViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.items_lista, viewGroup, false);
        return new ListaNotasViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ListaNotasViewHolder viewHolder, int i) {
        viewHolder.titulo.setText(items.get(i));
    }
}

