# Introduction #

MicroPsi comes as a plugin to the Eclipse GUI. Within this plugin, you can edit and run agents. Here, we describe how to install the plugin code itself.

# Installation Instructions #

This is the codebase of the MicroPsi plugins. You will only have to follow the next steps if you want to work "under the hood" of MicroPsi.

1) Install Java 1.6

2) Download Eclipse 3.6.x RCP edition

3) Install SVN client and import projects.

You can use any SVN client for this task you want, however there is also a very good eclipse plugin Subversive (http://www.eclipse.org/subversive/http://www.eclipse.org/subversive/) which can be installed by Eclipse update manager.

Alternatively, you can use Subclipse.


4) The repository URL for authorized users is:

https://micropsi.googlecode.com/svn/
Username: <your.account>@gmail.com
Password: Go to the Source tab and click on "googlecode.com password" to have one generated.

(You can also check out without a username, but you won't be able to commit anything.)

5) Importing project

A) With Subversive SVN client:

Open SVN repositories view, select project, right click and Find/Check Out As

B) With some other SVN client:

Use Import Projects option in Package Explorer. (Subclipse will ask your username during commit.)