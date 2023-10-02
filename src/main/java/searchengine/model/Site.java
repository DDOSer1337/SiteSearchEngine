package searchengine.model;

import lombok.Getter;
import lombok.Setter;
import searchengine.model.Enum.SiteStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "sites")
public class Site implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "site_status", columnDefinition = "enum ('INDEXING','INDEXED','FAILED')")
    private SiteStatus siteStatus;

    @Column(name = "status_time", nullable = false)
    private LocalDateTime statusTime;

    @Column(name = "last_error")
    private String lastError;

    @Column(nullable = false, length = 255)
    private String url;

    @Column(nullable = false, length = 255)
    private String name;

    @OneToMany(mappedBy = "siteId",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Page> pages = new ArrayList<>();

    @OneToMany(mappedBy = "siteId",cascade = CascadeType.REMOVE,orphanRemoval = true)
    private List<Lemma> lemmas = new ArrayList<>();


    public Site() {
    }

    public Site(String url, String name) {
        this.siteStatus = SiteStatus.INDEXING;
        this.statusTime = LocalDateTime.now();
        this.url = url+"/";
        if (name.startsWith("www.")) {
            this.name = name.substring(4);
        } else {
            this.name = name;
        }
        this.lastError = "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Site site = (Site) o;
        return Objects.equals(url, site.url) && Objects.equals(name, site.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, name);
    }
}
