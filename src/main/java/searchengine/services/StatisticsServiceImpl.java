package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        TotalStatistics totalStatistics = new TotalStatistics();
        totalStatistics.setSites(sites.getSites().size());
        totalStatistics.setIndexing(true);
        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        for (searchengine.config.Site site : sites.getSites()) {
            DetailedStatisticsItem item = getDetailedStatisticsItem(site);
            detailed.add(item);
        }
        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        try {
            long pageCount = pageRepository.count();
            long lemmaCount = lemmaRepository.count();
            totalStatistics.setPages((int) pageCount);
            totalStatistics.setLemmas((int) (lemmaCount));
            data.setTotal(totalStatistics);
            data.setDetailed(detailed);
            response.setStatistics(data);
            response.setResult(true);

        } catch (Exception e) {
            totalStatistics.setPages(0);
            totalStatistics.setLemmas(0);
            data.setTotal(totalStatistics);
            data.setDetailed(detailed);
            response.setStatistics(data);
            response.setResult(true);
            e.printStackTrace();
        }
        return response;
    }

    private DetailedStatisticsItem getDetailedStatisticsItem(searchengine.config.Site sites) {
        DetailedStatisticsItem item = new DetailedStatisticsItem();
        System.out.println(sites.getName());
        Optional<Site> site = Optional.ofNullable(siteRepository.findByName(sites.getName()));
        if (site.isPresent()) {
            item.setStatusTime(site.get().getStatusTime().getNano());
            item.setStatus(site.get().getSiteStatus().toString());
            item.setName(sites.getName());
            item.setError(site.get().getLastError());
            item.setUrl(site.get().getUrl());
            item.setLemmas((int) lemmaRepository.countBySiteId_Name(site.get().getName()));
            item.setPages((int) pageRepository.countBySiteId_Name(site.get().getName()));
        } else {
            item.setStatusTime(0);
            item.setStatus("");
            item.setName(sites.getName());
            item.setError("Ошибка, данные не обнаружены");
            item.setUrl(sites.getUrl());
            item.setLemmas(0);
            item.setPages(0);
        }
        return item;
    }
}
