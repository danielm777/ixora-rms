echo off
set ROOT=C:\Dev\redbox\RMS
C:\Java\jdk1.6.0_10\bin\rmic -v1.2 -keep -sourcepath %ROOT%\src -classpath %ROOT%\classes;%ROOT%/../IxoraCommon/classes;%ROOT%/../RedboxRemote/classes;%ROOT%/../RMSCommon/classes -d %ROOT%\src com.ixora.remote.HostManagerImpl com.ixora.jobs.remote.JobManagerImpl com.ixora.rms.remote.agents.RemoteAgentManagerImpl com.ixora.rms.remote.agents.RemoteAgentManagerEventHandler com.ixora.jobs.remote.JobManagerEventHandler com.ixora.rms.remote.providers.RemoteProviderManagerImpl com.ixora.rms.remote.providers.RemoteProviderManagerEventHandler
pause
