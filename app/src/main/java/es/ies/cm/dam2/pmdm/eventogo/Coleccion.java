package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Collections;

//Mis eventos con menú de opciones superior y menú contextual
// Editar / Eliminar en cada cardview. Se conservan el recyclerview y los botones Añadir y Mostrar.
public class Coleccion extends AppCompatActivity {

    // Clave del extra con la posición del evento en la lista, ya que empieza en 0, le decimos que -1 sea un evento nuevo
    public static final String EXTRA_POSICION = "posicion";

    private final ArrayList<Evento> eventos = new ArrayList<>();
    private EventosRecyclerAdapter adapter;

    // El launcher es un campo de la clase porque lo usan el botón Añadir, el menú de opciones y el menú contextual  en editar
    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_coleccion);

        // toolbar superior que la declaramos y se la damos a la activity rellenada, como su
        // barra de acciones con opciones de menu infladas y que acepte o reciba el item seleccionado de ese menú
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mis eventos");

        Button btnAnadir = findViewById(R.id.btnAnadir);
        Button btnMostrar = findViewById(R.id.btnMostrar);
        RecyclerView rvEventos = findViewById(R.id.rvEventos);
        Button btnAtras = findViewById(R.id.btnAtras);

        // Carga  de los 20 eventos de ejemplo
        cargarEjemplos();

        // Aqui hacemos funcionar el rv que contiene el layout manager, el cual permite visualizar un cv debajo de otro
        // y nos traemos el recycler adapter
        rvEventos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventosRecyclerAdapter(this, eventos);
        rvEventos.setAdapter(adapter);


        // El launcher recibe lo que devuelve NuevoEvento, tanto al añadir nuevo evento, como al editar
        //   uno ya existente. Se registra aquí, dentro del oncreate pero fuera de cualquier click
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Evento evento = (Evento) result.getData().getSerializableExtra(Evento.EXTRA);
                        // -1 si es un evento nuevo, como indicamos arriba, o la posición del evento que se ha editado
                        // ya que se le han introducido datos nuevos
                        int posicion = result.getData().getIntExtra(EXTRA_POSICION, -1);

                        if (evento != null) {
                            if (posicion >= 0) {
                                // el evento editado sustituye al antiguo, lo "sobre-escribe"
                                eventos.set(posicion, evento);
                                Toast.makeText(this, "Nombre del evento actualizado: " + evento.getNombre(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                //añadirlo al final
                                eventos.add(evento);
                                Toast.makeText(this, "Se ha añadido el evento:  " + evento.getNombre(),
                                        Toast.LENGTH_SHORT).show();
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

        // Inlcuimos la opción dle botón añadir en el menu de opciones también
        btnAnadir.setOnClickListener(v -> anadirEvento());

        // Btn mostar que enseña u oculta la colección
        btnMostrar.setOnClickListener(v -> {
            if (rvEventos.getVisibility() == View.VISIBLE) {
                rvEventos.setVisibility(View.GONE);
                btnMostrar.setText("Mostrar los eventos");
            } else {
                rvEventos.setVisibility(View.VISIBLE);
                btnMostrar.setText("Ocultar los eventos");
            }
        });

        // ATRAS
        btnAtras.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.coleccion), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Aquí se "infla" el menú de opciones con la información que se lee el XML de res/menu y se pinta en la barra
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_coleccion, menu);
        return true;
    }

    // Aquí se decide qué hacer cuando se pulsa una opción del menú
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.opcion_anadir) {
            anadirEvento();
            return true;
        } else if (id == R.id.opcion_ordenar) {
            // Ordena la colección alfabéticamente por nombre, sin distinguir mayúsculas
            Collections.sort(eventos, (a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()));
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Eventos ordenados por nombre", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.opcion_acerca) {
            startActivity(new Intent(this, AcercaDe.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // apartado de acciones que son llamadas por los botones, el menú de opciones y el menú contextual
    // Abre el formulario en modo alta nuevo evento, sin mandarle ningún evento existente
    private void anadirEvento() {
        launcher.launch(new Intent(this, NuevoEvento.class));
    }

    // Abre el formulario en modo de edición de evento existente y le mandamos el evento actual que quiere editar y su posición en la lista
    public void editarEvento(Evento evento) {
        Intent intent = new Intent(this, NuevoEvento.class);
        intent.putExtra(Evento.EXTRA, evento);
        intent.putExtra(EXTRA_POSICION, eventos.indexOf(evento));
        launcher.launch(intent);
    }

    // Opción de borrar el evneto con doble confirmación en un dialog (ventana modal)
    public void eliminarEvento(Evento evento) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar evento")
                .setMessage("¿Seguro que quieres eliminar \"" + evento.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    eventos.remove(evento);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Evento eliminado: " + evento.getNombre(),
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Los 20 eventos de ejemploq ue pide la practica 9
    // Orden de los datos: nombre, tipo, cliente, asistentes, fecha,
    // nº de servicios (1-10), duración en horas, incluye AV, valoración (0-5)
    private void cargarEjemplos() {
        eventos.add(new Evento("Boda de Noemí y Pablo", "Boda", "Familia Ferrero", 250, "26/09/2027", 6, 480, true, 4.5f));
        eventos.add(new Evento("Festival de Verano", "Concierto", "Ayuntamiento", 800, "18/07/2027", 9, 720, true, 5f));
        eventos.add(new Evento("Convención de Ventas", "Corporativo", "Tecnova", 200, "03/03/2027", 7, 360, true, 4f));
        eventos.add(new Evento("Cumpleaños de Marta", "Cumpleaños", "Marta López", 40, "21/11/2026", 3, 240, false, 3.5f));
        eventos.add(new Evento("Boda en el jardín", "Boda", "Pilar y Dani", 90, "05/09/2027", 5, 420, true, 4f));
        eventos.add(new Evento("Concierto benéfico", "Concierto", "Cruz Roja", 350, "10/12/2026", 6, 300, true, 4.5f));
        eventos.add(new Evento("Presentación de producto", "Corporativo", "Aurea", 150, "14/01/2027", 5, 180, true, 3.5f));
        eventos.add(new Evento("50 cumpleaños de Paco", "Cumpleaños", "Paco Gil", 60, "30/01/2027", 4, 300, false, 4f));
        eventos.add(new Evento("Boda civil", "Boda", "Elena y Marcos", 50, "22/05/2027", 3, 240, false, 5f));
        eventos.add(new Evento("Concierto de jazz", "Concierto", "Sala Azul", 200, "27/02/2027", 5, 240, true, 4f));
        eventos.add(new Evento("Cena de empresa", "Corporativo", "Grupo Norte", 120, "17/12/2026", 4, 240, false, 3f));
        eventos.add(new Evento("Fiesta infantil", "Cumpleaños", "Familia Soto", 30, "09/01/2027", 2, 180, false, 4.5f));
        eventos.add(new Evento("Boda de otoño", "Boda", "Rocío y Álex", 140, "10/10/2027", 7, 540, true, 4.5f));
        eventos.add(new Evento("Gira de verano", "Concierto", "Banda Sur", 500, "25/07/2027", 8, 480, true, 5f));
        eventos.add(new Evento("Jornada de formación", "Corporativo", "Instituto Tec", 80, "08/02/2027", 3, 480, true, 3.5f));
        eventos.add(new Evento("Cumpleaños sorpresa", "Cumpleaños", "Nuria Vega", 25, "14/11/2026", 2, 180, false, 4f));
        eventos.add(new Evento("Aniversario de bodas", "Otro", "Familia Cano", 70, "19/04/2027", 4, 300, false, 4f));
        eventos.add(new Evento("Gala solidaria", "Otro", "Fundación Luz", 260, "05/03/2027", 8, 360, true, 4.5f));
        eventos.add(new Evento("Inauguración de tienda", "Corporativo", "Moda Viva", 110, "12/03/2027", 5, 240, true, 3.5f));
        eventos.add(new Evento("Concierto de Navidad", "Concierto", "Coro Municipal", 300, "20/12/2026", 6, 180, true, 5f));
    }
}
