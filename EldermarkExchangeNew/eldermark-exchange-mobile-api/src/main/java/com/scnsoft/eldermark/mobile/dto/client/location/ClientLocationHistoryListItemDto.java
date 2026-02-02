package com.scnsoft.eldermark.mobile.dto.client.location;

import com.scnsoft.eldermark.annotations.sort.DefaultSort;
import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ClientLocationHistoryListItemDto implements ClientIdAware {

    private Long id;
    private Long clientId;

    @NotNull
    @DefaultSort(direction = Sort.Direction.DESC)
    private Long seenDatetime;

    @NotNull
    private BigDecimal longitude;

    @NotNull
    private BigDecimal latitude;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getSeenDatetime() {
        return seenDatetime;
    }

    public void setSeenDatetime(Long seenDatetime) {
        this.seenDatetime = seenDatetime;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
}
