package com.scnsoft.eldermark.entity.signature;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class DocumentSignatureFieldUiLocation {

    @Min(0)
    @Max(1)
    private double topLeftX;
    @Min(0)
    @Max(1)
    private double topLeftY;
    @Min(0)
    @Max(1)
    private double bottomRightX;
    @Min(0)
    @Max(1)
    private double bottomRightY;

    public double getTopLeftX() {
        return topLeftX;
    }

    public void setTopLeftX(double topLeftX) {
        this.topLeftX = topLeftX;
    }

    public double getTopLeftY() {
        return topLeftY;
    }

    public void setTopLeftY(double topLeftY) {
        this.topLeftY = topLeftY;
    }

    public double getBottomRightX() {
        return bottomRightX;
    }

    public void setBottomRightX(double bottomRightX) {
        this.bottomRightX = bottomRightX;
    }

    public double getBottomRightY() {
        return bottomRightY;
    }

    public void setBottomRightY(double bottomRightY) {
        this.bottomRightY = bottomRightY;
    }
}
