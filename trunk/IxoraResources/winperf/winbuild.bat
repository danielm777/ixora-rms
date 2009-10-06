call "C:\Program Files\Microsoft Visual Studio .NET\Vc7\bin\vcvars32.bat"
devenv vc7winperf.sln /build debug

rem set MINC=..\..\..\Tools\minc\bin
rem cd lib
rem dlltool --input-def pdh.def --dllname pdh.dll --add-stdcall-alias --output-lib libpdh.a
rem cd ..
rem "%MINC%\gcc" -shared -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32" -o ..\..\RMS\bin\win\winperf.dll winperf.cpp -Llib -lpdh -lsupc++ -lstdc++

pause