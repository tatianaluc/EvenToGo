package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Base de datos Room de los eventos y sus reuniones.
@Database(entities = {Evento.class, ReunionEvento.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Room genera el código de estos dos métodos
    public abstract EventoDao eventoDao();

    public abstract ReunionDao reunionDao();

    // Un solo hilo aparte para todas las consultas de la app, asi las tareas se ejecutan
    // por orden. Se usa el executor que es como el start de hilos
    public static final ExecutorService ejecutor = Executors.newSingleThreadExecutor();

    // Patrón Singleton: solo se crea una instancia de la bd para toda la app.
    private static volatile AppDatabase instancia;

    public static AppDatabase getInstance(Context context) {
        if (instancia == null) {
            synchronized (AppDatabase.class) {
                if (instancia == null) {
                    instancia = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "eventogo_room.db").build();
                }
            }
        }
        return instancia;
    }
}
