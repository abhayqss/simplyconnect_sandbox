package com.scnsoft.eldermark.dto.pdf;

import com.itextpdf.text.Font;

public class PdfFont {
    private Font font;
    private int alignment;
    private float leading;

    public Font getFont() {
        return font;
    }

    public void setFont(final Font font) {
        this.font = font;
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(final int alignment) {
        this.alignment = alignment;
    }

    public float getLeading() {
        return leading;
    }

    public void setLeading(float leading) {
        this.leading = leading;
    }
}
