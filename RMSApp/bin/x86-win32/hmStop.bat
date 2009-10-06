@echo off
set RMSLIB=config/messages/;jars/IxoraCommon.jar;jars/RMS.jar;jars/RMSCommon.jar;lib/commons-httpclient.jar;lib/commons-codec.jar;lib/commons-codec.jar;lib/commons-logging-api.jar;lib/jcommon.jar;lib/log4j.jar;lib/mailapi.jar;lib/xalan.jar;lib/xerces.jar;lib/xercesImpl.jar;lib/xml-apis.jar;
set CLASSPATH=%AGENTS%;%RMSLIB%
set PATH=jre/bin;%PATH%
set SYSOPTS=-Dapplication.home="%CD%" -Djava.security.policy=java.policy -Djavax.xml.transform.TransformerFactory=org.apache.xalan.processor.TransformerFactoryImpl -Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl -Djavax.xml.parsers.SAXParserFactory=org.apache.xerces.jaxp.SAXParserFactoryImpl
java %SYSOPTS% -cp %CLASSPATH% com.ixora.rms.remote.starter.HostManagerStopper
