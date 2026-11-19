package es.ies.cm.dam2.pmdm.eventogo;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

// Implementa Serializable para poder mandarlo dentro de un Intent.
// @Entity hace que Room cree una tabla "eventos" con una columna por cada atributo
@Entity(tableName = "eventos")
public class Evento implements Serializable {

    // Nombre del extra con el que viaja el Evento en los Intent
    public static final String EXTRA = "evento";

    private String nombre;
    private String tipo;        // categoria
    private String cliente;
    private int asistentes;
    private String fecha;
    private int servicios;      // nº de servicios a presupuestar (1-10)
    private boolean incluyeAV;
    // id con el que se guarda en la bd. Vale 0 mientras el evento no se haya guardado todavía: cuando Room ve
    // un 0 en una clave autogenerada, le pone él solo el siguiente número
    @PrimaryKey(autoGenerate = true)
    private int id = 0;
    private String horaInicio;  // formato HH:mm
    private String horaFin;     // formato HH:mm
    private String lugar;
    // @Ignore: la portada no se guarda en la bd, se calcula en el constructor a partir del tipo
    @Ignore
    private int portada;        // la imagen de portada
    // Valor de usuarioId de los eventos de ejemplo (catálogo), que no son de ningún usuario
    public static final int SIN_DUENO = -1;
    // Dueño del evento: id del usuario (que vive en SQLite, por eso aquí es solo un número, sin clave ajena)
    private int usuarioId = SIN_DUENO;

    // Constructor completo, para los eventos de ejemplo
    public Evento(String nombre, String tipo, String cliente, int asistentes, String fecha,
                  String horaInicio, String horaFin, int servicios, boolean incluyeAV, String lugar) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.cliente = cliente;
        this.asistentes = asistentes;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.servicios = servicios;
        this.incluyeAV = incluyeAV;
        this.lugar = lugar;
        this.portada = portadaSegun(tipo);
    }


    // Elige la imagen según el tipo de evento desde res/drawable
    private int portadaSegun(String tipo) {
        String t = (tipo == null) ? "" : tipo.toLowerCase();
        if (t.contains("boda")) return R.drawable.boda;
        if (t.contains("concierto")) return R.drawable.concierto;
        if (t.contains("corporativ") || t.contains("empresa")) return R.drawable.corporativo;
        if (t.contains("cumple")) return R.drawable.cumple;
        return R.drawable.otro;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCliente() {
        return cliente;
    }

    public int getAsistentes() {
        return asistentes;
    }

    public String getFecha() {
        return fecha;
    }

    public int getServicios() {
        return servicios;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public String getLugar() {
        return lugar;
    }

    public boolean isIncluyeAV() {
        return incluyeAV;
    }

    public int getPortada() {
        return portada;
    }

    // complejidad del evento (de 0,5 a 5 estrellas) y cada 2 servicios a presupuestar es una estrella.
    public float getComplejidad() {
        return servicios / 2f;
    }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    // Calcula cuántas horas dura el evento. Si la hora de fin es menor que la de inicio, el evento acaba
    // al día siguiente, por ejemplo, un evento de 22:00 a 03:00 son 5 horas, pero a partir de las 00h
    // ya es el día sigueinte, así que le sumamos 24 horas a la de fin
    public int getDuracion() {
        int minInicio = Integer.parseInt(horaInicio.substring(0, 2)) * 60 + Integer.parseInt(horaInicio.substring(3, 5));
        int minFin = Integer.parseInt(horaFin.substring(0, 2)) * 60 + Integer.parseInt(horaFin.substring(3, 5));
        if (minFin < minInicio) {
            minFin += 24 * 60;
        }
        return Math.round((minFin - minInicio) / 60f); // en horas, redondeado
    }

    @Override
    public String toString() {
        return nombre + " (" + tipo + ")\n"
                + "Cliente: " + cliente + " - " + asistentes + " asistentes\n"
                + "Fecha: " + fecha + " (" + horaInicio + " - " + horaFin + ") - " + servicios + " servicios - " + getDuracion() + " horas\n"
                + "Lugar: " + lugar;
    }
}
