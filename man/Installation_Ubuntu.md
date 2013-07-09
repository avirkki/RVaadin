
Installation instructions for Ubuntu 12.04.2
============================================

These instructions show how to install the [Eclipse editor](http://www.eclipse.org/), [Apache Tomcat](http://tomcat.apache.org/) Web server, [Rserve](http://www.rforge.net/Rserve/) R server and the required Java libraries to a fresh installation of [Ubuntu 12.04.2 LTS Linux](http://www.ubuntu.com/download/desktop). Other editors (like Netbeans) can also be used, if desired. The installation procedure is analoguous in other operating systems. Optional steps are marked with **[Optional]**.

You need a basic understanding of [R](http://www.r-project.org/) and [Java](http://www.oracle.com/us/technologies/java/enterprise-edition/overview/index.html), and optionally the basics of Git to access the complete source from GitHub.

Vaadin Environment
------------------

Have a look at the [Book of Vaadin](https://vaadin.com/book), Chapter 2. It contants detailed installation instructions (for Windows XP), and the book is needed anyway to learn developing for Vaadin.


1. Install **Java 7** Development Kit. (Vaadin 7.x.x supports Java 6 or higher.) Open Terminal and write

    `sudo apt-get install openjdk-7-jdk`
    
2. Instead of using apt-get, download **Eclipse IDE** for **Java EE developers** (*not the basic edition!*) directly from <http://www.eclipse.org/downloads> and install it to obtain all the components needed by Vaadin. After downloading the latest file (where the name and location might differ from the example below), issue

        cd ~/Downloads/
        tar xvzf eclipse-jee-juno-SR2-polinux-gtk-x86_64.tar.gz
        mkdir ~/bin
        mv eclipse ~/bin/
        echo "alias eclipse='~/bin/eclipse/eclipse'" >> ~/.bashrc


    The last command writes an alias statement to the user's Bash resource file (.bashrc) which enables stating Eclipseby typing `eclipse` at the Terminal, once it has been restarted. (Eclipse might does not use the Ubuntu global menu by default. If that bothers, follow [these instructions](http://www.webupd8.org/2013/01/eclipse-ide-get-ubuntu-appmenu-and-hud.html) to enable the global menu.)

3. Download **Apache Tomcat** from <http://tomcat.apache.org/>. Tomcat must be run with user permissions to get the automated deployment from Eclipse to work. This is the reason for not installing it via apt-get. (For the server environment only intended for running RVaadin software, using apt-get is perfectly fine). After downloading the software, issue

        cd ~/Downloads/
        tar xvzf apache-tomcat-7.0.42.tar.gz
        mv apache-tomcat-7.0.42 ~/bin/
        ln -s ~/bin/apache-tomcat-7.0.42/ ~/bin/tomcat
        ~/bin/tomcat/bin/startup.sh
        gnome-open http://localhost:8080/

    The last two commands start the service and open the default Web browser to show Tomcat configuration window. To use Tomcat's Manager App, you need to edit `/bin tomcat/conf/tomcat-users.xml`, but that is only optional as software can also be deployed by simply copying the corresponding web archive (.war) file to `/bin/tomcat/webapps/` and letting the server do a hot deployment, as shown in the next, optional step.

5. **[Optional]** Test Apache Tomcat by downloading [a test application](war/Tomcat_Tryout.war?raw=true) from RVaadin GitHub repository and placing it manually under the Tomcat's webapps directory.

        cd ~/Downloads/
        mv Tomcat_Tryout.war ~/bin/tomcat/webapps/

    Now, wait a moment until Tomcat notices that there is a new application to be deployed, and then issue

        gnome-open http://localhost:8080/Tomcat_Tryout

    The Tomcat installation was successful if you can see the following application in the browser.
![Tomcat tryout was successful](img/Tomcat_Tryout_success.png?raw=true)


4. Install **Vaadin Plugin** for Eclipse. Follow the instructions in 
<https://vaadin.com/book/vaadin7/-/page/getting-started.eclipse.html> to install the required Ivy and Vaadin plugins.


R Environment
-------------

1. **Install the R language**. To use the latest R from a mirror from Sweden (which is a good option in Finland), open the editor with

        sudo gedit /etc/apt/sources.list.d/rproject.list

    Add the following line to the file and save it.

        deb http://ftp.sunet.se/pub/lang/CRAN/bin/linux/ubuntu precise/

    Issue

        sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys E084DAB9
        sudo apt-get update
        sudo apt-get install r-base

2. **Install Rserve**. Run R as root

        sudo R

    Then install the software via the ordinary packaging mechanism

        install.packages("Rserve")

    Finally, quit R and start the server with

        R CMD Rserve
    
    The server can be stopped, for example, with the command `killall Rserve`.

3. **Download Rserve Java libraries** [RservEngine.jar](http://www.rforge.net/Rserve/files/RserveEngine.jar) and [REngine.jar](http://www.rforge.net/Rserve/files/REngine.jar) and store them, for example, under your Eclipse workspace into a shared library folder. For additional information about, have a look at the [Rserve project page](http://www.rforge.net/Rserve/).

        mkdir -p ~/workspace/lib
        mv ~/Downloads/REngine.jar workspace/lib/
        mv ~/Downloads/RserveEngine.jar workspace/lib/

4. **Install Cairo** development libraries and the R package. Cairo is a multi-platform library providing anti-aliased vector-based rendering for multiple target backends, and RVaadin uses it to construct the R graphics to be shown on the browser window.


        sudo apt-get install libcairo2-dev
        
        sudo R
        install.packages("Cairo")

5. Download [RVaadin java library](../jar/RVaadin.jar?raw=true) from GitHub and save it for later use.

        mv ~/Downloads/RVaadin.jar workspace/lib/


Develop First Pure Vaadin Program
=================================

In this example we set up the development environment and test that it works correctly.

1. Start Eclipse and **configure Tomcat** to work with it. Choose the Servers tab from the Eclipse lower panel, and choose New by eiher right-clicking the background or clicking directly the wizard link.

    ![New Tomcat Server](img/New_Tomcat_Server.png?raw=true)
    ![Choose Tomcat Location](img/Choose_Tomcat.png?raw=true)

2. **Establish a test project** that uses pure Vaadin. Start a new Vaadin project by choosing File > New > Project, and then Vaadin > Vaadin 7 Project > Next.

    ![New Vaadin Project](img/New_Vaadin_Project.png?raw=true)


3. Now write the project name "TestApp" and choose "Default Configuration for Apache Tomcat"


4. Under the Vaadin section, choose the latest version of Vaadin (7.x.x). Now choose Next, and then again Next.

    ![Generate web.xml](img/Generate_web_xml.png?raw=true)

5. *Do not forget* to choose [x] Generate web.xml deployment descriptor.


6. Then choose Next, and edit the Base package name to fit the standard convention (i.e. the reversed company URL, such as fi.vtt.*package* for a package developed at VTT Technical Research Centre of Finland, having <http://www.vtt.fi> as its Internet address) before choosing Finish. Initiating the project for the very first time may take quite some time.


    If Tomcat is still running, stop it with

        ~/bin/tomcat/bin/shutdown.sh

    Otherwise Eclipse cannot start its own Tomcat instance.

7. **Run the TestApp** by highlighting it from the Project Explorer, and then pressing F11. Then choose "Run on Server" > OK > Choose an existing server > Finish.

    ![TestApp running](img/TestApp_running.png?raw=true)  
    **Figure.** Vaadin 7.1.0 Application template, produced by the Vaadin 7 application wizard, produces just a button to click.

Develop First RVaadin Program
=============================

*Finally, we have got beyond what can also be learned from the [Book of Vaadin](https://vaadin.com/book/vaadin7/-/page/getting-started.html) and [CRAN](http://cran.r-project.org/).*


1. **Link the R libraries** with the TestApp project. Symbolic linking suffices. Of course, there are multiple ways of telling Vaadin to include certain libraries into the project, but linking the jar-files under `<Project name>/WebContent/WEB-INF/lib/` does the trick:
 
        cd ~/workspace/TestApp/WebContent/WEB-INF/lib/
        ln -s ~/workspace/lib/REngine.jar .
        ln -s ~/workspace/lib/RserveEngine.jar .
        ln -s ~/workspace/lib/RVaadin.jar .

    Now notify Eclipse about this change by selecting you TestApp lib folder (as shown in the image below) and refreshing the view with F5.

    ![Update lib Folder](img/Update_lib_folder.png?raw=true)  
	**Figure.** You need to link the Rserve libraries (REngine.jar and RserveEngine.jar) and the RVaadin library (RVaadin.jar) with the project.



2. *Open Terminal to re-start Rserve*, and leave the window open. You can inspect the standard output of (all) the Rserve session(s) from it, which is very handy in case of error in the R code.

        killall Rserve
        R CMD Rserve

3. Click [here](examples/TestappUI.java?raw=true) to **download a [modified TestApp](examples/TestappUI.java) application** to see some output on both the Web browser and R Terminal. Inspect once more the previous TestApp Vaadin application, and then replace the contents of of it with t he modified `TestappUI.java`.

    The key elements is importing the RVaadin library

        import fi.vtt.RVaadin.RContainer;

    and then initializing a single R session for this application under the *init*-method.

        /* Initialize R */
        final RContainer R = new RContainer();

    The RContainer's *eval* method prints a familiar greeting to the Terminal,

        R.eval("cat('Hello R World!\n')");

    and the Button.ClickListener does not any more add text to the UI, but does some computations in the R language:

        /* Compute two random frequencies and construct
         * the R plot command */
        R.eval("freq <- runif(n=2, min=1, max=10)");
        String plotSrt = "plot(sin(w*freq[1]), sin(w*freq[2]), "
                        + "type='l', bty='L', lty='dashed',"
                        + "main='Random Lissajous Curve')";

    The Window object is generated with RContainer's *getGraph* method, and added to the Vaadin UI

        /* Get the R plot object embedded into a window
         * and add it to the user interface */
        Window lissajous = R.getGraph(plotSrt, 400, 400);
        getUI().addWindow(lissajous);

    ![Random Lissajous Curves.png](img/Random_Lissajous_Curves.png?raw=true)
    **Figure.** Random Lissajous Curves, particularly famous in electrical engineerin. For more information, see <http://en.wikipedia.org/wiki/Lissajous_curve>.
