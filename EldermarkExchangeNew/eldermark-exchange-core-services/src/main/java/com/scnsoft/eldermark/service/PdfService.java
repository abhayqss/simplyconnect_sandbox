package com.scnsoft.eldermark.service;


import com.scnsoft.eldermark.dto.RectangleDefinition;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;

import java.io.IOException;
import java.util.List;

public interface PdfService {
    byte[] convertWordToPdf(byte[] bytes) throws IOException;

    //returns pairs of width, height for each page
    List<Pair<Float, Float>> pagesDimensions(byte[] bytes) throws IOException;

    byte[] writeRectangles(byte[] bytes, List<RectangleDefinition> rectangles) throws IOException;
}
