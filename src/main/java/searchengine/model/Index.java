package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "indices")
public class Index implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "pages_id")
    private Page pageId;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "lemmas_id")
    private Lemma lemmaId;

    @Column(name = "`rank`", nullable = false)
    private float rank;

    public Index() {
    }

    public Index(Page pageId, Lemma lemmaId) {
        this.pageId = pageId;
        this.lemmaId = lemmaId;
        rank = 1;
    }
}

