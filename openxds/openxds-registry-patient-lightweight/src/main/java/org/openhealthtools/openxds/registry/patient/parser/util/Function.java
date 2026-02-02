package org.openhealthtools.openxds.registry.patient.parser.util;

public interface Function<P, R> {
    R apply(P param);
}
