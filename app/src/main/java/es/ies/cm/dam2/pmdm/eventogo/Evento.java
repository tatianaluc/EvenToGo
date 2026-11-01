package es.ies.cm.dam2.pmdm.eventogo;

import java.io.Serializable;

// Implementa Serializable para poder mandarlo dentro de un Intent
public class Evento implements Serializable {

    // Nombre del extra con el que viaja el Evento en los Intent
    public static final String EXTRA = "evento";

    private String nombre;
    private String tipo;        // categoria
    private String cliente;
    private int asistentes;
    private String fecha;
    private int servicios;      // nº de servicios a presupuestar (1-10)
    private int duracion;       // en minutos
    private boolean incluyeAV;
    private float valoracion;   // de 0 a 5 para el RatingBar de la ficha
    private int portada;        // la imagen de portada

    // Constructor completo, para los eventos de ejemplo
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
        this.portada = portadaSegun(tipo);
    }

    // Constructor para los eventos que se crean en el formulario (sin valoración todavía)
    public Evento(String nombre, String tipo, String cliente, int asistentes, String fecha,
                  int servicios, int duracion, boolean incluyeAV) {
        this(nombre, tipo, cliente, asistentes, fecha, servicios, duracion, incluyeAV, 0f);
    }

    // Elige la imagen según el tipo de evento desde res/drawable, de moemnto se usan de prueba
    private int portadaSegun(String tipo) {
        String t = tipo.toLowerCase();
        if (t.contains("boda")) {
            return R.drawable.boda;
        } else if (t.contains("concierto")) {
            return R.drawable.concierto;
        } else if (t.contains("corporativo") || t.contains("empresa")) {
            return R.drawable.corporativo;
        } else if (t.contains("cumple")) {
            return R.drawable.cumple;
        } else {
            return R.drawable.otro;
        }
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

    @Override
    public String toString() {
        return nombre + " (" + tipo + ")\n"
                + "Cliente: " + cliente + " - " + asistentes + " asistentes\n"
                + "Fecha: " + fecha + " - " + servicios + " servicios - " + duracion + " min";
    }
}
