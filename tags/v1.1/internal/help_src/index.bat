set JAVA_HOME=C:\Dev\Tools\jdk1.5.0
set PATH=C:\Dev\Tools\jdk1.5.0\bin;%PATH%

set RESHOME=C:\Dev\redbox\IxoraResources\libtools

java -jar %RESHOME%\jhindexer.jar -verbose *.html

pause
