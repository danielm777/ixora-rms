set RMSDIR=C:\dev\redbox

rem setup the build and output folder and copy common files
rmdir /S /Q IxoraRMS
rmdir /S /Q output
mkdir output
mkdir IxoraRMS
mkdir IxoraRMS\lib
mkdir IxoraRMS\logs
xcopy /H /E /Y /I %RMSDIR%\RMSRemote\config IxoraRMS\config
xcopy /H /E /Y /I %RMSDIR%\RMSApp\jars\*.jar IxoraRMS\jars
xcopy /H /E /Y /I %RMSDIR%\RMSApp\config IxoraRMS\config
xcopy /H /E /Y /I %RMSDIR%\RMSApp\help IxoraRMS\help
copy %RMSDIR%\RedboxResources\lib\*.jar IxoraRMS\lib
copy %RMSDIR%\RMSApp\java.policy IxoraRMS
rmdir /S /Q IxoraRMS\jars\xraw
del IxoraRMS\config\repository\*.zip
del IxoraRMS\config\logs\*.log
del /Q IxoraRMS\config\session\repository\*
copy %RMSDIR%\RMSApp\config\session\repository\winsample.lperf IxoraRMS\config\session\repository

rmdir /S /Q "IxoraRMS\config\agents\Sample Agent"
rmdir /S /Q "IxoraRMS\config\repository\Sample Agent"

rmdir /S /Q "IxoraRMS\config\agents\CustomSQLAgent"
rmdir /S /Q "IxoraRMS\config\repository\CustomSQLAgent"

rmdir /S /Q "IxoraRMS\config\agents\CustomJavaAgent"
rmdir /S /Q "IxoraRMS\config\repository\CustomJavaAgent"

rmdir /S /Q "IxoraRMS\config\agents\agents.jmxjsr77"
rmdir /S /Q "IxoraRMS\config\repository\agents.jmxjsr77"
del /Q "IxoraRMS\config\messages\agents.jmxjsr77.properties"

rmdir /S /Q "IxoraRMS\config\agents\agents.oc4j"
rmdir /S /Q "IxoraRMS\config\repository\agents.oc4j"
del /Q "IxoraRMS\config\messages\agents.oc4j.properties"

del /Q "IxoraRMS\config\history\*"


rem Now go and clean config folders: 
rem 1. session/config.prop - MRU 
rem 2. logchooser/config.prop - logchooser.most_recently_used=""
rem 3. remove all jobs from library
pause

rem *************************************************

rem Creating package for x86-win32-novm
rmdir /S /Q IxoraRMS\bin
del IxoraRMS\*.sh
del IxoraRMS\*.launch
del IxoraRMS\*.bat
xcopy /H /E /Y /I %RMSDIR%\RMSApp\bin\x86-win32\bin\*.dll IxoraRMS\bin
xcopy /H /E /Y /I %RMSDIR%\RMSApp\bin\x86-win32\* IxoraRMS

zip -r output/x86-win32-novm.zip IxoraRMS

rem Creating package for x86-win32-vm
xcopy /H /E /Y /I x86-win32\jre IxoraRMS\jre
zip -r output/x86-win32-vm.zip IxoraRMS

rem *************************************************

rem Creating package for x86-linux-novm
rmdir /S /Q IxoraRMS\bin
rmdir /S /Q IxoraRMS\jre
del IxoraRMS\*.sh
del IxoraRMS\*.launch
del IxoraRMS\*.bat
xcopy /H /E /Y /I %RMSDIR%\RMSApp\bin\x86-linux\bin\*.so IxoraRMS\bin
xcopy /H /E /Y /I %RMSDIR%\RMSApp\bin\x86-linux\* IxoraRMS

