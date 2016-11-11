package eu.recred.fidouafsvc.model;

import javax.persistence.*;

/**
 * Created by sorin.teican on 8/29/2016.
 */
@Entity
@Table(name = "REGISTRATIONRECORD")
public class RegistrationRecordModel {

    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String authenticator;

    @Column(columnDefinition="TEXT")
    private String record;

    ///////////////////////////////////////////////////////////////////////////////////////////////

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(String authenticator) {
        this.authenticator = authenticator;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }
}
