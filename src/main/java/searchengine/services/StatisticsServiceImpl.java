package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import searchengine.Busines.DBConnector;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.Site;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.Interface.StatisticsService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final SitesList sites;
    @Autowired
    private final SiteRepository siteRepository;
    @Autowired
    private final PageRepository pageRepository;
    @Autowired
    private final LemmaRepository lemmaRepository;

    String[] statuses = {"INDEXED", "FAILED", "INDEXING"};
    String[] errors = {
            "Ошибка индексации: главная страница сайта не доступна",
            "Ошибка индексации: сайт не доступен",
            ""
    };
    @Override
    public StatisticsResponse getStatistics() {
        DBConnector dbConnector = new DBConnector();
        Connection connection = dbConnector.getConnection();
        TotalStatistics totalStatistics = new TotalStatistics();
        totalStatistics.setSites(sites.getSites().size());
        totalStatistics.setIndexing(true);
        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        for (searchengine.config.Site site : sites.getSites()) {
            DetailedStatisticsItem item = getDetailedStatisticsItem(site.getName());
            detailed.add(item);
        }
        totalStatistics.setPages(pageRepository.getAllPageCount());
        totalStatistics.setLemmas(lemmaRepository.getAllLemmaCount());
        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(totalStatistics);
        data.setDetailed(detailed);
        response.setStatistics(data);
        response.setResult(true);
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return response;
    }
    private DetailedStatisticsItem getDetailedStatisticsItem(String name)
    {
        DetailedStatisticsItem item = new DetailedStatisticsItem();
        Site site = siteRepository.getSiteByName(name);
        item.setStatusTime(site.getStatusTime().getNano());
        item.setStatus(site.getSiteStatus().toString());
        item.setName(site.getName());
        item.setError(site.getLastError());
        item.setUrl(site.getUrl());
        item.setLemmas(lemmaRepository.getLemmaCount(site.getName()));
        item.setPages(pageRepository.getPageCount(site.getName()));
        return item;
    }
}
