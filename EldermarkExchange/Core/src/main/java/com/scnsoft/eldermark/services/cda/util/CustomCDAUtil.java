package com.scnsoft.eldermark.services.cda.util;

import org.apache.commons.lang3.StringUtils;
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
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * is used to load CDA jar library files into memory
 * org.eclipse.mdht.uml.cda.util.CDAUtil cannot be used since pom jar files are not present in class path of web application
 * CDAUtil assumes that the jar are in class path
 * Created by ggavrysh on 6/8/2018.
 * @see org.eclipse.mdht.uml.cda.util.CDAUtil
 */
public class CustomCDAUtil {
    private static boolean packagesLoaded = false;

    /**
     * counterpart of org.eclipse.mdht.uml.cda.util.CDAUtil#loadPackages()
     * @param webInfFiles absolute paths of all jar files
     */
    public static void loadPackages(final Set<String> webInfFiles) {


        if (!packagesLoaded) {

            final String PATH_SEPARATOR = System.getProperty("path.separator");

            final String JAVA_CLASSPATH = System.getProperty("java.class.path");

            String path1 = StringUtils.join(webInfFiles, PATH_SEPARATOR);

            //[COPY-PASTE] from org.eclipse.mdht.uml.cda.util.CDAPackageLoader BEGIN
            // (with minor adjustment to load class not from classpath jar files but from /WEB_INF/lib )
            final String BIN = "bin";

            final String PLUGINXML = "plugin.xml";

            StringTokenizer st = new StringTokenizer(path1, PATH_SEPARATOR);

            while (st.hasMoreTokens()) {

                String path = st.nextToken();

                if (path.endsWith(".jar") || (path.endsWith(".zip"))) {
                    try {
                        processModelPlugin(new ZipFile(path));
                    } catch (Exception e) {
                        e.printStackTrace();
                        // If there is an issue loading the plugin jar - we let
                        // normal processing continue
                    }
                } else if (path.endsWith(BIN)) {

                    String pluginPath = path.substring(0, path.lastIndexOf(BIN)) + PLUGINXML;

                    try {
                        FileInputStream pluginInputSteam = new FileInputStream(pluginPath);
                        processPluginXML(pluginInputSteam);
                    } catch (Exception e) {

                    }

                }
            }
        }

    }

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
            }

        }
    }

    private static void processModelPlugin(ZipFile zipFile)
            throws SAXException, IOException, ParserConfigurationException, XPathExpressionException,
            ClassNotFoundException, SecurityException, NoSuchFieldException {

        // Get the plugin.xml
        ZipEntry pluginEntry = zipFile.getEntry("plugin.xml");

        // if it has a plugin xml
        if (pluginEntry != null) {

            processPluginXML(zipFile.getInputStream(pluginEntry));

        }
    }
    //[COPY-PASTE] from org.eclipse.mdht.uml.cda.util.CDAPackageLoader END


}
