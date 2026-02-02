package com.scnsoft.eldermark.util.cda;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.mdht.uml.cda.util.CDAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Slightly modified version of {@link CDAUtil#loadPackages()}, which instead of loading cda packages from classpath
 * loads cda packages from provided array of Spring's resources.
 *
 *
 * @see CDAUtil#loadPackages()
 */
public class CustomCDAUtil {
    private static final Logger logger = LoggerFactory.getLogger(CustomCDAUtil.class);
    private static boolean packagesLoaded = false;

    /**
     * counterpart of org.eclipse.mdht.uml.cda.util.CDAUtil#loadPackages()
     *
     * @param dependencyResources resources containing jar files
     */
    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN", justification = "dependencyResources cannot be received from user")
    public static void loadPackages(Resource[] dependencyResources) {
        if (!packagesLoaded) {

            //[COPY-PASTE] from org.eclipse.mdht.uml.cda.util.CDAPackageLoader BEGIN
            // (with minor adjustment to load class not from classpath jar files but from provided resources)
            final String BIN = "bin";

            final String PLUGINXML = "plugin.xml";

//            StringTokenizer st = new StringTokenizer(path1, PATH_SEPARATOR);
            var st = Stream.of(dependencyResources).iterator();

            while (st.hasNext()) {

                var resource = st.next();
                String path = resource.getFilename();

                if (path.endsWith(".jar") || (path.endsWith(".zip"))) {
                    try {
                        processModelPlugin(new ZipInputStream(resource.getInputStream()));
                    } catch (Exception e) {
                        logger.info(ExceptionUtils.getStackTrace(e));
                        // If there is an issue loading the plugin jar - we let
                        // normal processing continue
                    }
                } else if (path.endsWith(BIN)) {

                    String pluginPath = path.substring(0, path.lastIndexOf(BIN)) + PLUGINXML;

                    try {
                        var pluginInputSteam = new FileInputStream(pluginPath);
                        processPluginXML(pluginInputSteam);
                    } catch (Exception e) {

                    }

                }
            }

            packagesLoaded = true;
        }

    }

    @SuppressFBWarnings("XXE_DOCUMENT")
    private static void processPluginXML(InputStream pluginStream)
            throws SAXException, IOException, ParserConfigurationException, XPathExpressionException,
            ClassNotFoundException, SecurityException, NoSuchFieldException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setNamespaceAware(true);

        DocumentBuilder builder;

        Document doc = null;

        XPathExpression expr = null;

        builder = factory.newDocumentBuilder();

        doc = builder.parse(new InputSource(pluginStream)); // zipFile.getInputStream(pluginEntry)));

        XPathFactory xFactory = XPathFactory.newInstance();

        XPath xpath = xFactory.newXPath();

        expr = xpath.compile("//plugin/extension[@point='org.eclipse.emf.ecore.generated_package']/package");

        Object result = expr.evaluate(doc, XPathConstants.NODESET);

        NodeList nodes = (NodeList) result;

        for (int i = 0; i < nodes.getLength(); i++) {

            String packageClass = nodes.item(i).getAttributes().getNamedItem("class").getNodeValue();

            // initializes the CDA package
            if (packageClass != null) {
                Class<?> c = Class.forName(packageClass);
                c.getDeclaredField("eINSTANCE");
                logger.debug("Loaded CDA package, {}", packageClass);
            }
        }
    }

    private static void processModelPlugin(ZipInputStream zipInputStream)
            throws SAXException, IOException, ParserConfigurationException, XPathExpressionException,
            ClassNotFoundException, SecurityException, NoSuchFieldException {

        // Get the plugin.xml
//        ZipEntry pluginEntry = zipFile.getEntry("plugin.xml");
        ZipEntry pluginEntry = findEntryWithNameAndPositionZipStream(zipInputStream, "plugin.xml");


        // if it has a plugin xml
        if (pluginEntry != null) {
//            var pluginEntryStream = new ByteArrayInputStream(zipFile.readNBytes((int) pluginEntry.getSize()));
            processPluginXML(zipInputStream);
        }
    }
    //[COPY-PASTE] from org.eclipse.mdht.uml.cda.util.CDAPackageLoader END

    private static ZipEntry findEntryWithNameAndPositionZipStream(ZipInputStream zipInputStream, String fileName) throws IOException {
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            if (fileName.equals(entry.getName())) {
                return entry;
            }
        }
        return null;
    }
}
