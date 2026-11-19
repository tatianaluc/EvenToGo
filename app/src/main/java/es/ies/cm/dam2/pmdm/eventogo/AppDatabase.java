package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Base de datos Room: eventos y sus reuniones (1-N). Los usuarios y la wishlist siguen en SQLite (EventosDB).
// exportSchema = false para que no pida una carpeta donde guardar el esquema
@Database(entities = {Evento.class, ReunionEvento.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Room genera el código de estos dos métodos
    public abstract EventoDao eventoDao();

    public abstract ReunionDao reunionDao();

    // Un solo hilo aparte para todas las consultas de la app. Al ser uno solo, las tareas se ejecutan
    // por orden: lo que se lanza antes (por ejemplo la carga inicial) termina antes que lo que se lanza después.
    // Se usa así: AppDatabase.ejecutor.execute(() -> { consulta... runOnUiThread(() -> { pintar }); });
    public static final ExecutorService ejecutor = Executors.newSingleThreadExecutor();

    // Singleton: solo se crea una instancia de la bd para toda la app, porque crearla es caro
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
