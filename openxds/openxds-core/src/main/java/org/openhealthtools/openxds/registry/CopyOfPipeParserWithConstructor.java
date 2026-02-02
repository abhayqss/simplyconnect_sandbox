package org.openhealthtools.openxds.registry;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.*;
import ca.uhn.hl7v2.parser.*;
import ca.uhn.hl7v2.util.FilterIterator;
import ca.uhn.hl7v2.util.MessageIterator;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.log.HapiLog;
import ca.uhn.log.HapiLogFactory;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *  This class is copy of PipeParser from hapi v0.5.1
 *  Additional constructor with ModelClassFactory is added.
 *
 *  Since 1.0 the library provides this constructor out of the box, however runtime and compile errors occurs with
 *  this and later versions.
 *
 *  [update hint] Please delete after updating library version.
 *
 */
public class CopyOfPipeParserWithConstructor extends Parser {

    private static final HapiLog log;
    private static final String segDelim = "\r";

    public CopyOfPipeParserWithConstructor() {
    }

    /**
     * Additional constructor to pass model class factory to Parser
     *
     * @param modelClassFactory
     */
    public CopyOfPipeParserWithConstructor(ModelClassFactory modelClassFactory) {
        super(modelClassFactory);
    }

    public String getEncoding(String message) {
        String encoding = null;
        if (message.length() < 4) {
            return null;
        } else {
            boolean ok = true;
            if (!message.startsWith("MSH")) {
                return null;
            } else {
                char fourthChar = message.charAt(3);
                StringTokenizer st = new StringTokenizer(message, String.valueOf("\r"), false);

                while(st.hasMoreTokens()) {
                    String x = st.nextToken();
                    if (x.length() > 0) {
                        if (Character.isWhitespace(x.charAt(0))) {
                            x = stripLeadingWhitespace(x);
                        }

                        if (x.length() >= 4 && x.charAt(3) != fourthChar) {
                            return null;
                        }
                    }
                }

                int nextFieldDelimLoc = 0;

                for(int i = 0; i < 11; ++i) {
                    nextFieldDelimLoc = message.indexOf(fourthChar, nextFieldDelimLoc + 1);
                    if (nextFieldDelimLoc < 0) {
                        return null;
                    }
                }

                if (ok) {
                    encoding = "VB";
                }

                return encoding;
            }
        }
    }

    public String getDefaultEncoding() {
        return "VB";
    }

    public boolean supportsEncoding(String encoding) {
        boolean supports = false;
        if (encoding != null && encoding.equals("VB")) {
            supports = true;
        }

        return supports;
    }

    /** @deprecated */
    public String getMessageStructure(String message) throws HL7Exception, EncodingNotSupportedException {
        return this.getStructure(message).messageStructure;
    }

    private CopyOfPipeParserWithConstructor.MessageStructure getStructure(String message) throws HL7Exception, EncodingNotSupportedException {
        EncodingCharacters ec = getEncodingChars(message);
        String messageStructure = null;
        boolean explicityDefined = true;

        try {
            String[] fields = split(message.substring(0, Math.max(message.indexOf("\r"), message.length())), String.valueOf(ec.getFieldSeparator()));
            String wholeFieldNine = fields[8];
            String[] comps = split(wholeFieldNine, String.valueOf(ec.getComponentSeparator()));
            if (comps.length >= 3) {
                messageStructure = comps[2];
            } else if (comps.length > 0 && comps[0] != null && comps[0].equals("ACK")) {
                messageStructure = "ACK";
            } else {
                if (comps.length != 2) {
                    StringBuffer buf = new StringBuffer("Can't determine message structure from MSH-9: ");
                    buf.append(wholeFieldNine);
                    if (comps.length < 3) {
                        buf.append(" HINT: there are only ");
                        buf.append(comps.length);
                        buf.append(" of 3 components present");
                    }

                    throw new HL7Exception(buf.toString(), 200);
                }

                explicityDefined = false;
                messageStructure = comps[0] + "_" + comps[1];
            }
        } catch (IndexOutOfBoundsException var9) {
            throw new HL7Exception("Can't find message structure (MSH-9-3): " + var9.getMessage(), 200);
        }

        return new CopyOfPipeParserWithConstructor.MessageStructure(messageStructure, explicityDefined);
    }

    private static EncodingCharacters getEncodingChars(String message) {
        return new EncodingCharacters(message.charAt(3), message.substring(4, 8));
    }

