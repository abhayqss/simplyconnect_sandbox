package com.scnsoft.eldermark.service.document.signature;

import com.scnsoft.eldermark.entity.signature.BaseDocumentSignatureFieldLocation;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureFieldUiLocation;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateFieldLocation;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentSignatureFieldLocationServiceImpl implements DocumentSignatureFieldLocationService {

    @Override
    public void fillUiLocation(DocumentSignatureFieldUiLocation target, BaseDocumentSignatureFieldLocation source, Pair<Float, Float> pageSize) {
        fillUiLocation(target, source, pageSize.getFirst(), pageSize.getSecond(), 0, 0);
    }

    @Override
    public void fillFromUiLocation(DocumentSignatureTemplateFieldLocation target, DocumentSignatureFieldUiLocation source, Pair<Float, Float> pageSize) {
        var width = pageSize.getFirst();
        var height = pageSize.getSecond();

        target.setTopLeftX(((short) (source.getTopLeftX() * width)));
        target.setTopLeftY(((short) (source.getTopLeftY() * height)));
        target.setBottomRightX(((short) (source.getBottomRightX() * width)));
        target.setBottomRightY(((short) (source.getBottomRightY() * height)));
    }

    @Override
    public void fillUiLocation(DocumentSignatureFieldUiLocation target, BaseDocumentSignatureFieldLocation source, List<Pair<Float, Float>> pageSizes, int pageOffset) {
        var totalHeight = pageSizes.stream()
                .mapToDouble(Pair::getSecond)
                .sum();

        var maxWidth = pageSizes.stream()
                .mapToDouble(Pair::getFirst)
                .max()
                .orElseThrow();

        var pageStartX = (maxWidth - pageSizes.get(source.getPageNo() + pageOffset - 1).getFirst()) / 2;

        var pageStartY = pageSizes.subList(0, source.getPageNo() + pageOffset - 1).stream()
                .mapToDouble(Pair::getSecond)
                .sum();

        fillUiLocation(target, source, maxWidth, totalHeight, pageStartX, pageStartY);
    }

    private void fillUiLocation(
            DocumentSignatureFieldUiLocation target,
            BaseDocumentSignatureFieldLocation source,
            double width,
            double height,
            double offsetX,
            double offsetY
    ) {
        target.setTopLeftX((offsetX + source.getTopLeftX()) / width);
        target.setTopLeftY((offsetY + source.getTopLeftY()) / height);
        target.setBottomRightX((offsetX + source.getBottomRightX()) / width);
        target.setBottomRightY((offsetY + source.getBottomRightY()) / height);
    }
}
