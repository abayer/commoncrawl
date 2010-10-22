<?xml version="1.0" encoding="ISO-8859-1"?> 
<!--
    Note this stylesheet currently requires SAXON as XSLT processor.
    This is not nice.
  -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.1"
    xmlns:x="http://www.w3.org/1999/xhtml"
    xmlns:fsn="http://frank.spieleck.de"
   >

<xsl:import href="docs-project.xsl" />   

<xsl:key name="anchoredDivs" match="x:body//x:div[@id]" 
  use=".//x:a[ancestor::x:div[1] = current()]/@name"/>
<!--
<xsl:key name="linkedDivs" match="x:body//x:div[@id]" 
  use=".//x:a[ancestor::x:div[1] = current()]/@href"/>
  -->
<xsl:variable name="pageDivs" select="/x:html/x:body//x:div[@id]"/>

<xsl:output method="html" encoding="UTF-8" omit-xml-declaration="yes"
            indent="no" 
  />  

<xsl:template match="x:html">/
  <xsl:for-each select="$pageDivs">
    <xsl:message>
      <xsl:value-of select="format-number(position(),'####')"/>
      <xsl:text> </xsl:text>
      <xsl:value-of select="@id"/>
      </xsl:message>
    <xsl:call-template name="makePage"/>
  </xsl:for-each>
</xsl:template>

<xsl:template name="makePage">
  <!--
    spread them one lower nodes xsl:exclude-result-prefixes="fsn">
    -->
  <xsl:variable name="href"><xsl:call-template name="genHref"/></xsl:variable>
  <xsl:document href="{$destpath}{$href}">
    <xsl:call-template name="makeRegularPage"/>
  </xsl:document>
  <xsl:if test="@fsn:print">
    <xsl:document href="{$destpath}print_{$href}">
      <xsl:call-template name="makePrintPage"/>
    </xsl:document>
  </xsl:if>
</xsl:template>

<xsl:template name="makeRegularPage">
  <html>
    <head>
      <xsl:call-template name="makeNonTitleHead"/>
      <title>
        <xsl:apply-templates select="/x:html/x:head/x:title" mode="stripelement"/>
        <xsl:text>, </xsl:text>
        <xsl:apply-templates select="x:h1" mode="stripelement"/>
      </title>
    </head>
    <body background="images/bg.gif">
      <table width="{$fullWidth}" border="0" cellspacing="{$cellspace}" cellpadding="{$cellpad}">
        <tr>
          <td valign="top" width="{$leftWidth}" rowspan="2">
            <xsl:call-template name="placeProjectLogo" />
          </td>
          <td class="small" width="*">
            <xsl:call-template name="makeBranch"/>
          </td>
          <td align="right" class="small">
            <a name="top.of.page">
              <xsl:value-of select="$stamp"/> 
              <xsl:text> v</xsl:text>
              <xsl:value-of select="$version"/> 
            </a>
          </td>
        </tr>
        <tr>
          <td valign="bottom" class="white" colspan="2">
            <xsl:call-template name="makeProjectSlogan" />
            <br/>
            <xsl:apply-templates select="x:h1"/>
          </td>
        </tr>
      </table>
      <table width="{$fullWidth}" border="0" cellspacing="{$cellspace}" cellpadding="{$cellpad}">
        <tr>
          <td class="navbarcell1" align="right" colspan="3">
            <xsl:call-template name="makeProjectNavbar" />
          </td>
        </tr>
      </table>
      <table width="{$fullWidth}" border="0" cellspacing="{$cellspace}" cellpadding="{$cellpad}">
        <tr>
          <td valign="top" width="{$leftWidth}"
              height="270" class="smallwhite">
            <xsl:call-template name="makeList">
              <xsl:with-param name="me" select="."/>
            </xsl:call-template>
            <br/>
            &#160;
          </td>
          <td valign="top" class="white" rowspan="3" colspan="2">
            <xsl:call-template name="makeSpecialLinks"/>
            <xsl:apply-templates select="*[not(name() = 'h1')]"/>
          </td>
        </tr>
        <tr>
          <td valign="top">
            <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td valign="middle" width="{$leftWidth}" class="small"
                    height="20" align="right">
                  <a href="http://sourceforge.net/export/rss2_projnews.php?group_id={$gid}&amp;rss_fulltext=1">Newsfeed<img src="images/xml.png" width="36" height="14" border="0" alt="RSS feed" title="RSS feed"/></a>
                </td>
              </tr>
              <tr>
                <td valign="middle" width="{$leftWidth}" class="small"
                    height="20" align="right">
                  <a href="http://sourceforge.net/export/rss2_projfiles.php?group_id={$gid}">Filefeed<img src="images/xml.png" width="36" height="14" border="0" alt="RSS feed" title="RSS feed"/></a>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td valign="center" width="{$leftWidth}"
              height="100%" class="small">
            <a href="http://sourceforge.net"><img src="http://sourceforge.net/sflogo.php?group_id={$gid}&amp;type=2" width="125" height="37" border="0" alt="Sourceforge Logo" title="Sourceforge Logo"/></a>
          </td>
        </tr>
      </table>
      <table width="{$fullWidth}" border="0" cellspacing="{$cellspace}" cellpadding="{$cellpad}">
        <tr>
          <td class="navbarcell1" align="left">
            <a href="http://spieleck.de">A Spieleck Project</a>
          </td>
          <td class="navbarcell1" align="right" colspan="2">
            <xsl:call-template name="makeTopLink"/>
          </td>
        </tr>
      </table>
    </body>
  </html>
