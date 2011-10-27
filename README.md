How to Say: an awesome pronunciation guide
===========================================
For users
---------
This is a pronunciation guide application for Android based on [Forvo](http://forvo.com). The app provides word search and pronunciations for every word.

For developers
--------------
How to get a working copy of the project:

 * `mkdir for_howtosay`
 * `cd for_howtosay`
 * clone [johannilsson/android-actionbar](/johannilsson/android-actionbar) or use [my fork](/o2genum/android-actionbar)
 * clone [commonsguy/cwac-endless](/commonsguy/cwac-endless) or use [my fork](/o2genum/cwac-endless)
 * clone [o2genum/forvo-java-api](o2genum/forvo-java-api) and see what do that project need to work (GSON and Apache Commons Codec)
 * fork this project, if you need it
 * clone this project (or your own fork)
 * `cd HowToSay`
 * first two clones are library projects - add them as library projects
 * [third clone](o2genum/forvo-java-api) is a simple Java library - download its JAR and JARs it needs (see its README.md) into `libs` directory
 * `cp src/o2genum/howtosay/ExampleSecret.java src/o2genum/howtosay/Secret.java`, open, read and edit `Secret.java` correspondingly

Now you likely have everything I have in my project directory. Please, read Android Code Style Guidelines (yes, code must be beautiful) and feel free to join the development.

### Project needs translations
Every translation will be appreciated. If you're not familar with git, download [strings.xml](res/values/strings.xml), translate it into your language (guidelines are in the file) and send it to me (my e-mail is o2(at)nm.ru). I think it's possible to do even if you're not familar with Android and XML.

License
-------
The code is licensed under the Apache Software License 2.0. See [LICENSE](LICENSE) file or [online version](http://www.apache.org/licenses/LICENSE-2.0.html). Copyright (c) 2011, Andrey Moiseev.
