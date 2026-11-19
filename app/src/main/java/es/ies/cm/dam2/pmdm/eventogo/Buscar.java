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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;

public class Buscar extends ActividadConBarra {
    // Categorías. La posición 0 es un texto que por defecto se utilizará para saber que
    // el usuario no ha elegido nada mientras esté seleccionada.
    // Los textos están en strings y se cargan en onCreate
    private String[] categorias;

    // Servicios de cada categoría. La posición 0 es el texto por defecto "Selecciona servicio".
    // Cada fila empieza en la posición 0
    private String[][] servicios;

    // Características de cada servicio, en el mismo orden que el array servicios
    private String[][] caracteristicas;

    // Las 5 ubicaciones de la lista son los mismos lugares del evento, sin el "Selecciona"
    private String[] ubicaciones;

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

        // Cargamos los textos desde strings, así cambian solos con el idioma del dispositivo
        categorias = getResources().getStringArray(R.array.buscar_categorias);
        servicios = new String[][]{
                getResources().getStringArray(R.array.buscar_servicios_0), // 0: sin categoría
                getResources().getStringArray(R.array.buscar_servicios_1), // Sonido dependiendo de los asistentes
                getResources().getStringArray(R.array.buscar_servicios_2), // Iluminación
                getResources().getStringArray(R.array.buscar_servicios_3)  // Video
        };
        caracteristicas = new String[][]{
                getResources().getStringArray(R.array.buscar_caract_0),
                getResources().getStringArray(R.array.buscar_caract_1),
                getResources().getStringArray(R.array.buscar_caract_2),
                getResources().getStringArray(R.array.buscar_caract_3)
        };
        String[] lugares = getResources().getStringArray(R.array.lugares);
        ubicaciones = Arrays.copyOfRange(lugares, 1, lugares.length); // quitamos el "Selecciona" de la posición 0

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

        // listview de ubicaciones, que cada fila lleva un RadioButton
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

            String texto = getString(R.string.buscar_toast,
                    servicios[cat][serv], categorias[cat], caracteristicas[cat][serv], ubicacion);
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