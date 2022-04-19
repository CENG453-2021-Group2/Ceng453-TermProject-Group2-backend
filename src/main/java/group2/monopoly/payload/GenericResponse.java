package group2.monopoly.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericResponse {
    private String message;
    private String error;

    public static GenericResponse message(String message) {
        GenericResponse response = new GenericResponse();
        response.message = message;
        return response;
    }

    public static GenericResponse error(String error) {
        GenericResponse response = new GenericResponse();
        response.error = error;
        return response;
    }
}
