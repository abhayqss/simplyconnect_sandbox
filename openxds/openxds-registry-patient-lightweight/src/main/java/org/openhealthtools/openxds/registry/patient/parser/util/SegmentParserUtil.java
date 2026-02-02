package org.openhealthtools.openxds.registry.patient.parser.util;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.AbstractMessage;
import org.openhealthtools.openxds.util.SafeGetNullableUtil;
import org.openhealthtools.openxds.util.Supplier;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SegmentParserUtil {

    private SegmentParserUtil() {
    }

    public static <P extends AbstractMessage, R> List<R> parseList(final int segmentCount,
                                                                   final P message,
                                                                   final BiIntFunction<P, R> segmentGetterFunction
    ) throws ApplicationException, HL7Exception {
        final List<R> result = new ArrayList<R>(segmentCount);
        for (int i = 0; i < segmentCount; i++) {
            final R parsedEntry = segmentGetterFunction.apply(message, i);
            if (parsedEntry != null) {
                result.add(parsedEntry);
            }
        }
        return result;
    }

    public static <S, R> List<R> parseList(final List<S> source,
                                           final Function<S, R> transformFunction) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        final List<R> result = new ArrayList<R>();
        for (final S s : source) {
            result.add(SafeGetNullableUtil.safeNpeGet(new Supplier<R>() {
                @Override
                public R supply() {
                    return transformFunction.apply(s);
                }
            }));
        }
        return result;
    }

    public static <S, R> List<R> parseArray(final S[] source, final Function<S, R> transformFunction) {
        return parseList(Arrays.asList(source), transformFunction);
    }
}
