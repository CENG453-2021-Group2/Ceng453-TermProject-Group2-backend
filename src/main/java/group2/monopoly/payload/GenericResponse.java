package group2.monopoly.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Class that can be used to return a success or error message to the client
 */
@Data
public class GenericResponse {
    private boolean success;
    private String message;

    /**
     * This function returns a GenericResponse object with the success field set to true and the
     * message field set to the message parameter.
     * 
     * @param message The message to be displayed to the user.
     * @return A GenericResponse object with the success and message fields set to true and the message
     * parameter respectively.
     */
    public static GenericResponse message(String message) {
        GenericResponse response = new GenericResponse();
        response.success = true;
        response.message = message;
        return response;
    }

    /**
     * If the response is not successful, return an error message.
     * 
     * @param error The error message to be displayed to the user.
     * @return A GenericResponse object with the success field set to false and the message field set
     * to the error message.
     */
    public static GenericResponse error(String error) {
        GenericResponse response = new GenericResponse();
        response.success = false;
        response.message = error;
        return response;
    }
}
