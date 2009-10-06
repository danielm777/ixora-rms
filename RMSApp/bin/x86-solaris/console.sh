RMSLIB=config/messages/:jars/RMS.jar:jars/IxoraCommon.jar:jars/RMSCommon.jar:jars/jfreechart.jar:lib/commons-net.jar:jars/ganymed-ssh2.jar:lib/jcommon.jar:lib/commons-httpclient.jar:lib/commons-codec.jar:lib/commons-logging-api.jar:lib/log4j.jar:lib/xerces.jar:lib/xercesImpl.jar:lib/xml-apis.jar:
CLASSPATH="$RMSLIB"
PATH=./jre/bin:$PATH
export PATH
MYPWD=`pwd`
export MYPWD
SYSOPTS="-Dapplication.home=""$MYPWD"" -Djava.security.policy=java.policy -Djavax.xml.transform.TransformerFactory=org.apache.xalan.processor.TransformerFactoryImpl -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl"
export SYSOPTS
java $SYSOPTS -cp $CLASSPATH com.ixora.rms.ui.starter.RMSStarter console.launch sh
