package es.ies.cm.dam2.pmdm.eventogo;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// Reunión que se pide para un evento
// La clave ajena eventoId apunta al id del evento y, con CASCADE, si se borra el evento
// Room borra solo sus reuniones.
@Entity(tableName = "reuniones",
        foreignKeys = @ForeignKey(
                entity = Evento.class,
                parentColumns = "id",
                childColumns = "eventoId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("eventoId")}) // Room recomienda un índice en la clave ajena
public class ReunionEvento {

    // Estados posibles de una reunión
    public static final String ACEPTADA = "ACEPTADO";
    public static final String CANCELADA = "CANCELADO";

    // Atributos públicos para que Room los lea y escriba directamente
    @PrimaryKey(autoGenerate = true)
    public int id;          // 0 = todavía sin guardar, Room le pone el siguiente número
    public int eventoId;    // a qué evento pertenece
    public String cliente;  // nombre del cliente que pide la reunión
    public String fecha;    // dd/MM/yyyy
    public String modalidad; // Online u Oficina
    public String estado;   // ACEPTADO o CANCELADO

    // Room usa este constructor vacío y luego rellena los atributos
    public ReunionEvento() {
    }

    // Constructor cómodo para crear una reunión nueva desde el código
    public ReunionEvento(int eventoId, String cliente, String fecha, String modalidad, String estado) {
        this.eventoId = eventoId;
        this.cliente = cliente;
        this.fecha = fecha;
        this.modalidad = modalidad;
        this.estado = estado;
    }

    // Así se ve cada reunión en la lista de ListaReuniones
    @Override
    public String toString() {
        return fecha + " - " + modalidad + "\nCliente: " + cliente + " - " + estado;
    }
}
