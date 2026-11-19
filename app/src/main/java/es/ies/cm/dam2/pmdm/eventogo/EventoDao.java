package es.ies.cm.dam2.pmdm.eventogo;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// DAO = interfaz con las operaciones sobre la tabla eventos.
// Room escribe el código de cada método al compilar.
// Todas se llaman desde el hilo executor, nunca desde el hilo principal UI.
// Los eventos del catálogo sin dueño tienen usuarioId = -1
@Dao
public interface EventoDao {

    // Guarda un evento y devuelve el id que le ha puesto Room
    @Insert
    long insertar(Evento evento);

    // Guarda varios a la vez en la carga inicial y devuelve sus ids en el mismo orden
    @Insert
    List<Long> insertarVarios(List<Evento> eventos);

    // Actualiza la fila que tenga el mismo id que el evento. Devuelve cuántas filas ha cambiado
    @Update
    int actualizar(Evento evento);

    // Borra el evento con ese id y en cascada sus reuniones. Devuelve cuántas filas ha borrado
    @Query("DELETE FROM eventos WHERE id = :id")
    int borrarPorId(int id);

    // Borra todos los eventos de un usuario y en cascada sus reuniones. Se usa al eliminar su cuenta
    @Query("DELETE FROM eventos WHERE usuarioId = :usuarioId")
    int borrarDe(int usuarioId);

    // Eventos de un usuario, en el orden en que se guardaron
    @Query("SELECT * FROM eventos WHERE usuarioId = :usuarioId ORDER BY id")
    List<Evento> eventosDe(int usuarioId);

    // Eventos de un usuario ordenados por nombre sin distinguir mayúsculas
    @Query("SELECT * FROM eventos WHERE usuarioId = :usuarioId ORDER BY nombre COLLATE NOCASE")
    List<Evento> eventosDePorNombre(int usuarioId);

    // Cuántos eventos tiene un usuario
    @Query("SELECT COUNT(*) FROM eventos WHERE usuarioId = :usuarioId")
    int contarDe(int usuarioId);

    // Cuántos eventos hay en total para saber si la bd está vacía
    @Query("SELECT COUNT(*) FROM eventos")
    int contarTodos();

    // los ejemplos sin dueño
    @Query("SELECT * FROM eventos WHERE usuarioId = -1 ORDER BY nombre COLLATE NOCASE")
    List<Evento> catalogo();

    // Un evento por su id, null si no existe
    @Query("SELECT * FROM eventos WHERE id = :id")
    Evento porId(int id);

    // Buscar en el catálogo por nombre con los % del LIKE y con un mínimo de asistentes
    @Query("SELECT * FROM eventos WHERE usuarioId = -1 AND nombre LIKE :patron "
            + "AND asistentes >= :minAsistentes ORDER BY nombre COLLATE NOCASE")
    List<Evento> buscarCatalogo(String patron, int minAsistentes);
}
