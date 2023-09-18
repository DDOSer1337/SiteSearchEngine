package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.Interface.IndexPage;
import searchengine.services.Interface.Indexing;
import searchengine.services.Interface.SearchEngine;
import searchengine.services.Interface.StatisticsService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final Indexing indexing;
    private final SearchEngine searchEngine;
    private final IndexPage indexPage;

    public ApiController(StatisticsService statisticsService, Indexing indexing, SearchEngine searchEngine, IndexPage indexPage) {
        this.statisticsService = statisticsService;
        this.indexing = indexing;
        this.searchEngine = searchEngine;
        this.indexPage = indexPage;
    }
    // Сделано
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
    // почти сделано, осталось сделать только нормальную индексацию
    @GetMapping("/startIndexing")
    public ResponseEntity<?> startIndexing(){
        return indexing.start();
    }
    // Сделано
    @GetMapping("/stopIndexing")
    public ResponseEntity<?> stopIndexing(){
        return indexing.stop();
    }
    // сделано
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("query") String query,@RequestParam(value = "site", required = false) String site){
        return searchEngine.search(site,query.split(" "));
    }
    //В процессе
    @PostMapping("/indexPage")
    public ResponseEntity<?> indexPage(@RequestParam("url") String url){
        return indexPage.AddOrUpdatePage(url);
    }
}
