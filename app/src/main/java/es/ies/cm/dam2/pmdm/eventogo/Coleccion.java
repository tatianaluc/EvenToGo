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


//Mis eventos con menú de opciones superior y menú contextual
// Editar / Eliminar en cada cardview. Se conservan el recyclerview y los botones Añadir y Mostrar.
public class Coleccion extends AppCompatActivity {

    // Clave del extra con la posición del evento en la lista, ya que empieza en 0, le decimos que -1 sea un evento nuevo
    public static final String EXTRA_POSICION = "posicion";

    private final ArrayList<Evento> eventos = new ArrayList<>();
    private EventosRecyclerAdapter adapter;
    // El launcher es un campo de la clase porque lo usan el botón Añadir, el menú de opciones y el menú contextual  en editar
    private ActivityResultLauncher<Intent> launcher;
    // si la colección se muestra ordenada por nombre o en el orden en que se guardaron
    private boolean ordenadoPorNombre = false;

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
        // Muestra la flecha de volver en la barra de arriba, quitamos boton atras para que se vea mas ordenado el xml
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btnAnadir = findViewById(R.id.btnAnadir);
        Button btnMostrar = findViewById(R.id.btnMostrar);
        RecyclerView rvEventos = findViewById(R.id.rvEventos);

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
                                // evento editado, actualizamos su fila en la bd
                                new EventosDB(this).actualizarEvento(evento);
                                Toast.makeText(this, "Evento actualizado: " + evento.getNombre(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // evento nuevo guardado en la bd, aquí solo avisamos
                                Toast.makeText(this, "Se ha añadido el evento:  " + evento.getNombre(),
                                        Toast.LENGTH_SHORT).show();
                            }
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
            // el orden lo hace la bd con ORDER BY al volver a leer los eventos
            ordenadoPorNombre = true;
            recargarEventos();
            Toast.makeText(this, "Eventos ordenados por nombre", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.opcion_acerca) {
            startActivity(new Intent(this, AcercaDe.class));
            return true;
        } else if (id == android.R.id.home) {
        // La flecha de la barra tiene este id: por defecto y cierra la pantalla, como el botón Atrás
        finish();
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
                    new EventosDB(this).borrarEvento(evento.getId()); // borra su fila en la bd por el id
                    recargarEventos();
                    Toast.makeText(this, "Evento eliminado: " + evento.getNombre(),
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // onResume se ejecuta cada vez que la pantalla vuelve a verse, así la colección siempre está
// actualizada con la bd
    @Override
    protected void onResume() {
        super.onResume();
        recargarEventos();
    }

    // Vacía la lista de eventos y la vuelve a llenar con lo que hay en la bd. Hay que usar la misma
    // porque el adapter la tiene guardada, y luego avisar de que ha cambiado
    private void recargarEventos() {
        eventos.clear();
        eventos.addAll(new EventosDB(this).obtenerEventos(ordenadoPorNombre));
        adapter.notifyDataSetChanged();
    }
}
