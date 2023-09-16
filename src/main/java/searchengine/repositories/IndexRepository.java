package searchengine.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;

@Repository
public interface IndexRepository extends CrudRepository<Index,Long> {

    boolean existsByLemmaIdAndPageId(Lemma lemma, Page page);

    Index findFirst10ByLemmaId_Lemma(String lemma);

    Iterable<Index> findTop10ByLemmaId_lemmaAndPageId_SiteId_urlOrderByLemmaId_frequencyDesc(String lemma, String url);

    @Query(value ="Select sum(`rank`) from skillbox.indices as i \n" +
            "join skillbox.lemmas as l on i.lemmas_id =  l.id \n" +
            "join skillbox.sites as s on l.sites_id = s.id \n" +
            "where s.url = :siteURL and lemma = :lemma",nativeQuery = true)
    Integer getRank(@Param("siteURL") String siteURL, @Param("lemma") String lemmas);

    @Modifying
    @Transactional
    @Query(value ="UPDATE skillbox.indices as i " +
            "SET i.rank = i.rank + 1 " +
            "where i.pages_id = :pageID and " +
            "lemmas_id = :lemmaID",nativeQuery = true)
    void upRank(@Param("pageID") int pageID, @Param("lemmaID") int LemmaID);
}
