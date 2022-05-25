package group2.monopoly.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperSingleton extends ObjectMapper {
    private static ObjectMapper objectMapper;
    private static final Object lock = new Object();

    public static ObjectMapper getMapper() {
        synchronized (lock) {
            if (objectMapper == null) {
                objectMapper = new ObjectMapper();
            }
        }
        return objectMapper;
    }
}
