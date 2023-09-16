package searchengine.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Lemma;
import searchengine.model.Site;

import java.util.Optional;

@Repository
public interface LemmaRepository extends CrudRepository<Lemma,Long> {

    Optional<Lemma> findByLemma(String lemma);

    long countBySiteId_Name(String name);

    boolean existsByLemmaAndSiteId(String lemma, Site site);

    @Modifying
    @Transactional
    @Query(value ="UPDATE lemmas SET `frequency` = `frequency` + 1 WHERE lemma = :lemma and sites_id = :sitesID",nativeQuery = true)
    void updateFrequency(@Param("lemma") String lemma, @Param("sitesID") int sitesID);


    @Query(value ="Select `frequency` from skillbox.lemmas WHERE lemma = :lemma and sites_id = :sitesID",nativeQuery = true)
    Integer getFrequency(@Param("lemma") String lemma, @Param("sitesID") int sitesID);

    @Query(value ="SELECT sum(frequency) FROM skillbox.lemmas",nativeQuery = true)
    Integer getAllSumFrequency ();
}