</xsl:template>

<xsl:template name="makeTopLink">
  <span class="small">
    <a href="#top.of.page">top</a>
  </span>
</xsl:template>

<xsl:template name="makePrintPage">
  <html>
    <head>
      <xsl:call-template name="makeNonTitleHead"/>
      <title>
        <xsl:apply-templates select="/x:html/x:head/x:title" mode="stripelement"/>
        <xsl:text>, </xsl:text>
        <xsl:apply-templates select="x:h1" mode="stripelement"/>
        <xsl:text>, print version</xsl:text>
      </title>
    </head>
    <body>
      <a href="${absUrl}/index.html">
        <xsl:call-template name="placeProjectLogo" />
      </a>
      <div>
        <!-- Augment the branch by another link to the home -->
        <a href="index.html">
          <xsl:value-of select="$projectName" />
        </a>
        <xsl:text>&gt;&#160;</xsl:text>
        <xsl:call-template name="makeBranch">
          <xsl:with-param name="me" select="/"/>
        </xsl:call-template>
      </div>
      <div class="small">
          <xsl:value-of select="$stamp"/> 
          <xsl:text> </xsl:text>
          <xsl:value-of select="$version"/> 
      </div>
      <h1><xsl:apply-templates select="x:h1"/></h1>
      <xsl:apply-templates select="*[not(name() = 'h1')]">
        <xsl:with-param name="notop">true</xsl:with-param>
      </xsl:apply-templates>
      <a href="http://sourceforge.net"><img src="http://sourceforge.net/sflogo.php?group_id={$gid}&amp;type=2" width="125" height="37" border="0" alt="Sourceforge Logo" title="Sourceforge Logo"/></a>
    </body>
  </html>
</xsl:template>

<xsl:template match="*" name="copy">
  <xsl:element name="{name()}">
    <xsl:copy-of select="@*"/>
    <xsl:apply-templates select="*|text()"/>
  </xsl:element>
</xsl:template>

<xsl:template match="x:h1|x:h2">
  <xsl:param name="notop"/>
  <xsl:if test="not($notop) and preceding-sibling::*[5]">
    <table align="right" border="0" cellpadding="1" cellspacing="0">
      <tr>
        <td class="navbarcell1">
          <xsl:call-template name="makeTopLink"/>
        </td>
      </tr>
    </table>
  </xsl:if>
  <xsl:call-template name="copy"/>
</xsl:template>

<xsl:template name="makeNonTitleHead">
  <xsl:apply-templates select="/x:html/x:head/*[name() != 'title']"/>
  <link rel ="stylesheet" type="text/css" href="stylesheet.css" title="Style"/>
  <xsl:call-template name="makeProjectMetatags"/>
  <meta name="MSSmartTagsPreventParsing" content="TRUE" />
  <meta http-equiv="imagetoolbar" content="no" />
  <meta name="Url">
    <xsl:attribute name="content">
        <xsl:value-of select="$absUrl"/>
        <xsl:text>/</xsl:text>
        <xsl:call-template name="genHref"/>
    </xsl:attribute>
  </meta>
</xsl:template>

<xsl:template name="makeBranch">
  <xsl:param name="ancestors" select="ancestor-or-self::x:div[@id]"/>
  <xsl:param name="me" select="."/>
  <xsl:variable name="count" select="count($ancestors)"/>
  <xsl:if test="@id = 'index'">
    &#160;
  </xsl:if>
  <xsl:if test="$count &gt; 0 and @id != 'index' ">
    <xsl:for-each select="$ancestors[1]">
      <a> 
        <xsl:if test=". != $me">
          <xsl:attribute name="href">
            <xsl:call-template name="genHref"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:apply-templates select="x:h1" mode="stripelement"/>
      </a>
    </xsl:for-each>
    <xsl:if test="$count &gt; 1">
      &gt; 
      <xsl:call-template name="makeBranch">
        <xsl:with-param name="ancestors" select="$ancestors[position() > 1]"/>
        <xsl:with-param name="me" select="$me"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:if>
