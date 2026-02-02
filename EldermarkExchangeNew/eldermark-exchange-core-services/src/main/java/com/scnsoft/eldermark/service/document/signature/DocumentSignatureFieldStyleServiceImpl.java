package com.scnsoft.eldermark.service.document.signature;

import com.itextpdf.text.pdf.BaseFont;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateField;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateFieldStyle;
import com.scnsoft.eldermark.entity.signature.TemplateFieldStyleType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import static com.scnsoft.eldermark.service.document.signature.DocumentSignaturePdfServiceImpl.TemplateFieldFont.ARIAL;

@Service
public class DocumentSignatureFieldStyleServiceImpl implements DocumentSignatureFieldStyleService {

    private static final float SMALL_FONT_SIZE = 11.0f;
    private static final float BIG_FONT_SIZE = 16.0f;
    private static final DocumentSignaturePdfServiceImpl.TemplateFieldFont DEFAULT_FONT = ARIAL;

    @Override
    public void populateDefaultTextFieldStyles(DocumentSignatureTemplateField field) {

        var location = field.getLocations().get(0);
        var fieldHeight = location.getBottomRightY() - location.getTopLeftY();
        var descentFactor = - DEFAULT_FONT.getFont().getBaseFont().getFontDescriptor(BaseFont.DESCENT, 1f);

        var calculatedFontSize = calculateFontSize(fieldHeight, descentFactor);
        var calculatedFontLeading = calculateFieldLeading(fieldHeight, calculatedFontSize, descentFactor);

        if (field.getStyles() == null) field.setStyles(new ArrayList<>());

        var font = getOrCreateFieldStyle(field, TemplateFieldStyleType.FONT);
        var fontSize = getOrCreateFieldStyle(field, TemplateFieldStyleType.FONT_SIZE);
        var fontLeading = getOrCreateFieldStyle(field, TemplateFieldStyleType.FONT_LEADING);

        font.setValue(DEFAULT_FONT.name());
        fontSize.setValue(String.valueOf(calculatedFontSize));
        fontLeading.setValue(String.valueOf(calculatedFontLeading));

        field.getStyles().clear();
        field.getStyles().add(font);
        field.getStyles().add(fontSize);
        field.getStyles().add(fontLeading);
    }

    private float calculateFontSize(int fieldHeight, float descentFactor) {
        var calculatedFontSize = fieldHeight / (descentFactor + 1);
        if (calculatedFontSize > BIG_FONT_SIZE) {
            calculatedFontSize = BIG_FONT_SIZE;
        } else  if (calculatedFontSize > SMALL_FONT_SIZE) {
            calculatedFontSize = SMALL_FONT_SIZE;
        }
        return calculatedFontSize;
    }

    private float calculateFieldLeading(int fieldHeight, float fontSize, float descentFactor) {
        if (fieldHeight > getMinHeightOfTwoLineField(fontSize, descentFactor)) {
            return fontSize;
        } else {
            return fieldHeight - descentFactor * fontSize;
        }
    }

    private DocumentSignatureTemplateFieldStyle getOrCreateFieldStyle(DocumentSignatureTemplateField field, TemplateFieldStyleType type) {
        return field.getStyles().stream()
                .filter(it -> it.getType() == type)
                .findFirst()
                .orElseGet(() -> {
                    var style = new DocumentSignatureTemplateFieldStyle();
                    style.setType(type);
                    style.setTemplateField(field);
                    return style;
                });
    }

    private float getMinHeightOfTwoLineField(float fontSize, float descentFactor) {
        return fontSize * (2f + descentFactor);
    }
}
