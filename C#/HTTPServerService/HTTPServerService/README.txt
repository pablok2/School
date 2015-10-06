Pavel Gorelov
Project 2 - Simple HTTP Server
COS 460
C#
-----------------------------------------
	My experience with this project was fun. I liked how
we built on top of the Telnet project into real world
parsing of the internet. I learned some more strengths
and weaknesses I have. I got the basic html code up and
running rather quickly to make sure I knew what I was doing.
The difficulty came later when I realized that the way I was
organizing my parsing was messy.
	Consequently I spent a lot of time tidying things up. But
along the way I was fixing bugs. I got HTML and picures to
work ok. For some reason I was struggling getting audio and video
to work, I keep getting fragment requests. I didn't complete
implementing the audio/video.
	In general I enjoyed this project, I had difficulties setting
up the server as a Windows service, but it was a good experience.
And it worked out in the end. Good project, a good challenge, and
it leaves me anticipating the next project.
-------------------------------------------

To COMPILE and SETUP the Windows service:
Compile as you normally would in Visual Studio.
Since the project is a Windows Service, it'll need
to be installed using the "installutil.exe" tool.
This tool is in Start > Program Files > Visual Studio >
Developer Command Prompt for VS20xx (Run it as an
administrator). In the command line, change directory
to where the binary is located. Run the command:

installutil <the compiled exe>

Now the service will appear in the Windows local Services
(Control Panel > Administrative Tools > Services) 
as "Simple HTTP Server". Open up the properties and
before starting the service, enter the root directory
followed by the server port as arguments in "Start parameters:".
The root directory is defaulted to "C:\" and port 80
if the arguments are left blank.