</xsl:template>

<xsl:template name="makeList">
  <xsl:param name="me"/>
  <xsl:for-each select="$pageDivs">
    <xsl:variable name="depth" select="count(ancestor::x:div[@id])"/>
    <xsl:if test="$depth &lt; $showtree or parent::x:div[@id] = $me or ./parent::x:div[@id]/descendant-or-self::x:div[@id] = $me">
        <!-- . = $me/ancestor::x:div[@id]"> -->
        <!-- or parent::x:div[@id] = $me"> -->
      <br/>
      <xsl:variable name="ancestors" select="ancestor::x:div[@id]"/>
      <xsl:for-each select="$ancestors">
        <xsl:text>&#160;&#160;</xsl:text>
      </xsl:for-each>
      <span>
        <xsl:choose>
          <xsl:when test=". = $me">
            <xsl:attribute name="class">headline-selected</xsl:attribute>
            <xsl:call-template name="pageLabel">
              <xsl:with-param name="me" select="$me"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:if test="count($ancestors) = 0">
              <xsl:attribute name="class">headline</xsl:attribute>
            </xsl:if>
            <a>
              <xsl:attribute name="href">
                <xsl:call-template name="genHref"/>
              </xsl:attribute>
              <xsl:call-template name="pageLabel">
                <xsl:with-param name="me" select="$me"/>
              </xsl:call-template>
            </a>
          </xsl:otherwise>
        </xsl:choose>
      </span>
    </xsl:if>
  </xsl:for-each>
</xsl:template>

<xsl:template name="pageLabel">
  <xsl:param name="me"/>
  <xsl:choose>
    <xsl:when test="x:div[@id] and descendant-or-self::x:div[@id] = $me">
      <!--
      <xsl:text>[-] </xsl:text>
      -->
      <img src="images/minus.gif" width="10" height="10" border="0"/>
    </xsl:when>
    <xsl:when test=". = $me">
      <img src="images/page.gif" width="10" height="10" border="0"/>
    </xsl:when>
    <xsl:when test="x:div[@id] and not(descendant-or-self::x:div[@id] = $me)">
      <img src="images/plus.gif" width="10" height="10" border="0"/>
      <!--
      <xsl:text>[+] </xsl:text>
      -->
    </xsl:when>
    <xsl:otherwise>
      <img src="images/page.gif" width="10" height="10" border="0"/>
    </xsl:otherwise>
  </xsl:choose>
  <xsl:apply-templates select="x:h1" mode="stripelement"/>
</xsl:template>

<xsl:template name="makeListText">
    <xsl:apply-templates select="x:h1" mode="stripelement"/>
</xsl:template>

<xsl:template match="*" mode="stripelement">
  <xsl:apply-templates mode="stripelement"/>
</xsl:template>

<xsl:template name="genHref">
  <xsl:param name="id" select="@id"/>
  <xsl:value-of select="concat($id,'.html')"/>
</xsl:template>

<xsl:template name="lookupPage">
  <xsl:param name="ref" select="substring(@href, 2)"/>
  <xsl:variable name="result" select="key('anchoredDivs',$ref)"/>
  <xsl:if test="not($result)">
    <xsl:message>Illegal link <xsl:value-of select="$ref"/></xsl:message>
  </xsl:if>
  <xsl:if test="count($result) &gt; 1">
    <xsl:message>Ambiguous link <xsl:value-of select="$ref"/>: <xsl:value-of select="count($result)"/></xsl:message>
  </xsl:if>
  <xsl:value-of select="$result[last()]/@id"/>
  <xsl:text>.html</xsl:text>
</xsl:template>

<xsl:template name="internLinkAttr">
  <xsl:attribute name="href">
    <xsl:call-template name="lookupPage"/>
    <xsl:value-of select="@href"/>
  </xsl:attribute>
</xsl:template>

<!-- Convert intra html links into interpage links. -->
<xsl:template match="x:a[starts-with(@href,'#')]">
  <a>
    <xsl:call-template name="internLinkAttr"/>
    <xsl:apply-templates/>
  </a>
</xsl:template>

<xsl:template match="x:div[@id]"/>

