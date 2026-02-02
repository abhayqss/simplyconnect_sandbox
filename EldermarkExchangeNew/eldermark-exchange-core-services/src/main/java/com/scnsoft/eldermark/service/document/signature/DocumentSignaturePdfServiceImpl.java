package com.scnsoft.eldermark.service.document.signature;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.scnsoft.eldermark.dto.pdf.PdfFont;
import com.scnsoft.eldermark.entity.signature.BaseDocumentSignatureFieldStyle;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestSubmittedField;
import com.scnsoft.eldermark.entity.signature.SignatureSubmittedFieldType;
import com.scnsoft.eldermark.entity.signature.TemplateFieldStyleType;
import com.scnsoft.eldermark.service.PdfService;
import com.scnsoft.eldermark.service.storage.ImageFileStorage;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class DocumentSignaturePdfServiceImpl implements DocumentSignaturePdfService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentSignaturePdfServiceImpl.class);

    private static final float DEFAULT_FONT_SIZE = 11f;
    private static final float DEFAULT_CHECKBOX_FONT_SIZE = 10f;
    private static final int DEFAULT_TEXT_ALIGNMENT = Element.ALIGN_LEFT;
    private static final String DEFAULT_FONT_FAMILY = FontFactory.TIMES_ROMAN;

    private interface PdfFieldWriter {
        void writeField(DocumentSignatureRequestSubmittedField submittedField, PdfContentByte content, Rectangle fieldRectangle);
    }

    private final Map<SignatureSubmittedFieldType, PdfFieldWriter> pdfFieldWriters = Map.of(
            SignatureSubmittedFieldType.CHECKBOX, this::drawCheckboxField,
            SignatureSubmittedFieldType.TEXT, this::drawTextField,
            SignatureSubmittedFieldType.UNDERLINE, this::drawUnderline,
            SignatureSubmittedFieldType.IMAGE, this::drawImageField
    );
    @Autowired
    private PdfService pdfService;

    @Autowired
    private ImageFileStorage imageFileStorage;

    @Override
    public byte[] writeFieldsToPdf(List<DocumentSignatureRequestSubmittedField> submittedFields, InputStream templateFile) {
        var baos = new ByteArrayOutputStream();
        PdfReader reader = null;
        PdfStamper stamper = null;
        try (baos) {
            reader = new PdfReader(templateFile);
            stamper = new PdfStamper(reader, baos);
            for (DocumentSignatureRequestSubmittedField field : submittedFields) {
                if (field.getFieldType() != null) {
                    writeValuesToPdfByCoordinates(reader, stamper, field);
                }
            }
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        } finally {

            if (stamper != null) {
                try {
                    stamper.close();
                } catch (DocumentException | IOException e) {
                    logger.warn("Exception during closing stamper", e);
                }
            }

            if (reader != null) {
                reader.close();
            }
        }
        return baos.toByteArray();
    }

    @Override
    public List<Pair<Float, Float>> getPdfPageSizes(byte[] bytes) {
        try {
            return pdfService.pagesDimensions(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeValuesToPdfByCoordinates(
            PdfReader reader,
            PdfStamper stamper,
            DocumentSignatureRequestSubmittedField submittedField
    ) throws DocumentException {
        var fieldWriter = pdfFieldWriters.get(submittedField.getFieldType());
        if (fieldWriter != null) {
            var pageNo = submittedField.getPageNo();
            var pageSize = reader.getPageSize(pageNo);

            var content = stamper.getOverContent(pageNo);

            content.saveState();

            var fieldRectangle = getFieldRectangle(submittedField, pageSize);
            fieldWriter.writeField(submittedField, content, fieldRectangle);

            content.restoreState();
        }
    }

    private void drawUnderline(DocumentSignatureRequestSubmittedField submittedField, PdfContentByte content, Rectangle fieldRectangle) {
        content.moveTo(fieldRectangle.getLeft(), fieldRectangle.getBottom());
        content.lineTo(fieldRectangle.getRight(), fieldRectangle.getBottom());
        content.closePathStroke();
    }

    private void drawCheckboxField(DocumentSignatureRequestSubmittedField submittedField, PdfContentByte content, Rectangle fieldRectangle) {
        var pdfFont = extractPdfFont(submittedField.getStyles(), this::createDefaultCheckboxPdfFont);
        var text = Boolean.parseBoolean(submittedField.getValue()) ? "X" : "";
        drawColumnText(content, fieldRectangle, pdfFont, text);
    }


    private void drawTextField(DocumentSignatureRequestSubmittedField submittedField, PdfContentByte content, Rectangle fieldRectangle) {
        var pdfFont = extractPdfFont(submittedField.getStyles(), this::createDefaultPdfFont);
        var text = submittedField.getValue();
        drawColumnText(content, fieldRectangle, pdfFont, text);
    }

    private void drawColumnText(PdfContentByte content, Rectangle fieldRectangle, PdfFont pdfFont, String text) {
        try {
            var ct = new ColumnText(content);
            ct.setSimpleColumn(fieldRectangle);
            ct.setLeading(pdfFont.getLeading());
            ct.setAlignment(pdfFont.getAlignment());
            ct.setText(new Phrase(text, pdfFont.getFont()));
            ct.go();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawImageField(DocumentSignatureRequestSubmittedField submittedField, PdfContentByte content, Rectangle fieldRectangle) {
        var imageFileName = submittedField.getValue();
        if (!imageFileStorage.exists(imageFileName)) {
            return;
        }
        try {

            var image = Image.getInstance(imageFileStorage.loadAsBytes(imageFileName));

            float scaleX = fieldRectangle.getWidth()  / image.getWidth();
            float scaleY = fieldRectangle.getHeight() / image.getHeight();

            if (scaleX < scaleY) {
                image.scalePercent(scaleX * 100);
                var dy = (1 - scaleX) * fieldRectangle.getHeight() / 2;
                image.setAbsolutePosition(
                        fieldRectangle.getLeft(),
                        fieldRectangle.getBottom() + dy
                );
            } else {
                image.scalePercent(scaleY * 100);
                var dx = (1 - scaleY) * fieldRectangle.getWidth() / 2;
                image.setAbsolutePosition(
                        fieldRectangle.getLeft() + dx,
                        fieldRectangle.getBottom()
                );
            }

            content.addImage(image);
        } catch (IOException | DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private Rectangle getFieldRectangle(DocumentSignatureRequestSubmittedField submittedField, Rectangle pageSize) {
        return new Rectangle(
                submittedField.getTopLeftX(),
                Math.round(pageSize.getHeight() - submittedField.getBottomRightY()),
                submittedField.getBottomRightX(),
                Math.round(pageSize.getHeight() - submittedField.getTopLeftY())
        );
    }

    private PdfFont extractPdfFont(List<? extends BaseDocumentSignatureFieldStyle> fieldStyles, Supplier<PdfFont> fallbackFont) {
        return CollectionUtils.isNotEmpty(fieldStyles)
                ? extractPdfFont(fieldStyles)
                : fallbackFont.get();
    }

    private PdfFont extractPdfFont(List<? extends BaseDocumentSignatureFieldStyle> fieldStyles) {
        var pdfFont = new PdfFont();
        var font = extractFont(fieldStyles);
        font.setStyle(extractFontStyle(fieldStyles));
        font.setSize(extractFontSize(fieldStyles));
        pdfFont.setAlignment(extractTextAlignment(fieldStyles));
        pdfFont.setFont(font);
        pdfFont.setLeading(extractLeading(fieldStyles).orElse(font.getSize()));
        return pdfFont;
    }

    private Font extractFont(List<? extends BaseDocumentSignatureFieldStyle> fieldStyles) {
        return fieldStyles.stream()
                .filter(s -> s.getType() == TemplateFieldStyleType.FONT)
                .map(BaseDocumentSignatureFieldStyle::getValue)
                .map(TemplateFieldFont::valueOf)
                .map(TemplateFieldFont::getFont)
                .findFirst()
                .orElseGet(() -> {
                    var font = new Font();
                    font.setFamily(extractFontFamily(fieldStyles));
                    return font;
                });
    }

    private int extractTextAlignment(final List<? extends BaseDocumentSignatureFieldStyle> fieldStyles) {
        return fieldStyles.stream()
                .filter(style -> style.getType() == TemplateFieldStyleType.TEXT_ALIGNMENT)
                .map(BaseDocumentSignatureFieldStyle::getValue)
                .map(TemplateFieldTextAlignment::valueOf)
                .map(TemplateFieldTextAlignment::getValue)
                .findFirst()
                .orElse(DEFAULT_TEXT_ALIGNMENT);
    }

    private String extractFontFamily(final List<? extends BaseDocumentSignatureFieldStyle> fieldStyles) {
        return fieldStyles.stream()
                .filter(style -> style.getType() == TemplateFieldStyleType.FONT_FAMILY)
                .map(BaseDocumentSignatureFieldStyle::getValue)
                .map(TemplateFieldFontFamily::valueOf)
                .map(TemplateFieldFontFamily::getValue)
                .findFirst()
                .orElse(DEFAULT_FONT_FAMILY);
    }

    private float extractFontSize(final List<? extends BaseDocumentSignatureFieldStyle> fieldStyles) {
        return fieldStyles.stream()
                .filter(style -> style.getType() == TemplateFieldStyleType.FONT_SIZE)
                .map(style -> Float.parseFloat(style.getValue()))
                .findFirst()
                .orElse(DEFAULT_FONT_SIZE);
    }

    private Optional<Float> extractLeading(final List<? extends BaseDocumentSignatureFieldStyle> fieldStyles) {
        return fieldStyles.stream()
                .filter(style -> style.getType() == TemplateFieldStyleType.FONT_LEADING)
                .map(style -> Float.parseFloat(style.getValue()))
                .findFirst();
    }

    private int extractFontStyle(final List<? extends BaseDocumentSignatureFieldStyle> fieldStyles) {
        var fontStyles = fieldStyles
                .stream()
                .filter(fieldStyle -> fieldStyle.getType() == TemplateFieldStyleType.FONT_STYLE)
                .map(BaseDocumentSignatureFieldStyle::getValue)
                .map(TemplateFieldFontStyle::valueOf)
                .map(TemplateFieldFontStyle::getValue)
                .mapToInt(Integer::intValue)
                .toArray();
        return TemplateFieldFontStyle.findBitwiseOr(fontStyles);
    }

    private PdfFont createDefaultPdfFont() {
        PdfFont pdfFont = new PdfFont();
        pdfFont.setFont(new Font(Font.FontFamily.TIMES_ROMAN, DEFAULT_FONT_SIZE, Font.NORMAL));
        pdfFont.setAlignment(Element.ALIGN_LEFT);
        pdfFont.setLeading(DEFAULT_FONT_SIZE);
        return pdfFont;
    }

    private PdfFont createDefaultCheckboxPdfFont() {
        PdfFont pdfFont = new PdfFont();
        pdfFont.setFont(new Font(Font.FontFamily.HELVETICA, DEFAULT_CHECKBOX_FONT_SIZE, Font.NORMAL));
        pdfFont.setAlignment(Element.ALIGN_CENTER);
        return pdfFont;
    }

    public enum TemplateFieldFontStyle {
        NORMAL(Font.NORMAL),
        BOLD(Font.BOLD),
        ITALIC(Font.ITALIC),
        BOLDITALIC(Font.BOLDITALIC),
        STRIKETHRU(Font.STRIKETHRU),
        UNDERLINE(Font.UNDERLINE);

        private final int value;

        TemplateFieldFontStyle(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static int findBitwiseOr(int[] arr) {
            if (arr.length > 0) {
                int result = arr[0];
                for (int j : arr) {
                    result = (result | j);
                }
                return result;
            }
            return 0;
        }
    }

    public enum TemplateFieldTextAlignment {
        ALIGN_LEFT(Element.ALIGN_LEFT),
        ALIGN_CENTER(Element.ALIGN_CENTER),
        ALIGN_RIGHT(Element.ALIGN_RIGHT);

        private final int value;

        TemplateFieldTextAlignment(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum TemplateFieldFont {
        ARIAL("/documents/font/arial.ttf");

        private final String fontName;

        TemplateFieldFont(String fontName) {
            this.fontName = fontName;
        }

        public Font getFont() {
            return FontFactory.getFont(fontName, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        }
    }

    public enum TemplateFieldFontFamily {
        TIMES_ROMAN(FontFactory.TIMES_ROMAN),
        HELVETICA(FontFactory.HELVETICA),
        COURIER(FontFactory.COURIER);

        private final String value;

        TemplateFieldFontFamily(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
