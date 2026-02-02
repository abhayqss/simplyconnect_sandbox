package com.scnsoft.eldermark.dump.bean;

import com.scnsoft.eldermark.dump.model.DumpType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class DumpFilter {

    private DumpType dumpType;
    private Long organizationId;
    private List<Long> residentIds;
    private LocalDateTime from;
    private LocalDateTime to;

    private FileMode fileMode = FileMode.SINGLE;

    public DumpType getDumpType() {
        return dumpType;
    }

    public void setDumpType(DumpType dumpType) {
        this.dumpType = dumpType;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<Long> getResidentIds() {
        return residentIds;
    }

    public void setResidentIds(List<Long> residentIds) {
        this.residentIds = residentIds;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public Instant getFromAtDefaultZone() {
        return from.atZone(ZoneId.systemDefault()).toInstant();
    }

    public Instant getToAtDefaultZone() {
        return to.atZone(ZoneId.systemDefault()).toInstant();
    }

    public FileMode getFileMode() {
        return fileMode;
    }

    public void setFileMode(FileMode fileMode) {
        this.fileMode = fileMode;
    }

    @Override
    public String toString() {
        return "DumpFilter{" +
                "dumpType=" + dumpType +
                ", organizationId=" + organizationId +
                ", residentIds=" + residentIds +
                ", from=" + from +
                ", to=" + to +
                ", fileMode=" + fileMode +
                '}';
    }
}
