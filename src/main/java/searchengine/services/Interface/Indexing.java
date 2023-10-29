package searchengine.services.Interface;

import org.springframework.http.ResponseEntity;

import java.util.concurrent.atomic.AtomicBoolean;


public interface Indexing {
    ResponseEntity<?> stop();
    ResponseEntity<?> start();
}
