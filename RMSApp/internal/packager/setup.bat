@echo Unpacking JRE ...

jre\bin\unpack200.exe -r jre\lib\deploy.pack.gz jre\lib\deploy.jar
jre\bin\unpack200.exe -r jre\lib\jce.pack.gz jre\lib\jce.jar
jre\bin\unpack200.exe -r jre\lib\jsse.pack.gz jre\lib\jsse.jar
jre\bin\unpack200.exe -r jre\lib\plugin.pack.gz jre\lib\plugin.jar
jre\bin\unpack200.exe -r jre\lib\rt.pack.gz jre\lib\rt.jar
jre\bin\unpack200.exe -r jre\lib\im\indicim.pack.gz jre\lib\im\indicim.jar
jre\bin\unpack200.exe -r jre\lib\im\thaiim.pack.gz jre\lib\im\thaiim.jar
jre\bin\unpack200.exe -r jre\lib\security\local_policy.pack.gz jre\lib\security\local_policy.jar
jre\bin\unpack200.exe -r jre\lib\security\US_export_policy.pack.gz jre\lib\security\US_export_policy.jar

del setup.jar
del setup.bat

@echo Done. Continuing with install...

jre\bin\javaw -jar setup.jar
