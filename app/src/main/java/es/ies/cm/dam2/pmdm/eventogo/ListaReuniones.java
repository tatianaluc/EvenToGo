package es.ies.cm.dam2.pmdm.eventogo;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

// Lista las reuniones de un evento.
// Pulsación larga sobre una reunión para marcarla como aceptada, cancelada o eliminarla
public class ListaReuniones extends ActividadConBarra {

    private int eventoId; // el evento del que enseñamos las reuniones (llega por Intent)
    // La lista que tiene guardada el adapter, así que siempre se rellena esta misma
    private final ArrayList<ReunionEvento> reuniones = new ArrayList<>();
    private ArrayAdapter<ReunionEvento> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_reuniones);

        eventoId = getIntent().getIntExtra(Reunion.EXTRA_EVENTO_ID, -1);

        ListView lvReuniones = findViewById(R.id.lvReuniones);
        TextView txtSinReuniones = findViewById(R.id.txtSinReuniones);
        // El ArrayAdapter enseña cada reunión con su to string
        // monta el texto de cada fila aquí para poder usar strings y traducir
        adaptador = new ArrayAdapter<ReunionEvento>(this, android.R.layout.simple_list_item_1, reuniones) {
            @NonNull
            @Override
            public View getView(int posicion, View convertView, @NonNull ViewGroup parent) {
                TextView fila = (TextView) super.getView(posicion, convertView, parent);
                ReunionEvento r = getItem(posicion);
                fila.setText(ListaReuniones.this.getString(R.string.reunion_texto, r.fecha,
                        Textos.modalidad(ListaReuniones.this, r.modalidad), r.cliente,
                        Textos.estado(ListaReuniones.this, r.estado)));
                return fila;
            }
        };
        lvReuniones.setAdapter(adaptador);
        // Si la lista está vacía se enseña este texto en su lugar
        lvReuniones.setEmptyView(txtSinReuniones);

        lvReuniones.setOnItemLongClickListener((parent, view, position, id) -> {
            mostrarOpciones(reuniones.get(position));
            return true; // true = gestionando la pulsación larga
        });

        findViewById(R.id.btnAtrasReuniones).setOnClickListener(v -> finish());

        // Respuesta del Dialog de confirmar borrado
        getSupportFragmentManager().setFragmentResultListener(ConfirmarDialog.CLAVE_BORRAR_REUNION, this,
                (clave, resultado) -> {
                    int idReunion = resultado.getInt(ConfirmarDialog.RESULTADO_ID);
                    AppDatabase.ejecutor.execute(() -> {
                        AppDatabase.getInstance(this).reunionDao().borrarPorId(idReunion);
                        runOnUiThread(this::cargarReuniones); // se vuelve a leer para que desaparezca de la lista
                    });
                });
    }

    // Cada vez que se vuelve a ver la pantalla se lee otra vez de Room
    @Override
    protected void onResume() {
        super.onResume();
        cargarReuniones();
    }

    // Lee las reuniones del evento en el hilo aparte y refresca la lista en el principal
    private void cargarReuniones() {
        AppDatabase.ejecutor.execute(() -> {
            List<ReunionEvento> lista = AppDatabase.getInstance(this).reunionDao().reunionesDe(eventoId);
            runOnUiThread(() -> {
                reuniones.clear();
                reuniones.addAll(lista);
                adaptador.notifyDataSetChanged();
            });
        });
    }

    // Ventana con las tres cosas que se pueden hacer con una reunión
    private void mostrarOpciones(ReunionEvento reunion) {
        String[] opciones = {
                getString(R.string.reunion_marcar_aceptada),
                getString(R.string.reunion_marcar_cancelada),
                getString(R.string.reunion_eliminar)};
        new AlertDialog.Builder(this)
                .setTitle(R.string.reunion_opciones)
                .setItems(opciones, (dialog, cual) -> {
                    if (cual == 2) {
                        // Eliminar pide confirmación con el Dialog antes de borrar nada
                        ConfirmarDialog.nuevo(
                                ConfirmarDialog.CLAVE_BORRAR_REUNION,
                                getString(R.string.reunion_eliminar),
                                getString(R.string.reunion_eliminar_mensaje),
                                reunion.id,
                                "")
                                .show(getSupportFragmentManager(), "confirmar_reunion");
                        return;
                    }
                    // Las otras dos opciones son un update del estado
                    AppDatabase.ejecutor.execute(() -> {
                        ReunionDao dao = AppDatabase.getInstance(this).reunionDao();
                        if (cual == 0) {
                            dao.cambiarEstado(reunion.id, ReunionEvento.ACEPTADA);
                        } else {
                            dao.cambiarEstado(reunion.id, ReunionEvento.CANCELADA);
                        }
                        runOnUiThread(this::cargarReuniones); // y se vuelve a leer para que se vea el cambio
                    });
                })
                .show();
    }
}
