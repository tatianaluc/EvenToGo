package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

// Compartir un evento con los colegas: se elige uno de los eventos del usuario que ha entrado (Spinner),
// se ve el mensaje que se va a mandar y el botón abre el selector de apps del móvil (WhatsApp, correo...)
public class Compartir extends AppCompatActivity {

    // Clave para guardar qué evento estaba elegido en el Spinner cuando se gira la pantalla
    private static final String ESTADO_POSICION = "posicionEvento";

    private Spinner spEvento;
    private TextView tvMensaje;
    private Button btnCompartir;

    // Datos reales (para tener todos los datos del evento) y textos que se enseñan en el Spinner
    private final ArrayList<Evento> misEventos = new ArrayList<>();
    private final ArrayList<String> textos = new ArrayList<>();
    private ArrayAdapter<String> adaptador;

    // Posición que había elegida antes de girar la pantalla (0 si es la primera vez)
    private int posicionGuardada = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_compartir);

        // El id del usuario llega desde Coleccion sin él no sabemos de quién son los eventos
        // si no llega, avisamos y salimos
        int usuarioId = getIntent().getIntExtra(Login.EXTRA_USUARIO_ID, -1);
        if (usuarioId == -1) {
            Toast.makeText(this, getString(R.string.wishlist_sin_usuario), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (savedInstanceState != null) {
            posicionGuardada = savedInstanceState.getInt(ESTADO_POSICION, 0);
        }

        spEvento = findViewById(R.id.spEventoCompartir);
        tvMensaje = findViewById(R.id.tvMensajeCompartir);
        btnCompartir = findViewById(R.id.btnEnviarEv);

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, textos);
        spEvento.setAdapter(adaptador);

        // Cada vez que se elige otro evento en el Spinner, se actualiza el mensaje de la vista previa
        spEvento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int posicion, long id) {
                if (posicion >= 0 && posicion < misEventos.size()) {
                    tvMensaje.setText(mensajeDe(misEventos.get(posicion)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Compartir send con el texto del mensaje. Chooser enseña el selector del móvil con todas
        // las apps que saben recibir texto así no dependemos de que haya una en concreto
        btnCompartir.setOnClickListener(v -> {
            int pos = spEvento.getSelectedItemPosition();
            if (pos < 0 || pos >= misEventos.size()) return; // no hay ningún evento elegido
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, mensajeDe(misEventos.get(pos)));
            startActivity(Intent.createChooser(i, getString(R.string.compartir_chooser)));
        });

        // Botón Atrás cierra esta activity y Android vuelve a la pantalla anterior
        findViewById(R.id.btnAtrasCompartir).setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.share), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cargarEventos(usuarioId);
    }

    // Lee los eventos del usuario en el hilo aparte (Room no deja en el principal) y rellena el Spinner en el principal
    private void cargarEventos(int usuarioId) {
        AppDatabase.ejecutor.execute(() -> {
            List<Evento> lista = AppDatabase.getInstance(this).eventoDao().eventosDePorNombre(usuarioId);
            runOnUiThread(() -> {
                misEventos.clear();
                misEventos.addAll(lista);
                textos.clear();
                for (Evento e : misEventos) {
                    textos.add(e.getNombre() + " (" + e.getFecha() + ")");
                }
                adaptador.notifyDataSetChanged();

                if (misEventos.isEmpty()) {
                    // Sin eventos no hay nada que compartir avisamos y dejamos el botón desactivado
                    tvMensaje.setText(R.string.compartir_sin_eventos);
                    btnCompartir.setEnabled(false);
                } else {
                    btnCompartir.setEnabled(true);
                    // Al girar la pantalla se vuelve a elegir el mismo evento que había
                    if (posicionGuardada < misEventos.size()) {
                        spEvento.setSelection(posicionGuardada);
                    }
                }
            });
        });
    }

    // El mensaje que se manda sale de strings con los datos del evento dentro, así se traduce solo.
    // El tipo y el lugar se guardan en español en la bd y Textos los pasa al idioma del móvil
    private String mensajeDe(Evento e) {
        return getString(R.string.compartir_mensaje, e.getNombre(), Textos.tipo(this, e.getTipo()),
                e.getFecha(), e.getHoraInicio(), e.getHoraFin(), Textos.lugar(this, e.getLugar()),
                e.getAsistentes());
    }

    // Guarda el evento elegido para que no se pierda al girar la pantalla
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ESTADO_POSICION, spEvento.getSelectedItemPosition());
    }
}
