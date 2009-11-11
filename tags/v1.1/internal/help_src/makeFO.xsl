<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'>

<xsl:import href="../../IxoraResources/libtools/docbook/fo/docbook.xsl"/>

<xsl:param name="draft.mode" select="no"/>
<xsl:param name="fop.extensions" select="1"/>
<xsl:param name="chapter.autolabel" select="0"></xsl:param>
<xsl:param name="qanda.defaultlabel">none</xsl:param>


<xsl:param name="generate.toc">
appendix  toc,title
article/appendix  nop
article   toc,title
book      toc,title
chapter   title
part      toc,title
preface   toc,title
qandadiv  toc
qandaset  toc
reference toc,title
sect1     title
sect2     title
sect3     toc
sect4     toc
sect5     toc
section   toc
set       toc,title
</xsl:param>
<xsl:param name="formal.title.placement">
figure after
example before
equation before
table before
procedure before
task before
</xsl:param>

<xsl:attribute-set name="xref.properties">
  <xsl:attribute name="font-weight">bold</xsl:attribute>
</xsl:attribute-set>

<xsl:param name="title.font.family" select="'sans-serif'"></xsl:param>
<xsl:param name="body.font.family" select="'sans-serif'"></xsl:param>

<xsl:param name="title.margin.left">0pc</xsl:param>

</xsl:stylesheet>


