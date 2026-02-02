package com.scnsoft.exchange.audit.model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SyncRange {
    private Date from;
    private Date to;

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public String getDiff() {
        long diff = to.getTime() - from.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        StringBuilder format = new StringBuilder();
        List<Long> args = new ArrayList<Long>();
        if (diff >= 24 * 60 * 60 * 1000) {
            format.append(" %sd");
            args.add(diffDays);
        }
        if (diff >= 60 * 60 * 1000) {
            format.append(" %sh");
            args.add(diffHours);
        }
        if (diff >= 60 * 1000) {
            format.append(" %sm");
            args.add(diffMinutes);
        }
        if (diff >= 1000) {
            format.append(" %ss");
            args.add(diffSeconds);
        }

        return String.format(format.toString(), args.toArray());
    }
}
