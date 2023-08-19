package searchengine.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;

import java.util.Optional;

@Repository
public interface LemmaRepository extends CrudRepository<Lemma,Long> {

    @Query(value ="Select * from skillbox.lemmas where WHERE lemma = :lemma and sites_id = :sitesID",nativeQuery = true)
    Optional<Lemma> getByLemma(@Param("lemma") String lemma, @Param("sitesID") int sitesID);

    @Query(value ="SELECT CASE WHEN frequency > 0 THEN true ELSE false END FROM skillbox.lemmas WHERE lemma = :lemma and sites_id = :sitesID",nativeQuery = true)
    boolean isExist(@Param("lemma") String lemma, @Param("sitesID") int sitesID);

    @Modifying
    @Query(value ="UPDATE skillbox.lemmas SET `frequency` = `frequency` + 1 WHERE lemma = :lemma and sites_id = :sitesID",nativeQuery = true)
    void updateFrequency(@Param("lemma") String lemma, @Param("sitesID") int sitesID);

    @Query(value ="Select `frequency` from skillbox.lemmas WHERE lemma = :lemma and sites_id = :sitesID",nativeQuery = true)
    Integer getFrequency(@Param("lemma") String lemma, @Param("sitesID") int sitesID);

    @Query(value ="Select Count(*) from skillbox.lemmas as l Join skillbox.sites as s on l.sites_id = s.id where s.name = :siteName",nativeQuery = true)
    Integer getLemmaCount(@Param("siteName") String siteName);

    @Query(value ="SELECT sum(frequency) FROM skillbox.lemmas")
    Integer getAllLemmaCount ();
}
