What is the pal project
==============

It is a repository of tool functions  - those like 'capitalize', 'shortenWithEllipsis', 'parseIpv4String' etc. which present in almost every project.

In the most cases developer has two alternatives:
1. use 3rd-party libraries (like underscore.js or guava-libraries) even if you need 1-2 functions from them
1. write own implementation but spend the time on it and probably get less quality code

Pal is not a library - it is a repository which allows to get required function implementation and include into your project. So you do not include additional libraries but include only what you need.

How it expected to work
=================

1. developer opens IDE (Jetbrains IDEA/Webstorm/..., Visual Studio, Notepad++, etc.)
2. when developer needs some utility function he just enter what he want (into dialog or directly in code) like 'get next ipv4 address'
3. system gets the corresponing function (with all dependencies) from Pal repository and adds to the user's project (in class/module named Pal by default)
4. ... and this is for any (well, for any supported) language, so if you used some Pal function on C# then you may use the same in Java or Ruby
5. if you did not find any function match your expectation you may write your own following pal guidelines and add to the repository. Even you wrote it on C# it will be available on other languages as well

Important note
================
Pal is for small utility functions, not for professional libraries (like node.js express, java JFreeChart, javascript jQuery, etc.). 
Big libraries use full set of language/platform possibilities, utility functions looks similar on all languages.

Project status
================
For now there is a working prototype - server with 3 predefined functions which can build java and ruby code and plugin for Jetbrains IDEA/Rubymine which allows to insert pal functions into your code.

To try it - see [this instruction](https://github.com/AlexeyGrishin/projectpal/wiki/How-to-work-with-prototype)

In order to build it on your workstation - just clone the github repository and open in Jetbrains IDEA (Community edition is enough). Without IDEA you may build server using maven, but not the plugin.

TODO list
================
List of tasks is not well organized as there is the beginning and I'm not sure what to improve first.

- new functions/features
   - ipv4 addresses manipulation
   - assertions (for input parameters checks)
   - method overload (for strong typization, for weak - type checks)
   - embedded expressions (lambdas)
   - several expressesions, assignments
- unit-tests
- other languages
   - js-client
   - js-server
- server
   - storage (couchdb/mysql ?)
   - cache of generated functions bodies
- client
   - documentation
   - some greetings
   - add includes (import for java, require for ruby) when adding pal function call
- sharing model (users, access rights, approves, hierarchy of servers, ...)
		
