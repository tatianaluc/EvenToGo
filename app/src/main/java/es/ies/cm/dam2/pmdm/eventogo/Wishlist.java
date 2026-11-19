package es.ies.cm.dam2.pmdm.eventogo;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

// Lista personal de eventos de cada usuario: se elige un ejemplo del catálogo en el Spinner y se añade a "mi lista".
// Las consultas a la bd (Room para los eventos, SQLite para la wishlist) van en un hilo aparte
// (AppDatabase.ejecutor) y las vistas solo se tocan en el hilo principal
public class Wishlist extends AppCompatActivity {

    private int usuarioId;                       // quién ha hecho login (llega por Intent)
    private EventosDB bd;
    private Spinner spCatalogo;
    private ListView lvWishlist;

    // Datos reales (para saber el id de cada evento) y textos que se enseñan
    private final ArrayList<Evento> catalogo = new ArrayList<>();
    private final ArrayList<Evento> miLista = new ArrayList<>();
    private final ArrayList<String> textosCatalogo = new ArrayList<>();
    private final ArrayList<String> textosLista = new ArrayList<>();
    private ArrayAdapter<String> adaptadorCatalogo;
    private ArrayAdapter<String> adaptadorLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        // El id del usuario viaja entre pantallas; si no llega, avisamos y salimos
        usuarioId = getIntent().getIntExtra(Login.EXTRA_USUARIO_ID, -1);
        if (usuarioId == -1) {
            Toast.makeText(this, getString(R.string.wishlist_sin_usuario), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bd = new EventosDB(this);
        spCatalogo = findViewById(R.id.spCatalogo);
        lvWishlist = findViewById(R.id.lvWishlist);

        adaptadorCatalogo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, textosCatalogo);
        spCatalogo.setAdapter(adaptadorCatalogo);
        adaptadorLista = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, textosLista);
        lvWishlist.setAdapter(adaptadorLista);

        // Añadir el evento elegido en el Spinner a mi lista
        findViewById(R.id.btnAnadirWishlist).setOnClickListener(v -> {
            int pos = spCatalogo.getSelectedItemPosition();
            if (pos < 0 || pos >= catalogo.size()) return; // catálogo vacío
            Evento elegido = catalogo.get(pos);
            AppDatabase.ejecutor.execute(() -> {
                boolean anadido = bd.anadirAWishlist(usuarioId, elegido.getId()); // en el hilo aparte
                runOnUiThread(() -> { // los Toast y las vistas solo desde el hilo principal
                    if (anadido) {
                        Toast.makeText(this, getString(R.string.wishlist_anadido), Toast.LENGTH_SHORT).show();
                        cargarDatos();
                    } else {
                        // el UNIQUE de la tabla ha rechazado el duplicado
                        Toast.makeText(this, getString(R.string.wishlist_duplicado), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        // Pulsación larga sobre un evento de mi lista: lo quita
        lvWishlist.setOnItemLongClickListener((parent, view, position, id) -> {
            Evento quitar = miLista.get(position);
            AppDatabase.ejecutor.execute(() -> {
                bd.quitarDeWishlist(usuarioId, quitar.getId());
                runOnUiThread(() -> {
                    Toast.makeText(this, getString(R.string.wishlist_quitado), Toast.LENGTH_SHORT).show();
                    cargarDatos();
                });
            });
            return true; // true = ya hemos gestionado la pulsación larga
        });

        findViewById(R.id.btnAtrasWishlist).setOnClickListener(v -> finish());

        cargarDatos();
    }

    // Lee el catálogo y mi lista en el hilo aparte y refresca las pantallas en el principal
    private void cargarDatos() {
        AppDatabase.ejecutor.execute(() -> {
            // Los eventos están en Room y la wishlist (solo ids) en SQLite, así que no hay JOIN posible:
            // sacamos los ids de la wishlist y cada evento se busca por su id en Room
            EventoDao dao = AppDatabase.getInstance(this).eventoDao();
            List<Evento> cat = dao.catalogo();
            ArrayList<Evento> mia = new ArrayList<>();
            for (int idEvento : bd.obtenerIdsWishlist(usuarioId)) {
                Evento e = dao.porId(idEvento);
                if (e != null) mia.add(e); // null = el evento se borró después de guardarlo en la lista
            }
            runOnUiThread(() -> {
                catalogo.clear();
                catalogo.addAll(cat);
                miLista.clear();
                miLista.addAll(mia);
                textosCatalogo.clear();
                for (Evento e : catalogo) textosCatalogo.add(textoDe(e));
                textosLista.clear();
                for (Evento e : miLista) textosLista.add(textoDe(e));
                adaptadorCatalogo.notifyDataSetChanged();
                adaptadorLista.notifyDataSetChanged();
            });
        });
    }

    // Lo que se ve de cada evento: nombre y categoría (tipo)
    private String textoDe(Evento e) {
        return e.getNombre() + " (" + e.getTipo() + ")";
    }
}
