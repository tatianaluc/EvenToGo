package es.ies.cm.dam2.pmdm.eventogo;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

// Lista las reuniones de un evento (la parte N de la relación 1-N, guardada en Room).
// Pulsación larga sobre una reunión: marcarla como aceptada o cancelada (update) o eliminarla (delete)
public class ListaReuniones extends AppCompatActivity {

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
        // El ArrayAdapter enseña cada reunión con su toString()
        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reuniones);
        lvReuniones.setAdapter(adaptador);
        // Si la lista está vacía se enseña este texto en su lugar
        lvReuniones.setEmptyView(txtSinReuniones);

        lvReuniones.setOnItemLongClickListener((parent, view, position, id) -> {
            mostrarOpciones(reuniones.get(position));
            return true; // true = ya hemos gestionado la pulsación larga
        });

        findViewById(R.id.btnAtrasReuniones).setOnClickListener(v -> finish());
    }

    // Cada vez que se vuelve a ver la pantalla se lee otra vez de Room (por si se ha guardado una reunión nueva)
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
                    AppDatabase.ejecutor.execute(() -> {
                        ReunionDao dao = AppDatabase.getInstance(this).reunionDao();
                        if (cual == 0) {
                            dao.cambiarEstado(reunion.id, ReunionEvento.ACEPTADA);
                        } else if (cual == 1) {
                            dao.cambiarEstado(reunion.id, ReunionEvento.CANCELADA);
                        } else {
                            dao.borrarPorId(reunion.id);
                        }
                        runOnUiThread(this::cargarReuniones); // y se vuelve a leer para que se vea el cambio
                    });
                })
                .show();
    }
}