<xsl:template name="makeSpecialLinks">
  <xsl:variable name="ql" select="fsn:quicklinks/fsn:link"/>
  <xsl:variable name="id" select="@id"/>
  <xsl:variable name="sharpId" select="concat('#',@id)"/>
  <xsl:variable name="bl">
    <!-- XXX Url logic scattered arround, fix it! -->
    <xsl:for-each select="$pageDivs/x:div[.//x:a/@href = $sharpId or fsn:quicklinks/fsn:link/@href = $sharpId]">
        <xsl:message>- <xsl:value-of select="@id"/></xsl:message>
        <xsl:copy-of select="." />
    </xsl:for-each>
    <!--
    <xsl:for-each select=".//x:a[ancestor::x:div[1] = current() and @name]">
      <xsl:copy-of select="key('linkedDivs', '#developer')"/>
    </xsl:for-each>
    -->
  </xsl:variable>
  <xsl:if test="$ql or count($bl/x:div) > 0 or @fsn:print">
    <table align="right" class="navbarcell1">
      <xsl:if test="@fsn:print">
        <xsl:variable name="href"><xsl:call-template name="genHref"/></xsl:variable>
        <tr>
          <td align="right" valign="top">
            <a href="print_{$href}">
              print version
              <img src="images/printer.gif" border="0" width="16" height="16"/>
            </a>
          </td>
        </tr>
      </xsl:if>
      <xsl:if test="$ql">
        <tr>
          <th>
            <b>Shortcuts:</b>
          </th>
        </tr>
        <xsl:for-each select="$ql">
          <tr>
            <td>
                <a>
                  <xsl:call-template name="internLinkAttr"/>
                  <xsl:value-of select="@text"/>
                </a>
            </td> 
          </tr>
        </xsl:for-each>
      </xsl:if>
      <xsl:if test="count($bl/x:div) > 0">
        <tr>
          <th>
            <b>Backlinks (<xsl:value-of select="count($bl/x:div)"/>):</b>
          </th>
        </tr>
        <xsl:for-each select="$bl/x:div">
          <tr>
            <td>
                <a href="{@id}.html">
                  <xsl:value-of select="x:h1[1]"/>
                </a>
            </td> 
          </tr>
        </xsl:for-each>
      </xsl:if>
    </table>
  </xsl:if>
</xsl:template>

<xsl:template match="fsn:sample">
  <xsl:variable name="full" select="text()"/>
  <a href="sample/{$full}"><code><xsl:value-of select="$full"/></code></a>
</xsl:template>

<xsl:template match="fsn:javadoc|fsn:jd" name="makeJavadoc">
  <xsl:param name="type" select="@type"/>
  <xsl:param name="full" select="text()"/>
  <xsl:variable name="path" select="translate($full,'.','/')"/>
  <code>
    <xsl:choose>
      <xsl:when test="$type='package'">
        <a href="{$javadoc.path}{$path}/package-summary.html">
          <xsl:value-of select="$full"/>
        </a>
      </xsl:when>
      <xsl:when test="$type='fullclass'">
        <a href="{$javadoc.path}{$path}.html">
          <xsl:value-of select="$full"/>
        </a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="short">
          <xsl:call-template name="shortClass">
            <xsl:with-param name="long" select="$full"/>
          </xsl:call-template>
        </xsl:variable>
        <a href="{$javadoc.path}{$path}.html">
          <xsl:value-of select="$short"/>
        </a>
      </xsl:otherwise>
    </xsl:choose>
  </code>
</xsl:template>


<!--
  descriptions
  -->
<xsl:template match="*" mode="description">
  <xsl:apply-templates mode="description"/>
</xsl:template>

<xsl:template match="lead" mode="description">
  <p><b><xsl:apply-templates mode="description"/></b></p>
</xsl:template>

<xsl:template match="details" mode="description">
  <xsl:apply-templates mode="description"/>
</xsl:template>

<xsl:template match="link" mode="description">
  <xsl:call-template name="makeJavadoc">
    <xsl:with-param name="type" select="'short'"/>
    <xsl:with-param name="full" select="@ref"/>
  </xsl:call-template>
</xsl:template>

<xsl:template match="author" mode="description"/>
<!--
-->

<xsl:template name="shortClass">
  <xsl:param name="long"/>
  <xsl:variable name="tail" select="substring-after($long, '.')"/>
  <xsl:choose>
    <xsl:when test="not($tail = '')">
      <xsl:call-template name="shortClass">
        <xsl:with-param name="long" select="$tail"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:value-of select="$long"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="fsn:e">
  <code>&lt;<xsl:apply-templates/>&gt;</code>
</xsl:template>

</xsl:stylesheet>
