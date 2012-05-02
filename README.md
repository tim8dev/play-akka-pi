play-akka-pi
============

Why I created it
----------------

A small Application showcasing fault-tolerance, fire-forget message distribution and WebSockets with Akka integration on Play 2.0.
Also Java/Scala interaction is demonstrated (it's real easy to do it :-]).

I put up this example as part of a presentation for my computer science course in school.
We learnt something oldschool about threads and locking and race-conditions, so I felt like spreading some advanced techniques of concurrent and parallel programming.

That's the reason why the application (pi approximation) code is completely 100% Java. It's also coded in "German", so variables etc. are in German, following our in-course-conventions. (We used to write all class names IN_UPPERCASE, but I drop that convention in favor of the somehow wider spread standard, writing them in CamelCase)

Play Code is written in Scala and using englisch variable names, I'm not assuming any of my class members wants to understand that, and if, they won't struggle from this piece of Scala code anyway. ;-)

Pi Approximation distributed over the network using AKKA 2.0 Remote Actors :-)

Usage
-----

First start the Play application with "play run" in the directory "playpi". It will download some dependencies on first run, so be patient :-).
Then connect your browser to http://localhost:9000/ so the classes and server-listener-actor get initialized.

Point the client's browsers to http://server.ip:9000/ and download the client.zip. Unzip it anywhere you want and start the client.jar on your commandline (Windows-User just double-click start.bat). Type in your local IP and then some random port, type in your server-ip and see how the client connects.

Benchmark
---------

Using a precision of 200 digits and message chunks of 8096 parts each, the current record is roughly a speed ("geschwindigkeit") of 6*600k parts of the sum per second (running on a 6-core AMD processor).

When I did the presentation in school, we processed 2500m parts of the sum in some 15 minutes.
Some 10 or 20 clients were connected, each having a power to produce roughly 250k parts of the sum per second. 

Let me know of your results :-)
