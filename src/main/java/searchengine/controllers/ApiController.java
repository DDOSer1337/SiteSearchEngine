package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.Indexing;
import searchengine.services.StatisticsService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final Indexing indexing;

    public ApiController(StatisticsService statisticsService,Indexing indexing) {
        this.statisticsService = statisticsService;
        this.indexing = indexing;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
    @GetMapping("/startIndexing")
    public ResponseEntity<?> startIndexing(){
        return indexing.start();
    }
    @GetMapping("/stopIndexing")
    public ResponseEntity<?> stopIndexing(){
        return indexing.stop();
    }
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("query") String query,@RequestParam(value = "site", required = false) String site){
        return null;
    }
    @PostMapping("/indexPage")
    public ResponseEntity<?> indexPage(@RequestParam("url") String url){
        return null;
    }
}
