@echo off

::Path to Java
set "javaPath=java"

::Full path to the video selector jar
set "jarPath=%~dp0\VideoSelector.jar"

::Full path to video player exe
set "videoPlayer=-v ^"C:\Program Files (x86)\VideoLAN\VLC\vlc.exe^""

::File extension filter
set "extensionFilter=-f"

::Path to video files
set "videoPath=-p E:\"


:handleArgs
shift
:processArguments
:: Process all arguments in the order received
if defined %0 then (
    set %0
    shift
    goto:processArguments
)


:saveCurDir
set PCD="%CD%"


:launcher
%javaPath% -jar %jarPath% %debug% %extensionFilter% %videoPlayer% %videoPath%


:loadCurDir
cd /D "%PCD%"

:exit
pause
