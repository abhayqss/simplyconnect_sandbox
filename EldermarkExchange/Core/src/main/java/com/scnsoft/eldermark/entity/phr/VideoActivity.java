package com.scnsoft.eldermark.entity.phr;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @author phomal
 * Created on 5/3/2017.
 */
@Entity
@DiscriminatorValue("VIDEO")
public class VideoActivity extends Activity {

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "incoming")
    private Boolean incoming;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Boolean getIncoming() {
        return incoming;
    }

    public void setIncoming(Boolean incoming) {
        this.incoming = incoming;
    }

}
