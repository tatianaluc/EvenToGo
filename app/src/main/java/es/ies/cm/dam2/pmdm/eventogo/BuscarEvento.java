package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

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
        adapterResultados = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, resultados);
        lvResultados.setAdapter(adapterResultados);

        // al pulsar un resultado se abre su ficha, como en la colección
        lvResultados.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, FichaEvento.class);
            intent.putExtra(Evento.EXTRA, resultados.get(position));
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

    // Lee los filtros, pregunta a la bd y enseña lo que devuelve
    private void buscar() {
        String nombre = etNombre.getText().toString().trim();
        // qué radiobutton está marcado en el grupo: Empieza por o Contiene
        boolean empiezaPor = rgModo.getCheckedRadioButtonId() == R.id.rbEmpieza;
        // si deja los asistentes mínimos vacíos, se busca con 0 y entran todos
        String textoMin = etMinAsistentes.getText().toString().trim();
        int minAsistentes = textoMin.isEmpty() ? 0 : Integer.parseInt(textoMin);

        resultados.clear();
        resultados.addAll(new EventosDB(this).buscarEventos(nombre, empiezaPor, minAsistentes));
        adapterResultados.notifyDataSetChanged();

        Toast.makeText(this, resultados.size() + " eventos encontrados", Toast.LENGTH_SHORT).show();
    }
}