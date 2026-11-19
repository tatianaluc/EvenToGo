package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class BuscarEvento extends AppCompatActivity {

    // lista con los resultados,como  el adapter la tiene guardada, usamos esta misma opción
    private final ArrayList<Evento> resultados = new ArrayList<>();
    private ArrayAdapter<Evento> adapterResultados;

    private EditText etNombre;
    private EditText etMinAsistentes;
    private RadioGroup rgModo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscar_evento);

        etNombre = findViewById(R.id.etNombreEvBuscar);
        etMinAsistentes = findViewById(R.id.etMinAsistentes);
        rgModo = findViewById(R.id.rgModo);
        Button btnBuscar = findViewById(R.id.btnBuscar);
        Button btnAtras = findViewById(R.id.btnAtras);
        ListView lvResultados = findViewById(R.id.lvResultados);

        // El adapter enseña cada Evento gracias al to string, que ya tiene nombre, cliente, fecha, horario y lugar
        adapterResultados = new ArrayAdapter<Evento>(this, android.R.layout.simple_list_item_1, resultados) {
            @NonNull
            @Override
            public View getView(int posicion, View convertView, @NonNull ViewGroup parent) {
                TextView fila = (TextView) super.getView(posicion, convertView, parent);
                Evento e = getItem(posicion);
                // el texto sale de strings
                fila.setText(BuscarEvento.this.getString(R.string.evento_resumen, e.getNombre(),
                        Textos.tipo(BuscarEvento.this, e.getTipo()), e.getCliente(), e.getAsistentes(),
                        e.getFecha(), e.getHoraInicio(), e.getHoraFin(), e.getServicios(), e.getDuracion(),
                        Textos.lugar(BuscarEvento.this, e.getLugar())));
                return fila;
            }
        };
        lvResultados.setAdapter(adapterResultados);

        // al pulsar un resultado se abre su ficha, como en la colección
        lvResultados.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, FichaEvento.class);
            intent.putExtra(Evento.EXTRA, resultados.get(position));
            // reenviamos el usuario (si lo hay) a la ficha para el Switch de favorito
            intent.putExtra(Login.EXTRA_USUARIO_ID, getIntent().getIntExtra(Login.EXTRA_USUARIO_ID, -1));
            startActivity(intent);
        });

        btnBuscar.setOnClickListener(v -> buscar());
        btnAtras.setOnClickListener(v -> finish());

        // al abrir la pantalla, sin filtros, se muestran todos los eventos
        buscar();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.buscarEvento), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Lee los filtros, pregunta a Room y enseña lo que devuelve
    private void buscar() {
        String nombre = etNombre.getText().toString().trim();
        // qué radiobutton está marcado en el grupo, si Empieza por o Contiene
        boolean empiezaPor = rgModo.getCheckedRadioButtonId() == R.id.rbEmpieza;
        // si deja los asistentes mínimos vacíos, se busca con 0 y entran todos
        String textoMin = etMinAsistentes.getText().toString().trim();
        int minAsistentes = textoMin.isEmpty() ? 0 : Integer.parseInt(textoMin);

        // El LIKE "Empieza por" es texto%, y "Contiene" es %texto%
        String patron = empiezaPor ? nombre + "%" : "%" + nombre + "%";

        // La consulta a Room va en el hilo aparte y los resultados se muestran en el principal
        AppDatabase.ejecutor.execute(() -> {
            List<Evento> encontrados = AppDatabase.getInstance(this).eventoDao()
                    .buscarCatalogo(patron, minAsistentes); // solo busca en el catálogo
            runOnUiThread(() -> {
                resultados.clear();
                resultados.addAll(encontrados);
                adapterResultados.notifyDataSetChanged();
                Toast.makeText(this, getString(R.string.eventos_encontrados, resultados.size()),
                        Toast.LENGTH_SHORT).show();
            });
        });
    }
}