RMSLIB=config/messages/:jars/IxoraCommon.jar:jars/RMS.jar:jars/RMSCommon.jar:lib/commons-httpclient.jar:lib/commons-codec.jar:lib/commons-codec.jar:lib/commons-logging-api.jar:lib/jcommon.jar:lib/log4j.jar:lib/mailapi.jar:lib/xalan.jar:lib/xerces.jar:lib/xercesImpl.jar:lib/xml-apis.jar:
CLASSPATH="$RMSLIB"
PATH=./jre/bin:$PATH
export PATH
MYPWD=`pwd`
export MYPWD
SYSOPTS="-Dapplication.home=""$MYPWD"" -Djava.security.policy=java.policy -Djavax.xml.transform.TransformerFactory=org.apache.xalan.processor.TransformerFactoryImpl -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl"
export SYSOPTS
java $SYSOPTS -cp $CLASSPATH com.ixora.rms.batch.starter.BatchStop
