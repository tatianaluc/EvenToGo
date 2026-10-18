package es.ies.cm.dam2.pmdm.eventogo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Buscar extends AppCompatActivity {

    // ------------------------------------------------------------------
    // DATOS: todo guardado en arrays de Java, como pide el enunciado
    // ------------------------------------------------------------------

    // Categorías. La posición 0 es un texto de relleno: así sabemos
    // que el usuario no ha elegido nada mientras esté seleccionada la 0
    private final String[] categorias = {
            "Selecciona categoría", "Sonido", "Iluminación", "Audiovisuales"
    };

    // Servicios de cada categoría. El primer índice es la categoría (el mismo
    // orden que el array de arriba) y el segundo es el servicio.
    // Cada fila empieza otra vez con el texto de relleno en la posición 0
    private final String[][] servicios = {
            {"Selecciona servicio"},                                                    // 0: sin categoría
            {"Selecciona servicio", "Equipo pequeño", "Equipo mediano", "DJ con equipo"},        // Sonido
            {"Selecciona servicio", "Luces básicas", "Luces de escenario", "Efectos especiales"}, // Iluminación
            {"Selecciona servicio", "Pantalla LED", "Proyector", "Streaming del evento"}         // Audiovisuales
    };

    // Características de cada servicio, en el MISMO orden que el array servicios,
    // para que servicios[c][s] y caracteristicas[c][s] hablen de lo mismo
    private final String[][] caracteristicas = {
            {""},
            {"", "2 altavoces y 1 micrófono. Hasta 50 personas.",
                    "4 altavoces, mesa de mezclas y 2 micrófonos. Hasta 150 personas.",
                    "DJ, equipo completo y micrófono. 5 horas."},
            {"", "4 focos LED de ambiente.",
                    "Focos móviles y barra LED para el escenario.",
                    "Láser, máquina de humo y bola de espejos."},
            {"", "Pantalla LED de 3x2 m con técnico incluido.",
                    "Proyector con pantalla de 2 m.",
                    "Cámara y retransmisión en directo del evento."}
    };

    // Las 5 ubicaciones de la lista
    private final String[] ubicaciones = {
            "Salón de eventos", "Jardín", "Sala de reuniones", "Carpa", "Local del cliente"
    };

    // Vistas que usamos en varios métodos, por eso están fuera del onCreate
    private Spinner spCategoria;
    private Spinner spServicio;
    private ListView lvUbicacion;
    private Button btnBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscar);

        // Buscamos las vistas del layout
        spCategoria = findViewById(R.id.spCat);
        spServicio = findViewById(R.id.spServicio);
        lvUbicacion = findViewById(R.id.lvUbicacion);
        btnBuscar = findViewById(R.id.btnBuscar);
        Button btnAtras = findViewById(R.id.btnAtras);

        // 1) SPINNER DE CATEGORÍAS: el adapter une el array con el Spinner
        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categorias);
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapterCategorias);

        // 2) SPINNER DE SERVICIOS: al abrir la pantalla todavía no hay categoría
        //    elegida (posición 0), así que solo mostrará el texto de relleno
        rellenarServicios(0);

        // 3) LISTVIEW DE UBICACIONES: cada fila lleva un RadioButton
        ArrayAdapter<String> adapterUbicaciones = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_single_choice, ubicaciones);
        lvUbicacion.setAdapter(adapterUbicaciones);
        // Sin este modo, la lista no recuerda cuál has marcado
        lvUbicacion.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // 4) AL ELEGIR CATEGORÍA: color de texto, rellenar los servicios de esa
        //    categoría (el filtrado) y revisar si ya se puede activar el botón
        spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // "view" es la fila que se ve en el Spinner; le cambiamos el color del texto
                if (view != null) {
                    ((TextView) view).setTextColor(
                            ContextCompat.getColor(Buscar.this, R.color.colorPrimary));
                }
                // position es la categoría elegida: con ella sacamos SUS servicios
                rellenarServicios(position);
                actualizarBoton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // no lo necesitamos, pero la interfaz obliga a escribirlo
            }
        });

        // 5) AL ELEGIR SERVICIO: otro color de texto y revisar el botón
        spServicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    ((TextView) view).setTextColor(
                            ContextCompat.getColor(Buscar.this, R.color.orange));
                }
                actualizarBoton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 6) AL ELEGIR UBICACIÓN: revisar el botón
        lvUbicacion.setOnItemClickListener((parent, view, position, id) -> actualizarBoton());

        // 7) BOTÓN BUSCAR: enseña las características del servicio en un Toast
        btnBuscar.setOnClickListener(v -> {
            int cat = spCategoria.getSelectedItemPosition();
            int serv = spServicio.getSelectedItemPosition();
            String ubicacion = ubicaciones[lvUbicacion.getCheckedItemPosition()];

            String texto = servicios[cat][serv] + " (" + categorias[cat] + ")\n"
                    + caracteristicas[cat][serv] + "\n"
                    + "Ubicación: " + ubicacion;
            Toast.makeText(this, texto, Toast.LENGTH_LONG).show();
        });

        // ATRAS
        btnAtras.setOnClickListener(v -> finish());

        // Estado inicial del botón (desactivado)
        actualizarBoton();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.buscar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // FILTRADO DEPENDIENTE: pone en el Spinner de servicios solo los de la
    // categoría que le pasamos. Al cambiar el adapter, el Spinner vuelve a la posición 0
    private void rellenarServicios(int categoria) {
        ArrayAdapter<String> adapterServicios = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, servicios[categoria]);
        adapterServicios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spServicio.setAdapter(adapterServicios);
    }

    // El botón solo se activa si hay categoría, servicio y ubicación elegidos
    private void actualizarBoton() {
        boolean hayCategoria = spCategoria.getSelectedItemPosition() > 0;  // 0 = texto de relleno
        boolean hayServicio = spServicio.getSelectedItemPosition() > 0;    // 0 = texto de relleno
        boolean hayUbicacion = lvUbicacion.getCheckedItemPosition() != ListView.INVALID_POSITION;

        btnBuscar.setEnabled(hayCategoria && hayServicio && hayUbicacion);
    }
}