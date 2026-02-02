package com.scnsoft.eldermark.hl7v2;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.AbstractGroup;
import ca.uhn.hl7v2.model.AbstractSegment;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Structure;
import ca.uhn.hl7v2.model.v251.segment.MSH;
import ca.uhn.hl7v2.model.v251.segment.PID;
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import com.scnsoft.eldermark.hl7v2.parse.segment.AdtSegmentParser;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * All casts should be performed through util so that if we decide to move to higher version of HL7 we'll just
 * change all casts in this single place. In other places we'll just need to change imports
 */
public class HapiUtils {

    public static PID getPid(Message message) throws HL7Exception {
        return (PID) message.get("PID");
    }

    public static MSH getMSH(Message message) throws HL7Exception {
        return (MSH) message.get("MSH");
    }

    public static PID toPid(Structure segment) {
        return (PID) segment;
    }

    public static <T, R> List<R> convertArray(T[] src, Function<T, R> mapper) {
        return Stream.of(src)
                .map(mapper)
                .collect(Collectors.toList());
    }

    public static <T extends AbstractSegment, R> List<R> convertSegmentList(List<T> src, AdtSegmentParser<R, T> parser,
                                                                            MessageSource messageSource) throws ApplicationException, HL7Exception {
        if (CollectionUtils.isEmpty(src)) {
            return List.of();
        }
        var result = new ArrayList<R>(src.size());
        for (var segment : src) {
            var parsed = parser.parse(segment, messageSource);
            if (parsed != null) {
                result.add(parsed);
            }
        }
        return result;
    }

    public static <T extends AbstractSegment, R> List<R> convertSegmentList(Structure[] src,
                                                                            Class<T> segmentClass,
                                                                            AdtSegmentParser<R, T> parser,
                                                                            MessageSource messageSource) throws ApplicationException, HL7Exception {
        if (ArrayUtils.isEmpty(src)) {
            return List.of();
        }
        var result = new ArrayList<R>(src.length);
        for (var segment : src) {
            var parsed = parser.parse(segmentClass.cast(segment), messageSource);
            if (parsed != null) {
                result.add(parsed);
            }
        }
        return result;
    }

    public static <G extends AbstractGroup, T extends AbstractSegment, R> List<R> convertSegmentListFromGroup(List<G> src,
                                                                                                              Function<G, T> sergmentMapper,
                                                                                                              AdtSegmentParser<R, T> parser,
                                                                                                              MessageSource messageSource) throws ApplicationException, HL7Exception {
        if (CollectionUtils.isEmpty(src)) {
            return List.of();
        }
        var result = new ArrayList<R>(src.size());
        for (var group : src) {
            var parsed = parser.parse(sergmentMapper.apply(group), messageSource);
            if (group != null) {
                result.add(parsed);
            }
        }
        return result;
    }

    public static DefaultHapiContext basicHapiContext() {
        var context = new DefaultHapiContext();
        context.setModelClassFactory(new CanonicalModelClassFactory("2.5.1"));

        return context;
    }
}
