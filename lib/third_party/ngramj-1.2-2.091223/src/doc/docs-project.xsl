<?xml version="1.0" encoding="ISO-8859-1"?> 
<!--
    Note this stylesheet currently requires SAXON as XSLT processor.
    This is not nice.
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.1"
    xmlns:x="http://www.w3.org/1999/xhtml"
    xmlns:fsn="http://frank.spieleck.de"
   >

<xsl:param name="javadoc.path">javadoc/</xsl:param>
<xsl:param name="gif">javadoc/</xsl:param>

<xsl:key name="anchoredDivs" match="x:body//x:div[@id]" 
  use=".//x:a[ancestor::x:div[1] = current()]/@name"/>
<xsl:key name="linkedDivs" match="x:body//x:div[@id]" 
  use=".//x:a[ancestor::x:div[1] = current()]/@href"/>

<xsl:variable name="absUrl" select="'http://ngramj.sourceforge.net'"/>
<xsl:variable name="logoWidth" select="'110'"/>
<xsl:variable name="logoHeight" select="'102'"/>
<xsl:variable name="projectName" select="'NGramJ'"/>
<xsl:param name="destpath"/>
<xsl:param name="showtree" select="'1'"/>
<xsl:param name="stamp"/>
<xsl:param name="version"/>
<xsl:param name="gid">41486</xsl:param>

<xsl:variable name="leftWidth" select="170"/>
<xsl:variable name="cellspace" select="'3'"/>
<xsl:variable name="cellpad" select="'3'"/>
<xsl:variable name="fullWidth" select="760"/>
<xsl:output method="html" encoding="UTF-8" omit-xml-declaration="yes"
            indent="no" 
  />  

<xsl:template name="placeProjectLogo">
    <a href="http://doris-frank.de">
      <img src="images/ngramj.gif" width="${logoWidth}" height="${logoHeight}"
        border="0"
        alt="NgramJ logo."
        title="NgramJ logo."
        />
    </a>
</xsl:template>

<xsl:template name="makeProjectNavbar">          
    <span class="small">
      <a href="download.html">Download NgramJ</a>
      <xsl:text>&#160;&#160;|&#160;&#160;</xsl:text>
      <a href="http://sourceforge.net/projects/ngramj">Sourceforge Project Summary</a>
      <xsl:text>&#160;&#160;|&#160;&#160;</xsl:text>
      <a href="http://ngramj.sourceforge.net">NgramJ Online</a>
    </span>
</xsl:template>          

<xsl:template name="makeProjectSlogan">
    <i>NGramJ,</i> smart scanning for document properties.
</xsl:template>

<xsl:template name="makeProjectMetatags">
  <meta name="keywords">
    <xsl:attribute name="content">
      <xsl:text>ngram, ngrams, language recognition, NLP, </xsl:text>
      <xsl:apply-templates select="x:h1" mode="stripelement"/>
      <xsl:text>, natural language processing, stochastic language models, lucene, Java</xsl:text>
    </xsl:attribute>
  </meta>
  <meta name="description">
    <xsl:attribute name="content">
      <xsl:text>NgramJ, a library for ngram operations, </xsl:text>
      <xsl:apply-templates select="x:h1" mode="stripelement"/>
    </xsl:attribute>
  </meta>
</xsl:template>

</xsl:stylesheet>
