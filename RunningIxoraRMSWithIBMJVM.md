# Introduction #

Problems when running IxoraRMS v1.1 with certain versions of IBM's JVM. They are related to the writing of the XML definition files when saving artefacts.


# Details #

The problem seems to be caused by the XML libraries used by the IBM JVM. To fix this modify the **console.launch** file in **IxoraRMS\_installation\_folder**.

Replace the line that sets SYSOPTS with the data below:

`set SYSOPTS=-Xbootclasspath/p:lib/xalan.jar;lib/xerces.jar;lib/xercesImpl.jar;lib/xml-apis.jar -Xmx256M -Dapplication.home="%CD%" -Dcom.ibm.CORBA.ConfigURL=file:%WAS%/profiles/AppSrv01/properties/sas.client.props -Dcom.ibm.SSL.ConfigURL=file:%WAS%/profiles/AppSrv01/properties/ssl.client.props -Djava.security.policy=java.policy -Djava.library.path=./bin;%SystemRoot%/System32 -Djavax.xml.transform.TransformerFactory=org.apache.xalan.processor.TransformerFactoryImpl -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl`

This makes the XML libraries supplied with IxoraRMS to take precedence in the class path.