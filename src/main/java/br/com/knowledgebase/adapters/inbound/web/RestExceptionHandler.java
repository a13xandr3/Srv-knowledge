package br.com.knowledgebase.adapters.inbound.web;

import br.com.knowledgebase.domain.exception.FileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Void> notFound(){ return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<String> badRequest(Exception e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
