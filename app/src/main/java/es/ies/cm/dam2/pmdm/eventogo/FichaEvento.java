package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// FICHA del evento (la de la práctica 4), reutilizada y ampliada.
// Ahora recibe el Evento entero (Serializable) en vez de campos sueltos.
// La abren NuevoEvento (flujo de la práctica 4) y Coleccion (clic corto en una tarjeta).
public class FichaEvento extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ficha_evento);

        // Recogemos el Evento que nos mandan en el Intent
        Evento evento = (Evento) getIntent().getSerializableExtra(Evento.EXTRA);

        // Vistas que ya tenías en la práctica 4
        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvTipo = findViewById(R.id.tvTipo);
        TextView tvCliente = findViewById(R.id.tvCliente);
        TextView tvAsistentes = findViewById(R.id.tvAsistentes);
        TextView tvFecha = findViewById(R.id.tvFecha);
        TextView tvServicios = findViewById(R.id.tvServ);
        TextView tvAV = findViewById(R.id.tvAvs);

        // Vistas NUEVAS: la portada grande, la duración y las estrellas
        ImageView imgPortada = findViewById(R.id.imgPortadaFicha);
        TextView tvDuracion = findViewById(R.id.tvDuracion);
        RatingBar rbValoracion = findViewById(R.id.rbValoracion);

        imgPortada.setImageResource(evento.getPortada());

        tvNombre.setText("Nombre del evento: " + evento.getNombre());
        tvFecha.setText("Fecha de evento: " + evento.getFecha());
        tvTipo.setText("Tipo de evento: " + evento.getTipo());
        tvCliente.setText("Nombre del cliente/empresa: " + evento.getCliente());
        tvAsistentes.setText("Cantidad de asistentes: " + evento.getAsistentes());
        tvServicios.setText("Cantidad de servicios: " + evento.getServicios());
        tvDuracion.setText("Duración: " + evento.getDuracion() + " minutos");
        tvAV.setText("Incluir AVs en presupuesto: " + (evento.isIncluyeAV() ? "Sí" : "No"));

        // RatingBar: las estrellas (0-5). Solo se muestra, el usuario no la cambia
        rbValoracion.setRating(evento.getValoracion());
        rbValoracion.setIsIndicator(true);

        // ATRAS
        Button btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> finish());

        // SIGUIENTE: lleva a la reunión (flujo de la práctica 5)
        Button btnSiguiente = findViewById(R.id.btnSig);
        btnSiguiente.setOnClickListener(v -> startActivity(new Intent(this, Reunion.class)));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fichaEvento), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
