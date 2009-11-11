echo off
set ROOT=C:\Dev\redbox\AgentWebSphereProxy\
set WAS_HOME=C:\Programs\IBM\Websphere\AppServer
call "%WAS_HOME%/bin/setupCmdLine.bat" 
rem set WAS_CP=C:\Dev\redbox\AgentWebSphereProxy\classes
set WAS_CP=%WAS_CP%;%WAS_HOME%\lib\pmi.jar
set WAS_CP=%WAS_CP%;%WAS_HOME%\lib\pmiclient.jar
set WAS_CP=%WAS_CP%;%WAS_HOME%\lib\ras.jar
set WAS_CP=%WAS_CP%;%WAS_HOME%\lib\admin.jar
set WAS_CP=%WAS_CP%;%WAS_HOME%\lib\wasjmx.jar
set WAS_CP=%WAS_CP%;%WAS_HOME%\lib\j2ee.jar
set WAS_CP=%WAS_CP%;%WAS_HOME%\lib\soap.jar
set WAS_CP=%WAS_CP%;%WAS_HOME%\lib\soap-sec.jar
set WAS_CP=%WAS_CP%;%WAS_HOME%\lib\nls.jar
set WAS_CP=%WAS_CP%;%WAS_HOME%\lib\wsexception.jar
set WAS_CP=%WAS_CP%;%WAS_HOME%\lib\ws-config-common.jar
set WAS_CP=%WAS_CP%;%WAS_HOME%\lib\namingclient.jar

C:\Java\jdk1.6.0_10\bin\rmic -v1.2 -keep -classpath "%ROOT%\classes;%WAS_CP%" -sourcepath %ROOT%\src -d %ROOT%\src com.ixora.rms.agents.websphere.v50.proxy.PmiClientProxyImpl com.ixora.rms.agents.websphere.v60.proxy.PmiClientProxyImpl

pause