package eu.recred.fidouafsvc.model;


import javax.persistence.*;

/**
 * Created by georgeg on 02/07/16.
 */
@Entity
@Table(name = "TRUSTEDFACET")
public class TrustedFacet {

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="NAME", unique = true)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;


    ///////////////////////////////////////////////////////////////////////////////////////////////
    //public int getId() { return this.id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
