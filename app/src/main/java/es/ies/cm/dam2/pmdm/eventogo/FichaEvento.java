package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
        TextView tvHorario = findViewById(R.id.tvHorario);
        TextView tvLugar = findViewById(R.id.tvLugar);

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
        tvDuracion.setText("Duración: " + evento.getDuracion() + " horas");
        tvAV.setText("Incluir AVs en presupuesto: " + (evento.isIncluyeAV() ? "Sí" : "No"));
        tvHorario.setText("Horario: " + evento.getHoraInicio() + " - " + evento.getHoraFin());
        tvLugar.setText("Lugar: " + evento.getLugar());


        // Mostrar las estrellas de complejidad (según los servicios).
        rbValoracion.setRating(evento.getComplejidad());
        rbValoracion.setIsIndicator(true);

        // Presupuesto estimado: cada línea sale de la clase Presupuesto (las tarifas están allí).
        // Solo se enseña en los eventos de una cuenta, no en los ejemplos del catálogo (que no tienen dueño)
        TextView tvPresupuesto = findViewById(R.id.tvPresupuesto);
        if (evento.getUsuarioId() == Evento.SIN_DUENO) {
            tvPresupuesto.setVisibility(View.GONE);
        } else {
            String presupuesto = getString(R.string.presupuesto_titulo) + "\n";
            if (evento.isIncluyeAV()) {
                // con AV: personal + horas extra (si las hay) + montaje + AV
                presupuesto += getString(R.string.presupuesto_personal, Presupuesto.tecnicos(evento),
                        Presupuesto.euros(Presupuesto.jornadas(evento))) + "\n";
                if (Presupuesto.horasExtra(evento) > 0) {
                    presupuesto += getString(R.string.presupuesto_extra, Presupuesto.horasExtra(evento),
                            Presupuesto.euros(Presupuesto.extras(evento))) + "\n";
                }
                presupuesto += getString(R.string.presupuesto_montaje, Presupuesto.euros(Presupuesto.MONTAJE_DESMONTAJE)) + "\n"
                        + getString(R.string.presupuesto_av, Presupuesto.euros(Presupuesto.av(evento))) + "\n";
            } else {
                // sin AV: el cliente solo quiere personal y es una tarifa fija
                presupuesto += getString(R.string.presupuesto_solo_personal,
                        Presupuesto.euros(Presupuesto.TARIFA_SOLO_PERSONAL)) + "\n";
            }
            presupuesto += getString(R.string.presupuesto_total, Presupuesto.euros(Presupuesto.total(evento))) + "\n"
                    + getString(R.string.presupuesto_aviso);
            tvPresupuesto.setText(presupuesto);
        }

        // ATRAS
        Button btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> finish());

        // SIGUIENTE: lleva a la reunión (flujo de la práctica 5)
        Button btnSiguiente = findViewById(R.id.btnSig);
        btnSiguiente.setOnClickListener(v -> {
            Intent i = new Intent(this, Reunion.class);
            // la reunión se guarda en Room ligada a este evento, así que le mandamos su id
            i.putExtra(Reunion.EXTRA_EVENTO_ID, evento.getId());
            startActivity(i);
        });

        // VER REUNIONES: lista las reuniones de este evento
        Button btnVerReuniones = findViewById(R.id.btnVerReuniones);
        btnVerReuniones.setOnClickListener(v -> {
            Intent i = new Intent(this, ListaReuniones.class);
            i.putExtra(Reunion.EXTRA_EVENTO_ID, evento.getId());
            startActivity(i);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fichaEvento), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
