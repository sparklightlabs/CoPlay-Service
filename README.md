# CoPlay-Service
 
## What Is CoPlay-Service?
 
CoPlay-Service together with the CoPlay-Launcher and CoPlay developer integrations make up the 3 parts of the CoPlay Accessibility Framework. CoPlay-Service is the background process that runs the http server, handles requests and triggers actions on the Quest/Go/Android VR device.
 
CoPlay-Launcher is the VR frontend used to launch and communicate with CoPlay-Service. The CoPlay developer integration allows you to create actions and events within your app that can be triggered by CoPlay.
 
CoPlay-Service is meant to be used in conjunction with CoPlay-Launcher.  Any changes or improvements made to core CoPlay functionality can be made in CoPlay-Service, then exported as a library (aar) and included in the CoPlay-Launcher unity project as a plugin. More details on that can be found in the CoPlay-Launcher Repo.
 
## Development Quick Start Guide
 
CoPlay-Service can be built as a 2d app that can run on a phone or VR device (though we don't recommend 2d mode for the quest or Go. The OS doesn't like it when 2D apps try and launch services). 2D mode allows for rapid iteration without deploying to a VR device. CoPlay-Service is primarily meant to be built as a Plugin to be used with CoPlay-Launcher.
 
To build in 2D mode, Copy the contents of app/application_grade and paste it into app/build.gradle. To build the library for export copy the contents of app/library_gradle into app/build.gradle. These two files (application_gradle and library_gradle) are just alternate gradle files that can be swapped in depending on your needs.
 
If you've made changes and wish to export them, first build them then search for a file with .aar extension in the project directory. You'll notice that the there are two aar files, app.aar (or app-debug.aar) and data.aar (or data-debug.aar). You'll need both for CoPlay Launcher (Data is where the http server lives and App is where the service lives). Copy those .aar files and place them in the plugins directory of the CoPlay-Launcher unity project. Again more details can be found in that repo.
 
If you want to get a sense for where important things are at, I suggest looking through the most recent commit history.
 
## Just a heads up
 
CoPlay is a streaming app. It's not real time, but it's pretty good.  There can be an expected delay of at least 0.5-1 second or more on bad WiFi or on heavy CPU load by other apps.<br>
 
CoPlay will throttle before game frame rates are affected.
 
You don't need an internet connection but you will need wifi. Some WiFi networks (mostly public/guest) block connections between its clients for security reasons, so you may not be able to connect to the device via WiFi. Fast and stable WiFi recommended (preferably 5ghz) because of high traffic and low network delay requirement.
 
The number of client connections is unlimited, but be aware that each of them requires some CPU resources and bandwidth to send data.
 
CoPlay is a heavily modified fork of ScreenStream by Dmitriy Krivoruchko <https://github.com/dkrivoruchko/ScreenStream>. He is a rockstar His source is licensed as MIT, My additions are open sourced but under the AGPL license. For more information, please see below.
 
Also, you may notice that the code for CoPlay Hub is not included in the repo. It will still be available in our releases. We are very excited about the potential for CoPlay Hub. Keep an eye out for more information.
 
## License
 
CoPlay-Service is Licensed as AGPL. This means that you are free to clone and use as you like (commercially or otherwise) but requires that sources are made available for any changes made to CoPlay-Service or if CoPlay-Service source code is integrated into any product. We want to ensure that improvements made to the service can be enjoyed by everybody.
 
We expect CoPlay and The CoPlay developer integrations to be used by enterprises and institutions alike and want to make it crystal clear that using the CoPlay developer integration **does not** tie your app to the AGPL license. The developer integration is written to be very loosely coupled with the CoPlay service and is also licensed differently (MIT). We expect CoPlay to be shipped with apps that use it's developer integrations and bundling the two also **does not** tie your app to the AGPL license. So no worries there :)

