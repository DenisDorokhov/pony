Pony - Music Streamer
=====================

<b>Pony</b> is an open source music streaming server written in Java.

This project was originally started as a playground for development of my skills in Java and modern web interfaces. Later it turned into something that I really enjoy using for listening to my home MP3 collection. This is a second version of Pony Music Streamer and I've finally decided to finish it as a ready-to-use product. I hope somebody finds it useful ;-)

<b>Status:</b> in development.

## Features

* Optimized for large music collections (tested on hundreds of gigabytes).
* RESTful API.
* Easy-to-use web GUI.
* Library based on contents of MP3 tags.
* Display of music artworks - both MP3-embedded and file based.
* Multi-user system.
* Fully automatic installation.
* Support of multimedia keys in Google Chrome browser with the help of "Media Keys by Sway.fm" extension.

## Known Issues

* Music does not play in IE.
* Needs more testing in different browsers. I test mostly in Google Chrome.
* Needs testing on Windows. I test on Mac OS X and Linux.

## Technologies

* Spring MVC
* Spring Data JPA
* Hibernate
* HSQLDB
* Spring Security
* Token-based authentication system.
* JSR 303 Bean Validation (Hibernate Validator)
* RESTful JSON API
* Google Web Toolkit
* GWT-Platform
* RestyGWT
* Twitter Bootstrap

## Development TODO

* Scanning GUI.
* Settings GUI.
* Edit current user GUI.
* User CRUD GUI.
* Log GUI.
* Search GUI.
* Edit audio tags GUI.
* Playback mode GUI: shuffle, repeat, repeat all, etc.
* REST API documentation.
* More server-side integration tests.
* iOS application.
* Android application.
