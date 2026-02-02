<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs" version="1.0">
    
    <!-- This is the home for the large libraries used as dependencies for the rendering - DO NOT EDIT -->
    
    <xsl:template name="jquery">
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.12.1/jquery.min.js"/>
    </xsl:template>
    
    <xsl:template name="jquery-ui">
        <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.12.0/jquery-ui.min.js"/>
    </xsl:template>
    
    <xsl:template name="bootstrap-css">
        <link type="text/css" rel="Stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css" />
    </xsl:template>
    
    <xsl:template name="bootstrap-javascript">
        <script type="text/javascript" src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"/>
    </xsl:template>
</xsl:stylesheet>