    protected Message doParse(String message, String version) throws HL7Exception, EncodingNotSupportedException {
        CopyOfPipeParserWithConstructor.MessageStructure structure = this.getStructure(message);
        Message m = this.instantiateMessage(structure.messageStructure, version, structure.explicitlyDefined);
        MessageIterator messageIter = new MessageIterator(m, "MSH", true);
        FilterIterator.Predicate segmentsOnly = new FilterIterator.Predicate() {
            public boolean evaluate(Object obj) {
                return Segment.class.isAssignableFrom(obj.getClass());
            }
        };
        FilterIterator segmentIter = new FilterIterator(messageIter, segmentsOnly);
        String[] segments = split(message, "\r");
        char delim = '|';

        for(int i = 0; i < segments.length; ++i) {
            if (segments[i] != null && segments[i].length() > 0 && Character.isWhitespace(segments[i].charAt(0))) {
                segments[i] = stripLeadingWhitespace(segments[i]);
            }

            if (segments[i] != null && segments[i].length() >= 3) {
                final String name;
                if (i == 0) {
                    name = segments[i].substring(0, 3);
                    delim = segments[i].charAt(3);
                } else {
                    name = segments[i].substring(0, segments[i].indexOf(delim));
                }

                log.debug("Parsing segment " + name);
                messageIter.setDirection(name);
                FilterIterator.Predicate byDirection = new FilterIterator.Predicate() {
                    public boolean evaluate(Object obj) {
                        Structure s = (Structure)obj;
                        CopyOfPipeParserWithConstructor.log.debug("PipeParser iterating message in direction " + name + " at " + s.getName());
                        return s.getName().matches(name + "\\d*");
                    }
                };
                FilterIterator dirIter = new FilterIterator(segmentIter, byDirection);
                if (dirIter.hasNext()) {
                    this.parse((Segment)dirIter.next(), segments[i], getEncodingChars(message));
                }
            }
        }

        return m;
    }

    public void parse(Segment destination, String segment, EncodingCharacters encodingChars) throws HL7Exception {
        int fieldOffset = 0;
        if (isDelimDefSegment(destination.getName())) {
            fieldOffset = 1;
            Terser.set(destination, 1, 0, 1, 1, String.valueOf(encodingChars.getFieldSeparator()));
        }

        String[] fields = split(segment, String.valueOf(encodingChars.getFieldSeparator()));

        for(int i = 1; i < fields.length; ++i) {
            String[] reps = split(fields[i], String.valueOf(encodingChars.getRepetitionSeparator()));
            if (log.isDebugEnabled()) {
                log.debug(reps.length + "reps delimited by: " + encodingChars.getRepetitionSeparator());
            }

            boolean isMSH2 = isDelimDefSegment(destination.getName()) && i + fieldOffset == 2;
            if (isMSH2) {
                reps = new String[]{fields[i]};
            }

            for(int j = 0; j < reps.length; ++j) {
                try {
                    StringBuffer statusMessage = new StringBuffer("Parsing field ");
                    statusMessage.append(i + fieldOffset);
                    statusMessage.append(" repetition ");
                    statusMessage.append(j);
                    log.debug(statusMessage.toString());
                    Type field = destination.getField(i + fieldOffset, j);
                    if (isMSH2) {
                        Terser.getPrimitive(field, 1, 1).setValue(reps[j]);
                    } else {
                        parse(field, reps[j], encodingChars);
                    }
                } catch (HL7Exception var12) {
                    var12.setFieldPosition(i);
                    var12.setSegmentRepetition(MessageIterator.getIndex(destination.getParent(), destination).rep);
                    var12.setSegmentName(destination.getName());
                    throw var12;
                }
            }
        }

        if (destination.getClass().getName().indexOf("OBX") >= 0) {
            Varies.fixOBX5(destination, this.getFactory());
        }

    }

    private static boolean isDelimDefSegment(String theSegmentName) {
        boolean is = false;
        if (theSegmentName.equals("MSH") || theSegmentName.equals("FHS") || theSegmentName.equals("BHS")) {
            is = true;
        }

        return is;
    }

    private static void parse(Type destinationField, String data, EncodingCharacters encodingCharacters) throws HL7Exception {
        String[] components = split(data, String.valueOf(encodingCharacters.getComponentSeparator()));

        for(int i = 0; i < components.length; ++i) {
            String[] subcomponents = split(components[i], String.valueOf(encodingCharacters.getSubcomponentSeparator()));

            for(int j = 0; j < subcomponents.length; ++j) {
                String val = subcomponents[j];
                if (val != null) {
                    val = Escape.unescape(val, encodingCharacters);
                }

                Terser.getPrimitive(destinationField, i + 1, j + 1).setValue(val);
            }
        }

    }

    private static char getSeparator(boolean subComponents, EncodingCharacters encodingChars) {
        char separator;
        if (subComponents) {
            separator = encodingChars.getSubcomponentSeparator();
        } else {
            separator = encodingChars.getComponentSeparator();
        }

        return separator;
    }

