package com.scnsoft.scansol.shared;

import java.util.List;

/**
 * Date: 19.05.15
 * Time: 10:47
 */
public class ScanSolResidentsDto {
    List<ScanSolResidentDto> residents;

    public List<ScanSolResidentDto> getResidents () {
        return residents;
    }

    public void setResidents (List<ScanSolResidentDto> residents) {
        this.residents = residents;
    }
}
