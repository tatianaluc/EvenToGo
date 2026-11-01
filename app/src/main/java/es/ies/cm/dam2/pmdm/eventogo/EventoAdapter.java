package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

// ADAPTADOR PROPIO para el ListView (parte 3 de la práctica 8).
// Un adaptador es el "puente" entre la lista de datos (los Evento) y las filas que se
// dibujan en pantalla: el ListView le pregunta "dame la vista de la fila N" y responde getView.
public class EventoAdapter extends ArrayAdapter<Evento> {

    // Dos tonos de azul que se alternan, para ver dónde empieza y termina cada ítem
    private static final int AZUL_CLARO = 0xFFE3F2FD;
    private static final int AZUL_MEDIO = 0xFFBBDEFB;

    public EventoAdapter(Context context, List<Evento> eventos) {
        // El 0 es el layout por defecto, que no usamos porque dibujamos nosotras la fila
        super(context, 0, eventos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // convertView es una fila que ya existía y se puede reciclar (así el scroll va fluido).
        // Solo la inflamos desde el XML si todavía no hay ninguna
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_evento, parent, false);
        }

        // El evento que toca dibujar en esta posición
        Evento evento = getItem(position);

        ImageView imgPortada = convertView.findViewById(R.id.imgPortada);
        TextView txtNombre = convertView.findViewById(R.id.txtNombre);
        TextView txtAsistentes = convertView.findViewById(R.id.txtAsistentes);

        imgPortada.setImageResource(evento.getPortada());
        txtNombre.setText(evento.getNombre());
        txtAsistentes.setText(evento.getAsistentes() + " asistentes");

        // Posición par: azul claro. Posición impar: azul medio
        convertView.setBackgroundColor(position % 2 == 0 ? AZUL_CLARO : AZUL_MEDIO);

        return convertView;
    }
}