    public static String[] split(String composite, String delim) {
        ArrayList components = new ArrayList();
        if (composite == null) {
            composite = "";
        }

        if (delim == null) {
            delim = "";
        }

        StringTokenizer tok = new StringTokenizer(composite, delim, true);
        boolean previousTokenWasDelim = true;

        while(tok.hasMoreTokens()) {
            String thisTok = tok.nextToken();
            if (thisTok.equals(delim)) {
                if (previousTokenWasDelim) {
                    components.add((Object)null);
                }

                previousTokenWasDelim = true;
            } else {
                components.add(thisTok);
                previousTokenWasDelim = false;
            }
        }

        String[] ret = new String[components.size()];

        for(int i = 0; i < components.size(); ++i) {
            ret[i] = (String)components.get(i);
        }

        return ret;
    }

    public static String encode(Type source, EncodingCharacters encodingChars) {
        StringBuffer field = new StringBuffer();

        for(int i = 1; i <= Terser.numComponents(source); ++i) {
            StringBuffer comp = new StringBuffer();

            for(int j = 1; j <= Terser.numSubComponents(source, i); ++j) {
                Primitive p = Terser.getPrimitive(source, i, j);
                comp.append(encodePrimitive(p, encodingChars));
                comp.append(encodingChars.getSubcomponentSeparator());
            }

            field.append(stripExtraDelimiters(comp.toString(), encodingChars.getSubcomponentSeparator()));
            field.append(encodingChars.getComponentSeparator());
        }

        return stripExtraDelimiters(field.toString(), encodingChars.getComponentSeparator());
    }

    private static String encodePrimitive(Primitive p, EncodingCharacters encodingChars) {
        String val = p.getValue();
        if (val == null) {
            val = "";
        } else {
            val = Escape.escape(val, encodingChars);
        }

        return val;
    }

    private static String stripExtraDelimiters(String in, char delim) {
        char[] chars = in.toCharArray();
        int c = chars.length - 1;
        boolean found = false;

        while(c >= 0 && !found) {
            if (chars[c--] != delim) {
                found = true;
            }
        }

        String ret = "";
        if (found) {
            ret = String.valueOf(chars, 0, c + 2);
        }

        return ret;
    }

    protected String doEncode(Message source, String encoding) throws HL7Exception, EncodingNotSupportedException {
        if (!this.supportsEncoding(encoding)) {
            throw new EncodingNotSupportedException("This parser does not support the " + encoding + " encoding");
        } else {
            return this.encode(source);
        }
    }

    protected String doEncode(Message source) throws HL7Exception {
        Segment msh = (Segment)source.get("MSH");
        String fieldSepString = Terser.get(msh, 1, 0, 1, 1);
        if (fieldSepString == null) {
            throw new HL7Exception("Can't encode message: MSH-1 (field separator) is missing");
        } else {
            char fieldSep = '|';
            if (fieldSepString != null && fieldSepString.length() > 0) {
                fieldSep = fieldSepString.charAt(0);
            }

            String encCharString = Terser.get(msh, 2, 0, 1, 1);
            if (encCharString == null) {
                throw new HL7Exception("Can't encode message: MSH-2 (encoding characters) is missing");
            } else if (encCharString.length() != 4) {
                throw new HL7Exception("Encoding characters '" + encCharString + "' invalid -- must be 4 characters", 102);
            } else {
                EncodingCharacters en = new EncodingCharacters(fieldSep, encCharString);
                return encode((Group)source, en);
            }
        }
    }

    public static String encode(Group source, EncodingCharacters encodingChars) throws HL7Exception {
        StringBuffer result = new StringBuffer();
        String[] names = source.getNames();

        for(int i = 0; i < names.length; ++i) {
            Structure[] reps = source.getAll(names[i]);

            for(int rep = 0; rep < reps.length; ++rep) {
                if (reps[rep] instanceof Group) {
                    result.append(encode((Group)reps[rep], encodingChars));
                } else {
                    String segString = encode((Segment)reps[rep], encodingChars);
                    if (segString.length() >= 4) {
                        result.append(segString);
                        result.append('\r');
                    }
                }
            }
        }

        return result.toString();
    }

    public static String encode(Segment source, EncodingCharacters encodingChars) {
        StringBuffer result = new StringBuffer();
        result.append(source.getName());
        result.append(encodingChars.getFieldSeparator());
        int startAt = 1;
        if (isDelimDefSegment(source.getName())) {
            startAt = 2;
        }

        int numFields = source.numFields();

        for(int i = startAt; i <= numFields; ++i) {
            try {
                Type[] reps = source.getField(i);

                for(int j = 0; j < reps.length; ++j) {
                    String fieldText = encode(reps[j], encodingChars);
                    if (isDelimDefSegment(source.getName()) && i == 2) {
                        fieldText = Escape.unescape(fieldText, encodingChars);
                    }

                    result.append(fieldText);
                    if (j < reps.length - 1) {
                        result.append(encodingChars.getRepetitionSeparator());
                    }
                }
            } catch (HL7Exception var9) {
                log.error("Error while encoding segment: ", var9);
            }

            result.append(encodingChars.getFieldSeparator());
        }

        return stripExtraDelimiters(result.toString(), encodingChars.getFieldSeparator());
    }

