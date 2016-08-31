package to.lova.hibernate.testcase.entitygraph.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class Order {

    @Id
    @GeneratedValue
    private long id;

    @OneToMany
    @Fetch(FetchMode.SELECT)
    private List<Product> products;

    @OneToMany
    @Fetch(FetchMode.SELECT)
    private List<Tag> tags;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Product> getProducts() {
        return this.products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Tag> getTags() {
        return this.tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

}
