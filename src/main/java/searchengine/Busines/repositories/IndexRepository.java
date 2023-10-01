package searchengine.Busines.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndexRepository extends CrudRepository<Index,Long> {

    boolean existsByLemmaIdAndPageId(Lemma lemma, Page page);

    List<Index> findAllByLemmaId_LemmaOrderByLemmaId_frequency(String lemma);

    List<Index> findAllByLemmaId_lemmaAndPageId_SiteId_urlOrderByLemmaId_frequency(String lemma, String url);

    @Query(value ="Select Count(`rank`) from skillbox.indices as i \n" +
            "join skillbox.lemmas as l on i.lemmas_id =  l.id \n" +
            "join skillbox.sites as s on l.sites_id = s.id \n" +
            "where s.url = :siteURL and lemma = :lemma",nativeQuery = true)
    Optional<Integer> getRankOnOneSite(@Param("siteURL") String siteURL, @Param("lemma") String lemmas);

    @Query(value ="Select Count(`rank`) from skillbox.indices as i \n" +
            "join skillbox.lemmas as l on i.lemmas_id =  l.id \n" +
            "where lemma = :lemma",nativeQuery = true)
    Optional<Integer> getRankOnAllSites(@Param("lemma") String lemmas);

    @Modifying
    @Transactional
    @Query(value ="UPDATE skillbox.indices as i " +
            "SET i.rank = i.rank + 1 " +
            "where i.pages_id = :pageID and " +
            "lemmas_id = :lemmaID",nativeQuery = true)
    void upRank(@Param("pageID") int pageID, @Param("lemmaID") int LemmaID);
}