    public static String stripLeadingWhitespace(String in) {
        StringBuffer out = new StringBuffer();
        char[] chars = in.toCharArray();

        int c;
        for(c = 0; c < chars.length && Character.isWhitespace(chars[c]); ++c) {
            ;
        }

        for(int i = c; i < chars.length; ++i) {
            out.append(chars[i]);
        }

        return out.toString();
    }

    public Segment getCriticalResponseData(String message) throws HL7Exception {
        int locStartMSH = message.indexOf("MSH");
        if (locStartMSH < 0) {
            throw new HL7Exception("Couldn't find MSH segment in message: " + message, 100);
        } else {
            int locEndMSH = message.indexOf(13, locStartMSH + 1);
            if (locEndMSH < 0) {
                locEndMSH = message.length();
            }

            String mshString = message.substring(locStartMSH, locEndMSH);
            char fieldSep = mshString.charAt(3);
            String[] fields = split(mshString, String.valueOf(fieldSep));
            Segment msh = null;

            try {
                String encChars = fields[1];
                char compSep = encChars.charAt(0);
                String messControlID = fields[9];
                String[] procIDComps = split(fields[10], String.valueOf(compSep));
                String version = "2.4";

                try {
                    version = this.getVersion(message);
                } catch (Exception var14) {
                    ;
                }

                msh = Parser.makeControlMSH(version, this.getFactory());
                Terser.set(msh, 1, 0, 1, 1, String.valueOf(fieldSep));
                Terser.set(msh, 2, 0, 1, 1, encChars);
                Terser.set(msh, 10, 0, 1, 1, messControlID);
                Terser.set(msh, 11, 0, 1, 1, procIDComps[0]);
                Terser.set(msh, 12, 0, 1, 1, version);
                return msh;
            } catch (Exception var15) {
                throw new HL7Exception("Can't parse critical fields from MSH segment (" + var15.getClass().getName() + ": " + var15.getMessage() + "): " + mshString, 101, var15);
            }
        }
    }

    public String getAckID(String message) {
        String ackID = null;
        int startMSA = message.indexOf("\rMSA");
        if (startMSA >= 0) {
            int startFieldOne = startMSA + 5;
            char fieldDelim = message.charAt(startFieldOne - 1);
            int start = message.indexOf(fieldDelim, startFieldOne) + 1;
            int end = message.indexOf(fieldDelim, start);
            int segEnd = message.indexOf(String.valueOf("\r"), start);
            if (segEnd > start && segEnd < end) {
                end = segEnd;
            }

            if (end < 0) {
                if (message.charAt(message.length() - 1) == '\r') {
                    end = message.length() - 1;
                } else {
                    end = message.length();
                }
            }

            if (start > 0 && end > start) {
                ackID = message.substring(start, end);
            }
        }

        log.debug("ACK ID: " + ackID);
        return ackID;
    }

    public String getVersion(String message) throws HL7Exception {
        int startMSH = message.indexOf("MSH");
        int endMSH = message.indexOf("\r", startMSH);
        if (endMSH < 0) {
            endMSH = message.length();
        }

        String msh = message.substring(startMSH, endMSH);
        String fieldSep = null;
        if (msh.length() > 3) {
            fieldSep = String.valueOf(msh.charAt(3));
            String[] fields = split(msh, fieldSep);
            String compSep = null;
            if (fields.length >= 2 && fields[1] != null && fields[1].length() == 4) {
                compSep = String.valueOf(fields[1].charAt(0));
                String version = null;
                if (fields.length >= 12) {
                    String[] comp = split(fields[11], compSep);
                    if (comp.length >= 1) {
                        version = comp[0];
                        return version;
                    } else {
                        throw new HL7Exception("Can't find version ID - MSH.12 is " + fields[11], 101);
                    }
                } else {
                    throw new HL7Exception("Can't find version ID - MSH has only " + fields.length + " fields.", 101);
                }
            } else {
                throw new HL7Exception("Invalid or incomplete encoding characters - MSH-2 is " + fields[1], 101);
            }
        } else {
            throw new HL7Exception("Can't find field separator in MSH: " + msh, 203);
        }
    }

    static {
        log = HapiLogFactory.getHapiLog(PipeParser.class);
    }

    private static class MessageStructure {
        public String messageStructure;
        public boolean explicitlyDefined;

        public MessageStructure(String theMessageStructure, boolean isExplicitlyDefined) {
            this.messageStructure = theMessageStructure;
            this.explicitlyDefined = isExplicitlyDefined;
        }
    }
}