chmod 777 IxoraRMS/*.sh
chmod 777 IxoraRMS/bin/*.so
tar -czvf output/x86-linux-novm.tar.gz IxoraRMS

rem Creating package for x86-linux-vm
xcopy /H /E /Y /I x86-linux\jre IxoraRMS\jre
tar -czvf output/x86-linux-vm.tar.gz IxoraRMS

rem *************************************************

rem Creating package for x86-solaris-novm
rmdir /S /Q IxoraRMS\bin
rmdir /S /Q IxoraRMS\jre
del IxoraRMS\*.sh
del IxoraRMS\*.launch
del IxoraRMS\*.bat
xcopy /H /E /Y /I %RMSDIR%\RMSApp\bin\x86-solaris\bin\*.so IxoraRMS\bin
xcopy /H /E /Y /I %RMSDIR%\RMSApp\bin\x86-solaris\*.sh IxoraRMS
xcopy /H /E /Y /I %RMSDIR%\RMSApp\bin\x86-solaris\*.launch IxoraRMS
chmod 777 IxoraRMS/*.sh
chmod 777 IxoraRMS/bin/*.so
tar -cjvf output/x86-solaris-novm.tar.bzip IxoraRMS

rem Creating package for x86-solaris-vm
xcopy /H /E /Y /I x86-solaris\jre IxoraRMS\jre
tar -cjvf output/x86-solaris-vm.tar.bzip IxoraRMS

rem *************************************************

rem Creating package for sparc-solaris-novm
rmdir /S /Q IxoraRMS\bin
rmdir /S /Q IxoraRMS\jre
del IxoraRMS\*.sh
del IxoraRMS\*.launch
del IxoraRMS\*.bat
xcopy /H /E /Y /I %RMSDIR%\RMSApp\bin\sparc-solaris\bin\*.so IxoraRMS\bin
xcopy /H /E /Y /I %RMSDIR%\RMSApp\bin\sparc-solaris\*.sh IxoraRMS
xcopy /H /E /Y /I %RMSDIR%\RMSApp\bin\sparc-solaris\*.launch IxoraRMS
chmod 777 IxoraRMS/*.sh
chmod 777 IxoraRMS/bin/*.so
tar -cjvf output/sparc-solaris-novm.tar.bzip IxoraRMS

rem Creating package for sparc-solaris-vm
xcopy /H /E /Y /I sparc-solaris\jre IxoraRMS\jre
tar -cjvf output/sparc-solaris-vm.tar.bzip IxoraRMS

rem *************************************************

rem Creating package for ppc-aix-novm
rmdir /S /Q IxoraRMS\bin
rmdir /S /Q IxoraRMS\jre
del IxoraRMS\*.sh
del IxoraRMS\*.launch
del IxoraRMS\*.bat
xcopy /H /E /Y /I %RMSDIR%\RMSApp\bin\ppc-aix\bin\*.so IxoraRMS\bin
xcopy /H /E /Y /I %RMSDIR%\RMSApp\bin\ppc-aix\*.sh IxoraRMS
xcopy /H /E /Y /I %RMSDIR%\RMSApp\bin\ppc-aix\*.launch IxoraRMS
chmod 777 IxoraRMS/*.sh
chmod 777 IxoraRMS/bin/*.so
tar -cjvf output/ppc-aix-novm.tar.bzip IxoraRMS

rem Creating package for ppc-aix-vm
xcopy /H /E /Y /I ppc-aix\jre IxoraRMS\jre
tar -cjvf output/ppc-aix-vm.tar.bzip IxoraRMS

rem *************************************************

rem Creating package for all-novm
rmdir /S /Q IxoraRMS\bin
rmdir /S /Q IxoraRMS\jre
del IxoraRMS\*.sh
del IxoraRMS\*.launch
del IxoraRMS\*.bat
xcopy /H /E /Y /I %RMSDIR%\RMSApp\bin\ppc-aix\bin\*.so IxoraRMS\bin
xcopy /H /E /Y /I %RMSDIR%\RMSApp\bin\ppc-aix\*.sh IxoraRMS
xcopy /H /E /Y /I %RMSDIR%\RMSApp\bin\ppc-aix\*.launch IxoraRMS
del IxoraRMS\bin\*.so
chmod 777 IxoraRMS/*.sh
tar -cvf output/all-novm.tar IxoraRMS

pause
