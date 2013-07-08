
Installation instructions for Ubuntu 12.04.2
============================================

These instructions show how to install the [Eclipse editor](http://www.eclipse.org/), [Apache Tomcat](http://tomcat.apache.org/) Web server, [Rserve](http://www.rforge.net/Rserve/) R server and the required Java libraries to a fresh installation of [Ubuntu 12.04.2 LTS Linux](http://www.ubuntu.com/download/desktop). Other editors (like Netbeans) can also be used, if desired. The installation procedure is analoguous in other operating systems. Optional steps are marked with *.

You need a basic understanding of [R](http://www.r-project.org/) and [Java](http://www.oracle.com/us/technologies/java/enterprise-edition/overview/index.html), and optionally the basics of Git to access the complete source from GitHub.

Vaadin Environment
------------------

Have a look at the [Book of Vaadin](https://vaadin.com/book), Chapter 2. It contants detailed installation instructions (for Windows XP), and the book is needed anyway to learn developing for Vaadin.


1. Install **Java 7** Development Kit. (Vaadin 7.x.x supports Java 6 or higher.) Open Terminal and write

    `sudo apt-get install openjdk-7-jdk`
    
2. Instead of using apt-get, download **Eclipse IDE** for **Java EE developers** (*not the basic edition!*) directly from <http://www.eclipse.org/downloads> and install it to obtain all the components needed by Vaadin. After downloading the latest file (where the name and location might differ from the example below), issue

        cd ~/Downloads/
        tar xvzf eclipse-jee-juno-SR2-polinux-gtk-x86_64.tar.gz`
        mkdir ~/bin`
        mv eclipse ~/bin/
        echo "alias eclipse='~/bin/eclipse/eclipse'" >> ~/.bashrc


    The last command writes an alias statement to the user's Bash resource file (.bashrc) which enables stating Eclipseby typing `eclipse` at the Terminal, once it has been restarted.

3. Download **Apache Tomcat** from <http://tomcat.apache.org/>. Tomcat must be run with user permissions to get the automated deployment from Eclipse to work. This is the reason for not installing it via apt-get. (For the server environment only intended for running RVaadin software, using apt-get is perfectly fine). After downloading the software, issue

        cd ~/Downloads/
        tar xvzf apache-tomcat-7.0.42.tar.gz
        mv apache-tomcat-7.0.42 ~/bin/
        ln -s ~/bin/apache-tomcat-7.0.42/ ~/bin/tomcat
        ~/bin/tomcat/bin/startup.sh
        gnome-open http://localhost:8080/

    The last two commands start the service and open the default Web browser to show Tomcat configuration window. To use Tomcat's Manager App, you need to edit `/bin tomcat/conf/tomcat-users.xml`, but that is only optional as software can also be deployed by simply copying the corresponding web archive (.war) file to `/bin/tomcat/webapps/` and letting the server do a hot deployment, as shown in the next, optional step.

  * Test Apache Tomcat by downloading [a test application](war/Tomcat_Tryout.war?raw=true) from RVaadin GitHub repository and placing it manually under the Tomcat’s webapps directory.

            cd ~/Downloads/
            mv Tomcat_Tryout.war ~/bin/tomcat/webapps/
            gnome-open http://localhost:8080/Tomcat_Tryout

            ~/bin/tomcat/bin/shutdown.sh
            ~/bin/tomcat/bin/startup.sh

** work in progress **

The installation was successful if you can see the following application in the browser.
4. Install Vaadin Plugin for Eclipse. Follow the instructions in https://vaadin.com/book/vaadin7/-/page/getting-
started.eclipse.html to install the required Ivy and Vaadin plugins.
1.2
R Environment
1. Install R.
(a) Open the editor with
sudo gedit /etc/apt/sources.list.d/rproject.list &
(b) Add the followin line to the file and save it.
deb http://ftp.sunet.se/pub/lang/CRAN/bin/linux/ubuntu precise/
(c) Issue
sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys E084DAB9
sudo apt-get update
sudo apt-get install r-base
2. Install Rserve.
(a) Run R as root
sudo R
(b) Install the software via the packaging mechanism
install.packages("Rserve")
(c) Quit R and start the server with
R CMD Rserve
GLU
CONFIDENTIAL
A Virkki (2/7)
Getting Started with RVaadin
2013-04-15
The server can be stopped with the command killall Rserve.
3. Download the Rserve Java libraries from the svn or from the developer’s pages and store them for later
use. For details, see http://rforge.net/Rserve/
mkdir -p ~/bin/lib/Rserve
cd ~/bin/lib/Rserve
svn export --username guest --password guest https://asylum.vtt.fi/TK8036/svn/\
xva/xVAResources/trunk/xVAResources/REngine/REngine.jar
svn export --username guest --password guest https://asylum.vtt.fi/TK8036/svn/\
xva/xVAResources/trunk/xVAResources/REngine/RserveEngine.jar
4. Install Cairo development libraries and the R package. Cairo is a multi-platform library providing anti-
aliased vector-based rendering for multiple target backends, and xVA uses it to construct the R graphics
to be shown on the browser window.
sudo apt-get install libcairo2-dev
sudo R
install.packages("Cairo")
1.3
Cross-language Visual Analytics library (xVA)
Download the library from the svn and save it for later use.
mkdir -p ~/bin/lib/xva
cd ~/bin/lib/xva
svn export --username guest --password guest \
https://asylum.vtt.fi/TK8036/svn/xva/xVACore/trunk/xVACore/jar/xva.jar
2
Startup
In this example we set up the development environment and test that it works correctly.
1. Start Eclipse and configure Tomcat to work with it. Choose the Servers tab from the Eclipse lower panel,
and choose New by eiher right-clicking the background or clicking directly the wizard link.
GLU
CONFIDENTIAL
A Virkki (3/7)
Getting Started with RVaadin
2013-04-15
2. Establish a test project that uses pure Vaadin.
(a) Start a new Vaadin project by choosing File
Next.
New
Project..., and then Vaadin
Vaadin Project
(b) Now write the project name (TestApp) and choose "Default Configuration for Apache Tomcat"
(c) Under the Vaadin section, choose Download...
Next.
pick the latest stable release. Now choose Next
(d) Do not forget to choose [x] Generate web.xml deployment descriptor.
(e) Choose "Next >", and edit the Base package name to fit the standard convention (reversed com-
pany URL) before choosing Finish. (Initiating the project for the very first time may take quite some
time.)
If Tomcat is still running, stop it with
~/bin/tomcat/bin/shutdown.sh
Otherwise Eclipse cannot start its own Tomcat instance.
3. Run the TestApp by highlighting it from the Project Explorer, and then pressing F11. Then choose "Run
on Server"
OK
Choose an existing server
Finish.
GLU
CONFIDENTIAL
A Virkki (4/7)
Getting Started with RVaadin
2013-04-15
4. Link the external libraries with this project. Symbolic linking suffices. Adjust the following commands,
if your Eclipse workspace location differs.
cd ~/workspace/TestApp/WebContent/WEB-INF/lib/
ln -s ~/bin/lib/Rserve/* .
ln -s ~/bin/lib/xva/xva.jar .
Then notify Eclipse about this change by selecting you TestApp lib folder (as shown in the above image
right) and refreshing the view with F5.
5. Open Terminal, (re)start Rserve, and leave the window open. You can inspect the standard output of
(all) the Rserve session(s) from it.
killall Rserve
R CMD Rserve
6. Test the following small application to see output on both the Web browser and R Terminal.
package fi.vtt.testapp;
import com.vaadin.Application;
import com.vaadin.ui.*;
import fi.vtt.xva.*;
public class TestappApplication extends Application {
private static final long serialVersionUID = 1L;
@Override
public void init() {
Window main = new Window("Testapp Application");
Label status = new Label("Hello Web World!");
main.addComponent(status);
setMainWindow(main);
RContainer R = new RContainer(main.getApplication());
R.eval("cat(’Hello R world!\n’)");
}
}
7. Enable the Javadoc hover help with xVA. Highlight the TestApp, choose File
Properties
Java Build
Path
Libraries
xva.ja
Javadoc location
Edit
Javadoc in archive
Browse the location of
xva.jar
Path within archive: doc
OK
OK

