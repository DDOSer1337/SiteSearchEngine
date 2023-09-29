package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "lemmas")
public class Lemma implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "sites_id")
    private Site siteId;

    @Column(name = "lemma",nullable = false)
    private String lemma;

    @Column(name = "frequency",nullable = false)
    private int frequency;

    @OneToMany(mappedBy = "lemmaId",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Index> indices = new ArrayList<>();


}
