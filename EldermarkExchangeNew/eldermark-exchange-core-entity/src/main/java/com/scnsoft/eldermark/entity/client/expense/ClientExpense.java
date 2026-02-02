package com.scnsoft.eldermark.entity.client.expense;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.Instant;

@Table(name = "ResidentExpense")
@Entity
public class ClientExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JoinColumn(name = "resident_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Client client;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long clientId;

    @Column(name = "expense_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ClientExpenseType type;

    @Column(name = "cost", nullable = false)
    private Long cost;

    @Formula("(select sum(ce.cost) from ResidentExpense ce where ce.expense_date <= expense_date and ce.resident_id = resident_id)")
    private Long cumulativeCost;

    @Column(name = "comment")
    private String comment;

    @Column(name = "expense_date", nullable = false)
    private Instant date;

    @Column(name = "reported_date", nullable = false)
    private Instant reportedDate;

    @JoinColumn(name = "author_id", nullable = false)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Employee author;

    @Column(name = "author_id", nullable = false, insertable = false, updatable = false)
    private Long authorId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public ClientExpenseType getType() {
        return type;
    }

    public void setType(ClientExpenseType type) {
        this.type = type;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public Long getCumulativeCost() {
        return cumulativeCost;
    }

    public void setCumulativeCost(Long cumulativeCost) {
        this.cumulativeCost = cumulativeCost;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Instant getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(Instant reportedDate) {
        this.reportedDate = reportedDate;
    }

    public Employee getAuthor() {
        return author;
    }

    public void setAuthor(Employee author) {
        this.author = author;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
}
