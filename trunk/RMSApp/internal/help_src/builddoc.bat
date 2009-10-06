set JAVA_HOME=C:\Java\jdk1.6.0_10
set PATH=C:\Java\jdk1.6.0_10\bin;%PATH%

set RMSHOME=C:\Dev\redbox\RMSApp
set RESLIB=C:\Dev\redbox\IxoraResources
set RESHOME=C:\Dev\redbox\IxoraResources\libtools

rem ***** Build Java Help *****
java -cp %RESLIB%/lib/xalan.jar org.apache.xalan.xslt.Process -xsl makeJavaHelp.xsl -in bookHelp.xml
rem mkdir %RMSHOME%\help\images
xcopy images\*.* %RMSHOME%\help\images\*.* /Y /S
copy style.css %RMSHOME%\help\style.css /Y

rem ***** Building Help Index *****
rmdir /S /Q %RMSHOME%\help\JavaHelpSearch
rem the command below does fart -Cr ..\help <div></div> &nbsp;
fart -Cr %RMSHOME%\help\*.* \x3Cdiv\x3E\x3C/div\x3E \x26nbsp;
rem java -jar %RESHOME%\jhindexer.jar -verbose -db ../../help/JavaHelpSearch ../../help

echo RUN INDEX.BAT FROM THE GENERATED HELP FOLDER!!!
pause
