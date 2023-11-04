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
public class StatisticsImpl implements StatisticsService {
    private final SitesList sites;
    @Autowired
    private final SiteRepository siteRepository;
    @Autowired
    private final PageRepository pageRepository;
    @Autowired
    private final LemmaRepository lemmaRepository;

    @Override
    public StatisticsResponse getStatistics() {

        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        for (searchengine.config.Site site : sites.getSites()) {
            DetailedStatisticsItem item = getDetailedStatisticsItem(site);
            detailed.add(item);
        }
        try {
            return getResponse(sites.getSites().size(), pageRepository.count(), lemmaRepository.count(), detailed);

        } catch (Exception e) {
            e.printStackTrace();
            return getResponse(sites.getSites().size(), 0, 0, detailed);
        }

    }

    private DetailedStatisticsItem getDetailedStatisticsItem(searchengine.config.Site sites) {
        return Optional.ofNullable(siteRepository.findByName(sites.getName()))
                .map(value -> itemCreator(value.getStatusTime().getNano(),
                        value.getSiteStatus().toString(),
                        sites.getName(), value.getLastError(),
                        value.getUrl(),
                        (int) lemmaRepository.countBySiteId_Name(value.getName()),
                        (int) pageRepository.countBySiteId_Name(value.getName()))).orElseGet(()
                        -> itemCreator(0, "", sites.getName(), "Ошибка, данные не обнаружены", sites.getUrl(), 0, 0));
    }

    private DetailedStatisticsItem itemCreator(long statusTime, String siteStatus, String name, String lastError, String url, int lemmaCount, int pageCount) {
        DetailedStatisticsItem item = new DetailedStatisticsItem();
        item.setStatusTime(statusTime);
        item.setStatus(siteStatus);
        item.setName(name);
        item.setError(lastError);
        item.setUrl(url);
        item.setLemmas(lemmaCount);
        item.setPages(pageCount);
        return item;
    }

    private StatisticsResponse getResponse(int sitesCount, long pageCount, long lemmaCount, List<DetailedStatisticsItem> detailed) {
        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();

        data.setTotal(getTotalStatistics(sitesCount, (int) pageCount, lemmaCount));
        data.setDetailed(detailed);

        response.setStatistics(data);
        response.setResult(true);

        return response;
    }

    private TotalStatistics getTotalStatistics(int sitesCount, int pageCount, long lemmaCount) {
        TotalStatistics totalStatistics = new TotalStatistics();
        totalStatistics.setSites(sitesCount);
        totalStatistics.setIndexing(true);
        totalStatistics.setPages(pageCount);
        totalStatistics.setLemmas((int) lemmaCount);
        return totalStatistics;
    }
}
