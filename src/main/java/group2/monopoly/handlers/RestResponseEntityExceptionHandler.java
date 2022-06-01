package group2.monopoly.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import group2.monopoly.mapper.ObjectMapperSingleton;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * It extends the ResponseEntityExceptionHandler class and overrides the
 * handleMethodArgumentNotValid
 * method to return a JSON response with the validation errors
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {


    public static ObjectMapper mapper = ObjectMapperSingleton.getMapper();


    /**
     * Handles {@link MethodArgumentNotValidException}.
     * <br><br>
     * The response object has a JSON body describing the problematic fields of a request.
     *
     * @param ex      The exception that was thrown
     * @param headers The headers to be written to the response
     * @param status  The HTTP status code to return.
     * @param request The current request.
     * @return A JSON object with the following structure:
     *
     */
    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        Map<String, Set<String>> errorsMap = fieldErrors.stream().collect(
                Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toSet())
                )
        );
        if (errorsMap.isEmpty()) {
            return ResponseEntity.status(status).headers(headers).body(ex);
        }

        ObjectNode response = mapper.createObjectNode().put("message", "One or more fields could " +
                "not be validated.");
        response.set("errors", mapper.convertValue(errorsMap, JsonNode.class));

        return ResponseEntity.status(status).headers(headers).body(response);
    }


}
