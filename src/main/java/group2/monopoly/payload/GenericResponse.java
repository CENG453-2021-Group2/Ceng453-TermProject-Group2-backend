package group2.monopoly.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class GenericResponse {
    private boolean success;
    private String message;

    public static GenericResponse message(String message) {
        GenericResponse response = new GenericResponse();
        response.success = true;
        response.message = message;
        return response;
    }

    public static GenericResponse error(String error) {
        GenericResponse response = new GenericResponse();
        response.success = false;
        response.message = error;
        return response;
    }
}
