package com.scnsoft.eldermark.entity.basic;

import javax.persistence.*;

@MappedSuperclass
public class HistoryIdsAwareEntity implements HistoryIdsAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "chain_id")
    private Long chainId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }
}
