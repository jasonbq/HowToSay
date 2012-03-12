How to Say: an awesome pronunciation guide
===========================================
For users
---------
This is a pronunciation guide application for Android based on [Forvo](http://forvo.com). The app provides word search and pronunciations for every word. Available in [Android Market](https://market.android.com/details?id=ru.o2genum.howtosay) (search "How to Say" or "pname:ru.o2genum.howtosay").
<p align="center">
<img src="/o2genum/HowToSay/raw/master/etc/png/screenshot-1.png" width="18%"/>&nbsp;
<img src="/o2genum/HowToSay/raw/master/etc/png/screenshot-2.png" width="18%"/>&nbsp;
<img src="/o2genum/HowToSay/raw/master/etc/png/screenshot-3.png" width="18%"/>&nbsp;
<img src="/o2genum/HowToSay/raw/master/etc/png/screenshot-4.png" width="18%"/>&nbsp;
<img src="/o2genum/HowToSay/raw/master/etc/png/screenshot-5.png" width="18%"/>
<br>
<img src="/o2genum/HowToSay/raw/master/etc/png/screenshot-6.png" width="94%"/>
</p>

For developers
--------------
How to get a working copy of the project:

 * `mkdir for_howtosay`
 * `cd for_howtosay`
 * clone [johannilsson/android-actionbar](/johannilsson/android-actionbar) or use [my fork](/o2genum/android-actionbar)
 * clone [commonsguy/cwac-endless](/commonsguy/cwac-endless) or use [my fork](/o2genum/cwac-endless)
 * clone [o2genum/forvo-java-api](/o2genum/forvo-java-api) and see what do that project need to work (GSON and Apache Commons Codec)
 * fork this project, if you need it
 * clone this project (or your own fork)
 * `cd HowToSay`
 * first two clones are library projects - add them as library projects
 * [third clone](/o2genum/forvo-java-api) is a simple Java library - download its JAR and JARs it needs (see its README.md) into `libs` directory
 * `cp src/o2genum/howtosay/ExampleSecret.java src/o2genum/howtosay/Secret.java`, open, read and edit `Secret.java` correspondingly

Now you likely have everything I have in my project directory. Please, read Android Code Style Guidelines (yes, code must be beautiful) and feel free to join the development.

### Project needs translations
https://www.transifex.net/projects/p/HowToSay/

Special thanks to:
-----------------

 * [johannilsson/android-actionbar](/johannilsson/android-actionbar) project for awesome action bar
 * [commonsguy/cwac-endless](/commonsguy/cwac-endless) for endless list adapter
 * [Inkscape](http://inkscape.org/) project (everything was drawn using that incredible SVG editor)
 * Karsten Priegnitz for [android-change-log](http://code.google.com/p/android-change-log/)
 * [Clker.com](http://www.clker.com/clipart-black-and-white-globe.html) for B&W globe SVG that I shamelessly copy-pasted from that site
 * [Yay.se](http://www.yay.se/resources/android-native-icons) for home and search actionbar icons
 * Alexander Boblak (Alex3n) for good tips
 * Hakan, Pedro Coelho, Pavel Fric for translations

Also, this project uses my another project [o2genum/forvo-java-api](/o2genum/forvo-java-api). Open its page to discover its dependencies.

License
-------
The code is licensed under the Apache Software License 2.0. See [LICENSE](/o2genum/HowToSay/blob/master/LICENSE) file or [online version](http://www.apache.org/licenses/LICENSE-2.0.html). Copyright (c) 2011, Andrey Moiseev.
