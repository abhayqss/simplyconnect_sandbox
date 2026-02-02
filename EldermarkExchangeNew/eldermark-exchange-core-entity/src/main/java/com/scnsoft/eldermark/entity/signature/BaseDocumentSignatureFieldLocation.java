package com.scnsoft.eldermark.entity.signature;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseDocumentSignatureFieldLocation {

    @Column(name = "top_left_x", nullable = false, columnDefinition = "decimal")
    private short topLeftX;

    @Column(name = "top_left_y", nullable = false, columnDefinition = "decimal")
    private short topLeftY;

    @Column(name = "bottom_right_x", nullable = false, columnDefinition = "decimal")
    private short bottomRightX;

    @Column(name = "bottom_right_y", nullable = false, columnDefinition = "decimal")
    private short bottomRightY;

    @Column(name = "page_no", nullable = false)
    private short pageNo;

    public short getTopLeftX() {
        return topLeftX;
    }

    public void setTopLeftX(short topLeftX) {
        this.topLeftX = topLeftX;
    }

    public short getTopLeftY() {
        return topLeftY;
    }

    public void setTopLeftY(short topLeftY) {
        this.topLeftY = topLeftY;
    }

    public short getBottomRightX() {
        return bottomRightX;
    }

    public void setBottomRightX(short bottomRightX) {
        this.bottomRightX = bottomRightX;
    }

    public short getBottomRightY() {
        return bottomRightY;
    }

    public void setBottomRightY(short bottomRightY) {
        this.bottomRightY = bottomRightY;
    }

    public short getPageNo() {
        return pageNo;
    }

    public void setPageNo(short pageNo) {
        this.pageNo = pageNo;
    }
}
