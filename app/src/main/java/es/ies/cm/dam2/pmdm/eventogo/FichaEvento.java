package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

// FICHA del evento
// recibe el Evento entero (Serializable) en vez de campos sueltos.
// La abren NuevoEvento y Coleccion.
public class FichaEvento extends ActividadConBarra {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ficha_evento);

        // Recogemos el Evento que nos mandan en el Intent
        Evento evento = (Evento) getIntent().getSerializableExtra(Evento.EXTRA);

        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvTipo = findViewById(R.id.tvTipo);
        TextView tvCliente = findViewById(R.id.tvCliente);
        TextView tvAsistentes = findViewById(R.id.tvAsistentes);
        TextView tvFecha = findViewById(R.id.tvFecha);
        TextView tvServicios = findViewById(R.id.tvServ);
        TextView tvAV = findViewById(R.id.tvAvs);
        TextView tvHorario = findViewById(R.id.tvHorario);
        TextView tvLugar = findViewById(R.id.tvLugar);
        ImageView imgPortada = findViewById(R.id.imgPortadaFicha);
        TextView tvDuracion = findViewById(R.id.tvDuracion);
        RatingBar rbValoracion = findViewById(R.id.rbValoracion);

        imgPortada.setImageResource(evento.getPortada());

        // Cada texto sale de strings con su dato numérico dentro.
        tvNombre.setText(getString(R.string.ficha_nombre, evento.getNombre()));
        tvFecha.setText(getString(R.string.ficha_fecha, evento.getFecha()));
        tvTipo.setText(getString(R.string.ficha_tipo, Textos.tipo(this, evento.getTipo())));
        tvCliente.setText(getString(R.string.ficha_cliente, evento.getCliente()));
        tvAsistentes.setText(getString(R.string.ficha_asistentes, evento.getAsistentes()));
        tvServicios.setText(getString(R.string.ficha_servicios, evento.getServicios()));
        tvDuracion.setText(getString(R.string.ficha_duracion, evento.getDuracion()));
        tvAV.setText(getString(R.string.ficha_av,
                getString(evento.isIncluyeAV() ? R.string.respuesta_si : R.string.respuesta_no)));
        tvHorario.setText(getString(R.string.ficha_horario, evento.getHoraInicio(), evento.getHoraFin()));
        tvLugar.setText(getString(R.string.ficha_lugar, Textos.lugar(this, evento.getLugar())));


        // Mostrar las estrellas de complejidad (según los servicios).
        rbValoracion.setRating(evento.getComplejidad());
        rbValoracion.setIsIndicator(true);

        // cada línea del presu estimado sale de la clase Presupuesto.
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

        // SWITCH DE FAVORITO, si esta marcado, el evento está en la lista personal del usuario.
        // -1 no hay usuario ni lista personal y escondemos el Switch
        int usuarioId = getIntent().getIntExtra(Login.EXTRA_USUARIO_ID, -1);
        MaterialSwitch swFavorito = findViewById(R.id.swFavorito);
        if (usuarioId == -1) {
            swFavorito.setVisibility(View.GONE);
        } else {
            EventosDB bd = new EventosDB(this);
            // Primero miramos en el hilo aparte si ya es favorito, y se pone el estado en el principal
            AppDatabase.ejecutor.execute(() -> {
                boolean esFavorito = bd.obtenerIdsWishlist(usuarioId).contains(evento.getId());
                runOnUiThread(() -> {
                    swFavorito.setChecked(esFavorito);
                    // El listener se pone después del estado inicial, si no se ejecutaría solo al marcarlo
                    swFavorito.setOnCheckedChangeListener((boton, marcado) ->
                            AppDatabase.ejecutor.execute(() -> {
                                if (marcado) {
                                    bd.anadirAWishlist(usuarioId, evento.getId()); // añade a la lista
                                } else {
                                    bd.quitarDeWishlist(usuarioId, evento.getId()); // quita de la lista
                                }
                            }));
                });
            });
        }

        // ATRAS
        Button btnAtras = findViewById(R.id.btnAtras);
        btnAtras.setOnClickListener(v -> finish());

        // SIGUIENTE lleva a la reunión
        Button btnSiguiente = findViewById(R.id.btnSig);
        btnSiguiente.setOnClickListener(v -> {
            Intent i = new Intent(this, Reunion.class);
            // la reunión se guarda en Room ligada a este evento, así que le mandamos su id
            i.putExtra(Reunion.EXTRA_EVENTO_ID, evento.getId());
            startActivity(i);
        });

        // VER REUNIONES lista las reuniones de este evento
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
