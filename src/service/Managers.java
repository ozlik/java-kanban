package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import converter.LocalDateTimeConverter;

import java.io.File;
import java.time.LocalDateTime;

public final class Managers {

    public static TaskManager getInMemoryTaskManager(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultFileBacked() {
        return FileBackedTaskManager.loadFromFile(new File("File.csv"));
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter())
                .create();
    }
}