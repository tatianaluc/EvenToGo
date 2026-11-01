package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// ADAPTADOR del RecyclerView, versión de la práctica 11.
// Cambios respecto a la 9: el clic largo ya NO borra, ahora abre un menú pequeño
// (PopupMenu) con Editar y Eliminar. Como la edición y el borrado los hace la pantalla
// (Coleccion), el adaptador recibe la propia Coleccion para poder avisarla.
public class EventosRecyclerAdapter
        extends RecyclerView.Adapter<EventosRecyclerAdapter.EventoViewHolder> {

    private final Coleccion coleccion;     // la pantalla en la que estamos
    private final List<Evento> eventos;    // la colección

    public EventosRecyclerAdapter(Coleccion coleccion, List<Evento> eventos) {
        this.coleccion = coleccion;
        this.eventos = eventos;
    }

    // 1) Crea una tarjeta nueva a partir del XML
    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(coleccion)
                .inflate(R.layout.item_evento_card, parent, false);
        return new EventoViewHolder(vista);
    }

    // 2) Rellena una tarjeta con los datos del evento que toca
    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        Evento evento = eventos.get(position);

        holder.imgPortada.setImageResource(evento.getPortada());
        holder.txtNombre.setText(evento.getNombre());
        holder.txtAsistentes.setText(evento.getAsistentes() + " asistentes");

        // CLIC CORTO: abrimos la ficha con el evento entero (igual que en la 9)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(coleccion, FichaEvento.class);
            intent.putExtra(Evento.EXTRA, evento);
            coleccion.startActivity(intent);
        });

        // CLIC LARGO: MENÚ CONTEXTUAL con Editar y Eliminar
        holder.itemView.setOnLongClickListener(v -> {
            // El menú sale pegado a la tarjeta pulsada (v)
            PopupMenu menu = new PopupMenu(coleccion, v);
            menu.getMenuInflater().inflate(R.menu.menu_contextual, menu.getMenu());

            menu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.ctx_editar) {
                    coleccion.editarEvento(evento);
                    return true;
                } else if (id == R.id.ctx_eliminar) {
                    coleccion.eliminarEvento(evento);
                    return true;
                }
                return false;
            });

            menu.show();
            return true;   // true = "el clic largo ya lo he gestionado yo"
        });
    }

    // 3) Cuántas tarjetas hay en total
    @Override
    public int getItemCount() {
        return eventos.size();
    }

    // El ViewHolder guarda las vistas de UNA tarjeta
    static class EventoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPortada;
        TextView txtNombre;
        TextView txtAsistentes;

        EventoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPortada = itemView.findViewById(R.id.imgPortada);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtAsistentes = itemView.findViewById(R.id.txtAsistentes);
        }
    }
}
