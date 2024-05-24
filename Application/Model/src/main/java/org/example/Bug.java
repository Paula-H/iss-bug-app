package org.example;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

@Entity
@Table( name = "Bug" )
public class Bug extends org.example.Entity<Integer>{
    private String name;
    private String description;
    private BugStatus status;
    private Integer solvedByDeveloperID;
    private Integer foundByTesterID;

    public Bug(String name, String description, BugStatus status, Tester foundBy) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.solvedByDeveloperID = -1;
        this.foundByTesterID = foundBy.getId();
    }

    public Bug() {
        name = "";
        description = "";
        solvedByDeveloperID = null;
        foundByTesterID = null;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "status")
    public BugStatus getStatus() {
        return status;
    }

    public void setStatus(BugStatus status) {
        this.status = status;
    }

    @Column(name = "solvedBy")
    public Integer getSolvedBy() {
        return solvedByDeveloperID;
    }

    public void setSolvedBy(Integer solvedBy) {
        this.solvedByDeveloperID = solvedBy;
    }

    @Column(name = "foundBy")
    public Integer getFoundBy() {
        return foundByTesterID;
    }

    public void setFoundBy(Integer foundBy) {
        this.foundByTesterID = foundBy;
    }

    @Override
    @Id
    @GeneratedValue(generator="increment")
    public Integer getId() {
        return super.getId();
    }

    public void setId(Integer id){
        super.setId(id);
    }

}
