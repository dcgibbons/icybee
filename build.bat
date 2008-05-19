@echo off
rem
rem  $Id: build.bat,v 1.2 2002/06/07 04:14:54 dcgibbons Exp $
rem Build bootstrap script for Windows NT CMD.EXE
rem
rem IcyBee - http://icybee.sourceforge.net
rem A client for the Internet CB NETwork - http://www.icb.net
rem
rem Copyright (C) 2000-2002 David C. Gibbons, dcgibons@hotmail.com
rem
rem This program is free software; you can redistribute it and/or
rem modify it under the terms of the GNU General Public License
rem as published by the Free Software Foundation; either version 2
rem of the License, or (at your option) any later version.
rem
rem This program is distributed in the hope that it will be useful,
rem but WITHOUT ANY WARRANTY; without even the implied warranty of
rem MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
rem GNU General Public License for more details.
rem
rem You should have received a copy of the GNU General Public License
rem along with this program; if not, write to the Free Software
rem Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
rem

if not "%JAVA_HOME%" == "" goto gotJavaHome
echo You must set the JAVA_HOME environment variable to point at your JDK
goto finish
:gotJavaHome

set _ANT_HOME=..\jakarta-ant
if not "%ANT_HOME%" == "" set _ANT_HOME=%ANT_HOME%
echo Using _ANT_HOME=%_ANT_HOME%

set _CLASSPATH=%_ANT_HOME%\lib\ant.jar;%JAVA_HOME%\lib\tools.jar
if not "%CLASSPATH%" == "" set _CLASSPATH=%_CLASSPATH%;%CLASSPATH%
echo Using _CLASSPATH=%_CLASSPATH%

echo Using _ANT_OPTS=%_ANT_OPTS%

%JAVA_HOME%\bin\java -classpath "%_CLASSPATH%" org.apache.tools.ant.Main %_ANT_OPTS% %1 %2 %3 %4 %5 %6 %7 %8 %9

:finish

