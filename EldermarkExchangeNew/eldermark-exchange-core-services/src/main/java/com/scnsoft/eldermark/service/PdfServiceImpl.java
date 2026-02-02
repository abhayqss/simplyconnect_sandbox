package com.scnsoft.eldermark.service;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.scnsoft.eldermark.dto.RectangleDefinition;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PdfServiceImpl implements PdfService {
    private static final Logger logger = LoggerFactory.getLogger(PdfServiceImpl.class);

    @Override
    public byte[] convertWordToPdf(byte[] bytes) throws IOException {
        var doc = new XWPFDocument(new ByteArrayInputStream(bytes));
        var pageSize = doc.getDocument().getBody().getSectPr().getPgSz();
        // multiply 20 since BigInteger represents 1/20 Point
        int pointsPerMillimeter = 20;
        pageSize.setH(BigInteger.valueOf((int) PageSize.A4.getHeight() * pointsPerMillimeter));
        pageSize.setW(BigInteger.valueOf((int) PageSize.A4.getWidth() * pointsPerMillimeter));
        var options = PdfOptions.create();
        var out = new ByteArrayOutputStream();
        PdfConverter.getInstance().convert(doc, out, options);
        return out.toByteArray();
    }

    @Override
    public List<Pair<Float, Float>> pagesDimensions(byte[] bytes) throws IOException {
        PdfReader reader = null;
        try {
            reader = new PdfReader(bytes);

            return IntStream.rangeClosed(1, reader.getNumberOfPages())
                    .mapToObj(reader::getPageSize)
                    .map(rect -> new Pair<>(rect.getWidth(), rect.getHeight()))
                    .collect(Collectors.toList());

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Override
    public byte[] writeRectangles(byte[] bytes, List<RectangleDefinition> rectangles) throws IOException {
        if (CollectionUtils.isEmpty(rectangles)) {
            return bytes;
        }
        var baos = new ByteArrayOutputStream();
        PdfReader reader = null;
        PdfStamper stamper = null;
        try (baos) {
            reader = new PdfReader(bytes);
            stamper = new PdfStamper(reader, baos);

            for (int i = 1; i <= reader.getNumberOfPages(); ++i) {
                var ct = stamper.getOverContent(i);
                var pageRect = reader.getPageSize(i);
                var pageHeight = (int) pageRect.getHeight();
                var pageWidth = (int) pageRect.getWidth();

                int finalI = i;
                rectangles.stream()
                        .filter(rectangleDefinition -> rectangleDefinition.getApplyToPage().test(finalI))
                        .map(rectDef -> {
                            var rectangle = new Rectangle(
                                    rectDef.resolveLeftX(pageWidth),
                                    rectDef.resolveLowerY(pageHeight),
                                    rectDef.resolveRightX(pageWidth),
                                    rectDef.resolveUpperY(pageHeight)
                            );
                            rectangle.setBackgroundColor(rectDef.getColor());
                            rectangle.setBorder(0);
                            return rectangle;
                        }).forEach(ct::rectangle);
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
}
