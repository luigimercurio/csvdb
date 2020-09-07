package csvdb.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CsvDbExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<CsvDbResponse> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        CsvDbResponse response;

        response = new CsvDbResponse();
        response.setError(new IllegalArgumentException("File too large"));
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(response);
    }
}