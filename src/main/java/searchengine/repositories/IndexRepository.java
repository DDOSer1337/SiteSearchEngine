package searchengine.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Index;

@Repository
public interface IndexRepository extends CrudRepository<Index,Long> {

    boolean existsByLemmaIdAndPageId(int pageID,int lemmaID);


    Iterable<Index> findFirst10ByLemmaId_Lemma(String lemma);

    Iterable<Index> findFirst10ByLemmaId_LemmaAndLemmaId_SiteId_Url(String lemma, String url);

    @Query(value ="Select `rank` from skillbox.indices as i " +
            "join skillbox.lemmas as l on i.lemmas_id =  l.id" +
            "join skillbox.sites as s on l.sites_id = s.id" +
            "where s.url = :siteURL and lemmas_id = :lemmaID",nativeQuery = true)
    Integer getRank(@Param("siteURL") String siteURL, @Param("lemmaID") String lemmasID);

    @Modifying
    @Transactional
    @Query(value ="UPDATE skillbox.indices SET `rank` = `rank` + 1 where pages_id = :pageID and lemmas_id = :lemmaID",nativeQuery = true)
    void upRank(@Param("pageID") int pageID, @Param("lemmaID") int lemmasID);
}
