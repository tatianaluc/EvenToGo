package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;


//Mis eventos con menú de opciones superior y menú contextual
// Editar / Eliminar en cada cardview. Se conservan el recyclerview y los botones Añadir y Mostrar.
public class Coleccion extends AppCompatActivity {

    // Clave del extra con la posición del evento en la lista, ya que empieza en 0, le decimos que -1 sea un evento nuevo
    public static final String EXTRA_POSICION = "posicion";
    // Claves de lo que se guarda al girar la pantalla
    private static final String ESTADO_ORDENADO = "ordenadoPorNombre";
    private static final String ESTADO_LISTA_VISIBLE = "listaVisible";

    private final ArrayList<Evento> eventos = new ArrayList<>();
    private EventosRecyclerAdapter adapter;
    // El launcher es un campo de la clase porque lo usan el botón Añadir, el menú de opciones y el menú contextual  en editar
    private ActivityResultLauncher<Intent> launcher;
    // si la colección se muestra ordenada por nombre o en el orden en que se guardaron
    private boolean ordenadoPorNombre = false;
    // Id del usuario que ha hecho login y -1 si no llega
    private int usuarioId;
    // Contenedor del menú lateral, es un campo porque lo usan onCreate, el listener de las entradas y el botón Atrás
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_coleccion);
        // Recogemos el usuario que mandó Login con putExtra
        usuarioId = getIntent().getIntExtra(Login.EXTRA_USUARIO_ID, -1);
        // toolbar superior que la declaramos y se la damos a la activity rellenada, como su
        // barra de acciones con opciones de menu infladas y que acepte o reciba el item seleccionado de ese menú
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.nav_mis_eventos);

        // MENÚ LATERAL (Navigation Drawer): el DrawerLayout contiene la pantalla y el NavigationView, que es
        // el menú que sale desde la izquierda con las entradas de menu_drawer.xml
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        // El toggle es el icono de hamburguesa de la barra: abre y cierra el menú y se anima solo.
        // Sustituye a la flecha de volver de antes, ahora volver se hace con el botón Atrás del móvil
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.abrir_menu, R.string.cerrar_menu);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        // Cuando se pulsa una entrada del menú lateral se ejecuta este método
        navigationView.setNavigationItemSelectedListener(this::alPulsarMenuLateral);
        // Si el menú lateral está abierto, el botón Atrás lo cierra en vez de salir de la pantalla
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish(); // menú cerrado: Atrás se comporta como siempre
                }
            }
        });

        Button btnAnadir = findViewById(R.id.btnAnadir);
        Button btnMostrar = findViewById(R.id.btnMostrar);
        RecyclerView rvEventos = findViewById(R.id.rvEventos);

        // Aqui hacemos funcionar el rv que contiene el layout manager, el cual permite visualizar un cv debajo de otro
        // y nos traemos el recycler adapter
        rvEventos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventosRecyclerAdapter(this, eventos);
        rvEventos.setAdapter(adapter);


        // Respuesta del DialogFragment de confirmar borrado: solo llega si el usuario pulsa Eliminar.
        // Se registra aquí en onCreate para que siga funcionando si se gira la pantalla con la ventana abierta
        getSupportFragmentManager().setFragmentResultListener(ConfirmarDialog.CLAVE_BORRAR_EVENTO, this,
                (clave, resultado) -> borrarEvento(
                        resultado.getInt(ConfirmarDialog.RESULTADO_ID),
                        resultado.getString(ConfirmarDialog.RESULTADO_DATO)));

        // Respuesta del diálogo de confirmar al eliminar la cuenta (el id que llega es el del usuario)
        getSupportFragmentManager().setFragmentResultListener(ConfirmarDialog.CLAVE_BORRAR_CUENTA, this,
                (clave, resultado) -> borrarCuenta(resultado.getInt(ConfirmarDialog.RESULTADO_ID)));

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
                                // Room, en el hilo aparte. Al ir en el mismo hilo que la recarga de onResume,
                                // la lista se vuelve a leer ya con el cambio hecho
                                AppDatabase.ejecutor.execute(() ->
                                        AppDatabase.getInstance(this).eventoDao().actualizar(evento));
                                Toast.makeText(this, getString(R.string.evento_actualizado, evento.getNombre()),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // evento nuevo guardado en la bd, aquí solo avisamos
                                Toast.makeText(this, getString(R.string.evento_anadido, evento.getNombre()),
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
                btnMostrar.setText(R.string.mostrar_eventos);
            } else {
                rvEventos.setVisibility(View.VISIBLE);
                btnMostrar.setText(R.string.ocultar_eventos);
            }
        });

        // el saved instance se usa para consrvar los datos en su lugar al girar la pantalla
        // no se usa scrollview porque son listas que ya hacen scroll por si solas
        if (savedInstanceState != null) {
            ordenadoPorNombre = savedInstanceState.getBoolean(ESTADO_ORDENADO, false);
            if (savedInstanceState.getBoolean(ESTADO_LISTA_VISIBLE, false)) {
                rvEventos.setVisibility(View.VISIBLE);
                btnMostrar.setText(R.string.ocultar_eventos);
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.coleccion), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Aquí se "infla" el menú de opciones con la información que se lee el XML de menu y se pinta en la barra
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
            Toast.makeText(this, R.string.eventos_ordenados, Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.opcion_buscar) {
            // abre la búsqueda de eventos en la bd
            abrirCatalogo();
            return true;
        } else if (id == R.id.opcion_wishlist) {
            // pasamos el usuario para que Wishlist sepa de quién es la lista
            Intent i = new Intent(this, Wishlist.class);
            i.putExtra(Login.EXTRA_USUARIO_ID, usuarioId);
            startActivity(i);
            return true;
        } else if (id == R.id.opcion_acerca) {
            startActivity(new Intent(this, AcercaDe.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Cerrar sesión, y volvemos al Main y tood lo que hubiera por encima
    private void cerrarSesion() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }

    // Elimina la cuenta del usuario, utilizando primero el hilo del room y luego toda la info
    // guardada en sqlite, todo en cascada
    private void borrarCuenta(int id) {
        AppDatabase.ejecutor.execute(() -> {
            AppDatabase.getInstance(this).eventoDao().borrarDe(id);
            new EventosDB(this).borrarUsuario(id);
            runOnUiThread(() -> {
                Toast.makeText(this, R.string.cuenta_eliminada, Toast.LENGTH_SHORT).show();
                cerrarSesion(); // ya no hay usuario, volvemos al principio
            });
        });
    }

    // Aquí se decide qué hacer cuando se pulsa una entrada del menú lateral
    private boolean alPulsarMenuLateral(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_anadir) {
            anadirEvento();
        } else if (id == R.id.nav_buscar_evento) {
            abrirCatalogo();
        } else if (id == R.id.nav_wishlist) {
            // pasamos el usuario para que Wishlist sepa de quién es la lista
            Intent i = new Intent(this, Wishlist.class);
            i.putExtra(Login.EXTRA_USUARIO_ID, usuarioId);
            startActivity(i);
        } else if (id == R.id.nav_buscar) {
            startActivity(new Intent(this, Buscar.class));
        } else if (id == R.id.nav_compartir) {
            startActivity(new Intent(this, Compartir.class));
        } else if (id == R.id.nav_acerca) {
            startActivity(new Intent(this, AcercaDe.class));
        } else if (id == R.id.nav_cambiar_clave) {
            // diálogo para cambiar la clave del usuario que ha entrado
            CuentaDialog.nuevo(CuentaDialog.MODO_CAMBIAR_CLAVE, usuarioId)
                    .show(getSupportFragmentManager(), "cambiar_clave");
        } else if (id == R.id.nav_borrar_cuenta) {
            // antes de borrar nada se pide confirmación con el DialogFragment
            ConfirmarDialog.nuevo(
                    ConfirmarDialog.CLAVE_BORRAR_CUENTA,
                    getString(R.string.cuenta_eliminar_titulo),
                    getString(R.string.cuenta_eliminar_mensaje),
                    usuarioId,
                    "")
                    .show(getSupportFragmentManager(), "confirmar_cuenta");
        } else if (id == R.id.nav_salir) {
            cerrarSesion();
        }
        // En todos los casos se cierra el menú
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // apartado de acciones que son llamadas por los botones, el menú de opciones y el menú contextual
    // Abre el formulario en modo alta nuevo evento, sin mandarle ningún evento existente
    private void anadirEvento() {
        Intent intent = new Intent(this, NuevoEvento.class);
        // NuevoEvento necesita saber de quién es el evento que se va a crear
         intent.putExtra(Login.EXTRA_USUARIO_ID, usuarioId);
        launcher.launch(intent);
    }
    // Abre el catálogo de eventos pasando el usuario, así la ficha puede enseñar el Switch de favorito
    private void abrirCatalogo() {
        Intent i = new Intent(this, BuscarEvento.class);
        i.putExtra(Login.EXTRA_USUARIO_ID, usuarioId);
        startActivity(i);
    }

    // Lo usa el adapter para pasar el usuario a la ficha
    public int getUsuarioId() {
        return usuarioId;
    }

    // Abre el formulario en modo de edición de evento existente y le mandamos el evento actual que quiere editar y su posición en la lista
    public void editarEvento(Evento evento) {
        Intent intent = new Intent(this, NuevoEvento.class);
        intent.putExtra(Evento.EXTRA, evento);
        intent.putExtra(EXTRA_POSICION, eventos.indexOf(evento));
        launcher.launch(intent);
    }

    // Opción de borrar el evento con doble confirmación
    public void eliminarEvento(Evento evento) {
        ConfirmarDialog.nuevo(
                ConfirmarDialog.CLAVE_BORRAR_EVENTO,
                getString(R.string.evento_eliminar_titulo),
                getString(R.string.evento_eliminar_mensaje, evento.getNombre()),
                evento.getId(),
                evento.getNombre())
                .show(getSupportFragmentManager(), "confirmar_borrado");
    }

    // Se ejecuta si el usuario ha confirmado en el diálogo. Recibe el id del evento y su nombre
    private void borrarEvento(int id, String nombre) {
        // borra su fila en Room por el id
        // se recarga en el mismo hilo, así ya no sale el evento borrado
        AppDatabase.ejecutor.execute(() ->
                AppDatabase.getInstance(this).eventoDao().borrarPorId(id));
        recargarEventos();
        Toast.makeText(this, getString(R.string.evento_eliminado, nombre), Toast.LENGTH_SHORT).show();
    }

    // esto igual que antes, se eejecuta apra que al girar la pantalla no se pierda el estado de los datos
    @Override
    protected void onSaveInstanceState(@NonNull Bundle salida) {
        super.onSaveInstanceState(salida);
        salida.putBoolean(ESTADO_ORDENADO, ordenadoPorNombre);
        RecyclerView rvEventos = findViewById(R.id.rvEventos);
        salida.putBoolean(ESTADO_LISTA_VISIBLE, rvEventos.getVisibility() == View.VISIBLE);
    }

    // se ejecuta cada vez que la pantalla vuelve a verse, así siempre está actualizada con la bd
    @Override
    protected void onResume() {
        super.onResume();
        recargarEventos();
    }

    // Vuelve a leer de Room los eventos de este usuario y refresca la pantalla. La lectura va en el hilo
    // aparte y refresh en el principal.
    private void recargarEventos() {
        AppDatabase.ejecutor.execute(() -> {
            EventoDao dao = AppDatabase.getInstance(this).eventoDao();
            // pedimos solo los eventos de este usuario, en orden
            List<Evento> lista = ordenadoPorNombre ? dao.eventosDePorNombre(usuarioId) : dao.eventosDe(usuarioId);
            int total = dao.contarDe(usuarioId);
            runOnUiThread(() -> {
                eventos.clear();
                eventos.addAll(lista);
                adapter.notifyDataSetChanged();
                // cantiadd eventos de este usuario (se cuenta en la bbdd)
                getSupportActionBar().setSubtitle(getString(R.string.eventos_registrados, total));
            });
        });
    }
}
