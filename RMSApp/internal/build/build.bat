rem Building documentation
cd doc_src
call builddoc.bat
cd ..

rem Building jars
set ANT_HOME=c:\dev\tools\eclipse\plugins\org.apache.ant_1.6.3\lib
java -cp %ANT_HOME%\ant.jar;%ANT_HOME%\ant-launcher.jar org.apache.tools.ant.launch.Launcher -file build.xml obfuscate 

pause
