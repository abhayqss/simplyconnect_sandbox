package com.scnsoft.eldermark.dto;

import com.itextpdf.text.BaseColor;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;
import java.util.function.Predicate;

public class RectangleDefinition {
    private final Integer leftX;
    private final Integer leftXOffset;
    private final Integer lowerY;
    private final Integer lowerYOffset;

    private final int width;
    private final int height;

    private final Predicate<Integer> applyToPage;
    private final BaseColor color;

    public RectangleDefinition(Integer leftX, Integer leftXOffset,
                               Integer lowerY, Integer lowerYOffset,
                               int width, int height,
                               Predicate<Integer> applyToPage,
                               BaseColor color) {
        Objects.requireNonNull(ObjectUtils.firstNonNull(leftX, leftXOffset));
        Objects.requireNonNull(ObjectUtils.firstNonNull(lowerY, lowerYOffset));
        this.leftX = leftX;
        this.leftXOffset = leftXOffset;
        this.lowerY = lowerY;
        this.lowerYOffset = lowerYOffset;
        this.width = width;
        this.height = height;
        this.applyToPage = applyToPage;
        this.color = color;
    }


    public int resolveLeftX(int pageWitdh) {
        if (leftXOffset == null) {
            return leftX;
        } else {
            return pageWitdh + leftXOffset;
        }
    }

    public int resolveRightX(int pageWitdh) {
        return resolveLeftX(pageWitdh) + width;
    }

    public int resolveUpperY(int pageHeight) {
        return resolveLowerY(pageHeight) + height;
    }

    public int resolveLowerY(int pageHeight) {
        if (lowerYOffset == null) {
            return lowerY;
        } else {
            return pageHeight + lowerYOffset;
        }
    }

    public Predicate<Integer> getApplyToPage() {
        return applyToPage;
    }

    public BaseColor getColor() {
        return color;
    }
}
