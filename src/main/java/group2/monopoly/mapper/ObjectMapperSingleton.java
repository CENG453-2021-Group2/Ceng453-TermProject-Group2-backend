package group2.monopoly.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Singleton class extending Jackson's {@link ObjectMapper}.
 */
public class ObjectMapperSingleton extends ObjectMapper {
    private static ObjectMapper objectMapper;
    private static final Object lock = new Object();

    /**
     * Creates the {@link ObjectMapper} and returns it.
     * <br><br>
     * This method is thread-safe.
     *
     * @return the singleton mapper object.
     */
    public static ObjectMapper getMapper() {
        synchronized (lock) {
            if (objectMapper == null) {
                objectMapper = new ObjectMapper();
            }
        }
        return objectMapper;
    }
}
