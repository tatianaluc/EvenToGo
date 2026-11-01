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
    // Categorías. La posición 0 es un texto de que por defecto se utilizará para saber que
    // el usuario no ha elegido nada mientras esté seleccionada
    private final String[] categorias = {
            "Selecciona categoría", "Sonido", "Iluminación", "Video"
    };

    // Servicios de cada categoría. La posición 0 es un texto de que por defecto se utilizará para saber que
    // el usuario no ha elegido nada mientras esté seleccionada y el segundo es el servicio.
    // Cada fila empieza en la posición 0
    private final String[][] servicios = {
            {"Selecciona servicio"},                                                    // 0: sin categoría
            {"Selecciona servicio", "0-50 personas", "50-90 personas", "+90 personas"}, // Sonido dependiendo de los asistentes
            {"Selecciona servicio", "Pack básico", "Pack intermedio", "Pack completo"},
            {"Selecciona servicio", "Proyector", "Plasmas", "Pantala LED + streaming"}
    };

    // Características de cada servicio, en el mismo orden que el array servicios
    private final String[][] caracteristicas = {
            {""},
            {"", "2 altavoces + 1/2 micrófonos de cable + mesa analógica. ",
                    "4 altavoces + 1/2 micrófonos de cable/inalámbricos + mesa digital.",
                    "6 altavoces + hasta 4 micrófonos inalámbricos + mesa digital."},
            {"", "8 focos LED de ambiente.",
                    "10 focos LED de ambiente + 2 cabezas móviles + mesa.",
                    "16 focos LED de ambiente + 4 cabezas móviles + 2 frontales de escenario + mesa."},
            {"", "Proyector 5000 lúmenes + pantalla de 3x2.20m + portátil. ",
                    "Plasmas de 65'-86' + soporte + portátil.",
                    "Pantalla LED 3.5x2.5m + control de video + streaming (opcional)"}
    };

    // Las 5 ubicaciones de la lista
    private final String[] ubicaciones = {
            "Salón de Eventos Interior", "Zona de Eventos Exterior", "Sala de reuniones", "Carpa", "Local Particular"
    };

    // Vistas que usamos en varios métodos
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

        // spinner de las categorias, el adapter lo une con el array
        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categorias);
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapterCategorias);

        // spinner de servicios, al abrir la pantalla todavía no hay categoría
        // elegida así que solo mostrará el "seleccionar"
        rellenarServicios(0);

        // listview de ubicaciones que cada fila lleva un RadioButton
        ArrayAdapter<String> adapterUbicaciones = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_single_choice, ubicaciones);
        lvUbicacion.setAdapter(adapterUbicaciones);
        // Sin este modo, la lista no recuerda que radiobutton se marcó
        lvUbicacion.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // al elegir categoria, asignamos el color de texto, rellenar los servicios de esa
        // categoría que se van a  escgoer y revisar si ya se puede activar el botón byscar
        spCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // view es la fila que se ve en el Spinner, aqui le cambiamos el color del texto
                if (view != null) {
                    ((TextView) view).setTextColor(
                            ContextCompat.getColor(Buscar.this, R.color.red));
                }
                // position es la categoría elegida y con ella sacamos sus servicios
                rellenarServicios(position);
                actualizarBoton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // el elegir el servicio, que se ponga en otro color de texto y revisar el botón
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

        // al elegir ubicación, revisar que el botón se active
        lvUbicacion.setOnItemClickListener((parent, view, position, id) -> actualizarBoton());

        // el botón buscar aquí enseña las características del servicio en un Toast
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

        // Estado inicial del botón buscar desactivado
        actualizarBoton();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.buscar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // en el Spinner de servicios solo se muestran
    // los de la categoría que le pasamos. Al cambiar el adapter, el Spinner vuelve a 0
    private void rellenarServicios(int categoria) {
        ArrayAdapter<String> adapterServicios = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, servicios[categoria]);
        adapterServicios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spServicio.setAdapter(adapterServicios);
    }

    // El botón solo se activa si hay categoría, servicio y ubicación
    private void actualizarBoton() {
        // 0 = texto de relleno
        boolean hayCategoria = spCategoria.getSelectedItemPosition() > 0;
        boolean hayServicio = spServicio.getSelectedItemPosition() > 0;
        boolean hayUbicacion = lvUbicacion.getCheckedItemPosition() != ListView.INVALID_POSITION;

        btnBuscar.setEnabled(hayCategoria && hayServicio && hayUbicacion);
    }
}