package es.ies.cm.dam2.pmdm.eventogo;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

// DAO de la tabla reuniones
// Se llama siempre desde un hilo aparte (AppDatabase.ejecutor)
@Dao
public interface ReunionDao {

    // Guarda una reunión y devuelve su id
    @Insert
    long insertar(ReunionEvento reunion);

    // Guarda varias en la carga inicial
    @Insert
    void insertarVarias(List<ReunionEvento> reuniones);

    // Cambia solo el estado de una reunión ACEPTADO / CANCELADO
    @Query("UPDATE reuniones SET estado = :estado WHERE id = :id")
    int cambiarEstado(int id, String estado);

    // Borra una reunión por su id
    @Query("DELETE FROM reuniones WHERE id = :id")
    int borrarPorId(int id);

    // Reuniones de un evento
    @Query("SELECT * FROM reuniones WHERE eventoId = :eventoId ORDER BY id")
    List<ReunionEvento> reunionesDe(int eventoId);

    // Cuántas reuniones tiene un evento
    @Query("SELECT COUNT(*) FROM reuniones WHERE eventoId = :eventoId")
    int contarDe(int eventoId);
}
