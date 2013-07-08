<img align="right" src="man/img/vttplain.png" />
RVaadin
=======

A **Java library** to combine [Vaadin Web framework](http://vaadin.com) with the [R language](http://www.r-project.org).

When to use RVaadin
-----------------

 Whereas the [Vaadin](http://vaadin.com) framework  is excellent for writing full-blown Java EE Web applications, the [R language](http://www.r-project.org) provides superior flexibility for custom algorithm development - and a large selection of ready-made routines from the vast [CRAN](http://cran.r-project.org/) package repository. 

The RVaadin library is intended to be used when the production standards for serving Web pages are high, but the R language is used for both *quick prototyping* and the *final  implementation* of the sophisticated computational methods and graphics.

* RVaadin enables calling R functions from the Vaadin Web user sessions. 

* RVaadin implements 
    * Thread-safety, 
    * Integrated graphics
    * Ready-made upload and download elements 
    * Data type conversions between R data structures and Vaadin elements

* One R process is bound to a single Java object through the [Rserve TCP/IP server](http://www.rforge.net/Rserve/). Thus, a single user can have 
    * A dedicated R session
    * Several parallel R sessions
    * A single shared R session with other users

RVaadin is developed at
[VTT Technical Research Centre of Finland](http://http://www.vtt.fi/?lang=en),
 and published under [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html). 


Quick Installation
----------------

You can also read [detailed installation istructions for Ubuntu Linux 12.04.2](man/Installation_Ubuntu.md)

### Download the tools

1. Follow the [Book of Vaadin](https://vaadin.com/book/vaadin7/-/page/getting-started.html) to install Vaadin production environment.
2. Download the [RVaadin.jar](jar/RVaadin.jar?raw=true) Java library.
3. Install [R](http://cran.r-project.org/) and the [Rserve](http://www.rforge.net/Rserve/) package.
4. Download the Rserve Java libraries [RservEngine.jar](http://www.rforge.net/Rserve/files/RserveEngine.jar) and [REngine.jar](http://www.rforge.net/Rserve/files/REngine.jar).

### Test the setup

1. Start a new Vaadin 7 project in Eclipse. Call it e.g. "RVaadinTest"
2. Copy or link the three Java libraries under the `RVaadinTest/WebContent/WEB-INF/lib/` folder. (You may need to notify Eclipse for these new libraries by refreshing the Project Explorer with F5.)
4. Launch R serve in Terminal with `R CMD Rserve`. Leave the Terminal window open to see the output (which is handy for debugging).

Write a test program:

    package com.example.rvaadintest;

    import com.vaadin.server.VaadinRequest;
    import com.vaadin.ui.*;
    import fi.vtt.RVaadin.RContainer;

    public class RvaadintestUI extends UI {

    	@Override
    	protected void init(VaadinRequest request) {
    
    		/* Initialize Vaadin and say Hello */
	    	final VerticalLayout layout = new VerticalLayout();
    		layout.setMargin(true);
    		setContent(layout);
	    	Label hello = new Label("Hello Vaadin World!");
    		layout.addComponent(hello);
		
	    	/* Initialize R */
		    RContainer R = new RContainer();

    		/* Say Hello to the Terminal */
    		R.eval("cat('Hello R World!\n')");

    		/* Draw some graphics */
    		R.eval("d <- rnorm(100,0.1,0.5)");
    		Window graph = R.getGraph(
                    "plot(cumsum(d), type='l', bty='L')", 600,  400);
    		getUI().addWindow(graph);
    	}
    }

The program will produce some output to both Terminal and to the Web interface. 

![RVaadin Example Application](man/img/RVaadin_success.png?raw=true)
**Figure 1.** An example output from the the RVaadin test program. Even though the expected value of the normal distribution is positive, E(d) = 0.1, the variation with SD(d) = 0.5 turns the cumulative sum negative quite often. Those who bother can compute the actual probability of this occasion since the events are i.i.d. 

Usage
-----

So far, we have only seen the *eval(String)* method of the RContainer class, which takes an R expression as Java String  and evaluates it in the R session. In general, all communication with the R process go through the RContainer class which takes care that a single R session operates with a single task at a given time.

Other RContainer methods include 

* *getDoubles*, *getStrings*, ... return the correspoinding Java object given the R object name as String. These methods are merely wrappers for the corresponding Rserve RConnection methods.
* *getUploadElement* returns an instance of the RUpload class. The element can be used to upload arbitrary files to the R session's working directory.
* *getDownloadLink* returns a Vaadin Link object to download files saved to the R session working directory. 
* *getGraph* and *getEmbeddedGraph* can be used to get the images produced by R, where the argument is the ordinary R plot command as String.
* *getListSelect*, *getOptionGroup*, *getSlider*, ... return the corresponding Vaadin elements that implicitly and immediately change the given R variable into the selected value. 


Observe that each R session will be assigned a temporal default working directory by Rserve. This directory is intentionally different for each R session, and should not be changed in R with *setwd()* or even queried with *getwd()* for other than debugging purposes. When the R session and the Web software are running on different machines, information between Java and R is most convenienly passed only through the RContainer class, and not by pointing directly to different files in the filesystem. Having a commond directory for multiple sessions is also not a good practise, since it enables the users to overwrite each other's files. 

In addition to these *get...* methods, there are couple of set methods like *setGraphButtonsVisible( boolean )*, which change the behavior of the Graph window seen in the previous example, and *close()* and *closeAndDeleteFiles()* to explicitly clean up the R session (e.g. if there were other files that graphics generated).

Further information
-------------------

At present, the source code together with the JavaDoc are the definitive source of information. All proposals, ideas and concrete collaboration plans are warmly welcomed by the author(s) at FirstName.LastName@vtt.fi.


















