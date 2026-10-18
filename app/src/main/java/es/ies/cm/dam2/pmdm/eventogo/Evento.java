package es.ies.cm.dam2.pmdm.eventogo;

import java.io.Serializable;

// Esta clase es el "Juego" del enunciado, pero con tu temática: un Evento.
// IMPLEMENTA Serializable para poder meter el objeto ENTERO en un Intent
// (putExtra) y recibirlo en otra pantalla, en vez de mandar campo a campo.
public class Evento implements Serializable {

    // Clave con la que el Evento viaja dentro de los Intent
    public static final String EXTRA = "evento";

    private String nombre;
    private String tipo;        // la "categoría" del enunciado (Boda, Concierto...)
    private String cliente;     // la "editorial" del enunciado
    private int asistentes;     // el "número de jugadores" del enunciado
    private String fecha;       // el "año" del enunciado, pero completa
    private int servicios;      // nº de servicios a presupuestar (1-10): la "dificultad"
    private int duracion;       // en minutos
    private boolean incluyeAV;
    private float valoracion;   // de 0 a 5, para el RatingBar de la ficha
    private int portada;        // id del drawable que se usa de portada

    // Constructor completo (lo usamos para los eventos de ejemplo)
    public Evento(String nombre, String tipo, String cliente, int asistentes, String fecha,
                  int servicios, int duracion, boolean incluyeAV, float valoracion) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.cliente = cliente;
        this.asistentes = asistentes;
        this.fecha = fecha;
        this.servicios = servicios;
        this.duracion = duracion;
        this.incluyeAV = incluyeAV;
        this.valoracion = valoracion;
        this.portada = portadaPara(tipo);
    }

    // Constructor para los eventos que se crean en el formulario (aún sin valorar)
    public Evento(String nombre, String tipo, String cliente, int asistentes, String fecha,
                  int servicios, int duracion, boolean incluyeAV) {
        this(nombre, tipo, cliente, asistentes, fecha, servicios, duracion, incluyeAV, 0f);
    }

    // Elige la portada según el tipo de evento. Son dibujos de relleno:
    // cuando tengas fotos reales en res/drawable, cambia los R.drawable de aquí
    private static int portadaPara(String tipo) {
        String t = (tipo == null) ? "" : tipo.toLowerCase();
        if (t.contains("boda")) return R.drawable.portada_boda;
        if (t.contains("concierto")) return R.drawable.portada_concierto;
        if (t.contains("corporativ") || t.contains("empresa")) return R.drawable.portada_corporativo;
        if (t.contains("cumple")) return R.drawable.portada_cumple;
        return R.drawable.portada_otro;
    }

    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getCliente() { return cliente; }
    public int getAsistentes() { return asistentes; }
    public String getFecha() { return fecha; }
    public int getServicios() { return servicios; }
    public int getDuracion() { return duracion; }
    public boolean isIncluyeAV() { return incluyeAV; }
    public float getValoracion() { return valoracion; }
    public int getPortada() { return portada; }

    // El enunciado pide tener toString definido: devuelve el evento en texto
    @Override
    public String toString() {
        return nombre + " (" + tipo + ")\n"
                + "Cliente: " + cliente + " - " + asistentes + " asistentes\n"
                + "Fecha: " + fecha + " - " + servicios + " servicios - " + duracion + " min";
    }
}