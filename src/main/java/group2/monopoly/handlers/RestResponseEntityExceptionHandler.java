package group2.monopoly.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * It extends the ResponseEntityExceptionHandler class and overrides the handleMethodArgumentNotValid
 * method to return a JSON response with the validation errors
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * A custom exception handler for the MethodArgumentNotValidException.
     * 
     * @param ex The exception that was thrown
     * @param headers The headers to be written to the response
     * @param status The HTTP status code to return.
     * @param request The current request.
     * @return A JSON object with the following structure:
     * ```
     * {
     *     "success": false,
     *     "message": "One or more fields could not be validated.",
     *     "details": {
     *         "field1": [
     *             "error1",
     *             "error2"
     *         ],
     *         "field2": [
     *             "error1",
     *             "error
     */
    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                          HttpHeaders headers, HttpStatus status,
                                                          WebRequest request) {
        final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        Map<String, Set<String>> errorsMap = fieldErrors.stream().collect(
                Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toSet())
                )
        );
        if (errorsMap.isEmpty()) {
            return new ResponseEntity<>(ex, headers, status);
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        response.put("success", false);
        response.put("message", "One or more fields could not be validated.");

        response.set("details", mapper.convertValue(errorsMap, JsonNode.class));


        try {
            return new ResponseEntity<>(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response), headers, status);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<Object> handleAccessDeniedException(Exception exception, WebRequest webRequest) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        response.put("success", false);
        response.put("message", exception.getMessage());
        return new ResponseEntity(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }


}
