package searchengine.Busines.LinkHandling;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import searchengine.Busines.Lucene;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import static searchengine.services.IndexingImpl.atomicBoolean;

@RequiredArgsConstructor
public class LinkCrawler extends RecursiveAction {
    private final String domain, currentLink;
    private final Set<String> verifiedLinks;

    private final Site site;

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private LemmaRepository lemmaRepository;
    @Autowired
    private IndexRepository indexRepository;


    @Override
    protected void compute() {
        if (atomicBoolean.get() && !verifiedLinks.contains(currentLink)) {
            linkChecking();
        }
    }

    private void linkChecking() {
        Connection connection = Jsoup.connect(currentLink)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT " +
                        "5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com");
        try {
            List<Element> links = connection.get().select("a[href]");
            for (Element link : links) {
                String newLink = link.attr("abs:href");
                recursiveActionFork(newLink, connection);
            }
            verifiedLinks.add(currentLink);
        } catch (IOException e) {
            exceptionSite(site);
            e.printStackTrace();
        }
    }

    private void exceptionSite(Site site) {

    }

    private void recursiveActionFork(String newLink, Connection connection) throws IOException {
        Site site = siteRepository.getSiteByName(domain);
        Page page = new Page(newLink, connection.get(), domain, site, connection.execute().statusCode());
        if (!pageRepository.isExist(page.getPath(), site.getId())) {
            pageRepository.save(page);
            List<Lemma> list = getLemmas(connection.get());
            for (Lemma lemma : list) {
                if (atomicBoolean.get()) {
                    Index index = new Index();
                    if (!indexRepository.isExist(page.getId(), lemma.getId())) {
                        indexRepository.updateRank(page.getId(), lemma.getId());
                    } else {
                        indexRepository.save(index);
                    }
                    LinkCrawler linkCrawler = new LinkCrawler(domain,newLink,verifiedLinks,site);
                    linkCrawler.fork();
                }
            }
        }
    }

    private List<Lemma> getLemmas(Document document) {
        List<Lemma> list = new ArrayList<>();
        String[] allText = document.text().split(" ");
        for (String textWord : allText) {
            try {
                Lemma lemma = createLemma(textWord);
                list.add(lemma);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private Lemma createLemma(String textWord) throws IOException {
        String word2lower = textWord.toLowerCase(Locale.ROOT).trim();
        LuceneMorphology luceneMorphology = new Lucene(word2lower).getLuceneMorphology();
        String word = luceneMorphology.getNormalForms(word2lower).get(0);
        Lemma lemma = new Lemma();
        lemma.setLemma(word);
        lemma.setSiteId(site);
        if (!lemmaRepository.isExist(lemma.getLemma(), lemma.getSiteId().getId())) {
            lemma.setFrequency(1);
            lemmaRepository.save(lemma);
        } else {
            lemmaRepository.updateFrequency(lemma.getLemma(), lemma.getSiteId().getId());
        }
        return lemma;
    }
}
