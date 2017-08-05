<img align="right" src="man/img/vttplain.png" />

# RVaadin

A **Java library** to combine [Vaadin Web framework](http://vaadin.com) with the [R language](http://www.r-project.org).

**NOTE** (2017-08-05): While I feel that Vaadin is a superior Web framework, and R remains my tool
of choice for mathematical statistics, the *development of this package has
been stalled for quite a while.*

The reason for this is purely lack of free time. Feel free to fork this repository and
use RVaadin if it fits your needs. I thank my former employer [VTT](http://www.vtt.fi)
for funding this work. RVaadin is quite complete when it comes to showing static
R-generated graphics (either png or svg) with Vaadin. For interactive graphics, I
propose combining a suitable JavaScript library (g.e.
[D3.js](http://https://d3js.org) with a light-weight back-end such as
[Flask](http://flask.pocoo.org/) or [OpenCPU](http://flask.pocoo.org/), if an
 industrial-scale framework like Vaadin seems overkill).

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
[VTT Technical Research Centre of Finland](http://http://www.vtt.fi/?lang=en). It is published under [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).


Quick Installation
----------------

You can also follow the [detailed instructions for **Ubuntu LTS** 12.04.2 Linux](man/Installation_Ubuntu.md).

### Download the tools

1. Follow the [Book of Vaadin](https://vaadin.com/book/vaadin7/-/page/getting-started.html) to install Vaadin production environment.
2. Download the [RVaadin.jar](jar/RVaadin.jar?raw=true) Java library.
3. Install [R](http://cran.r-project.org/), and the [Rserve](http://www.rforge.net/Rserve/) and [Cairo](http://www.rforge.net/Cairo/) packages for it.
4. Download the Rserve Java libraries [RservEngine.jar](http://www.rforge.net/Rserve/files/RserveEngine.jar) and [REngine.jar](http://www.rforge.net/Rserve/files/REngine.jar).

### Test the setup

1. Start a new Vaadin 7 project in Eclipse. Call it e.g. "RVaadinTest"
2. Copy or link the three Java libraries under the *RVaadinTest/WebContent/WEB-INF/lib/* folder. (When using  Eclipse, you may need to refresh the Project Explorer with F5.)
4. Launch R serve in Terminal with `R CMD Rserve`. Leave the Terminal window open to see the output from the R processes. This is handy for debugging.

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
**Figure 1.** An example output from the the RVaadin test program. The expected value of the normally, identically and independently distributed sequence is 0.1 and SD(d) = 0.5. Because of the large standard deviation, the cumulative sum turns negative quite often. 

General Usage
-------------

So far, we have only seen the *eval(String)* method of the RContainer class, which takes an R expression as a Java String  and evaluates it in the R session. In general, all communication with the R process should go through the RContainer class which takes care that a single R session operates with a single task at a given time.

Other RContainer methods include 

* *getDoubles*, *getStrings*, ... take an R object name as String, and return the correspoinding Java object. These methods are merely wrappers for the corresponding Rserve RConnection methods.
* *getUploadElement* returns an instance of the RUpload class. The element can be used to upload arbitrary files to the R session's working directory.
* *getDownloadLink* returns a Vaadin Link pointing to a file saved to the working directory (of the corresponding R process). 
* *getGraph* and *getEmbeddedGraph* can be used to show the images produced by R, where the argument is an ordinary R plot command as String.
* *getListSelect*, *getOptionGroup*, *getSlider*, ... return the corresponding Vaadin elements that implicitly and immediately change the given R variable into the selected value. If other actions are needed, user can attach additional listeners to these objects. 

In addition to these *get...* methods, there are a few set methods like *setGraphButtonsVisible( boolean )*, which change the behavior of the Graph window seen in the previous example, and *close()* and *closeAndDeleteFiles()* to explicitly clean up the R session (e.g. if there were other files that graphics generated).

Observe that each R session will be assigned a temporal default working directory by Rserve. This directory is intentionally different for each R session, and should not be changed in R with *setwd()* or even queried with *getwd()* for other than debugging purposes. Pointing directly to files produced by R obviously does not make sense when the R processes are scattered between separate machines. Having a common directory for multiple sessions is also not good practise, since it enables the users to overwrite each other's files. In particular, running Rserve with user rights and using *setwd()* to change R working directory to e.g. Desktop (to see the files), *closeAndDeleteFiles()* will wipe the whole directory. To inspect R working directory in Ubuntu Linux, see under */tmp/Rserv/*, which is the default location for Rserve.

Some Examples
-------------

For brevity, we only show the *init()* routine of the complete program, or just snippets of code.

### Using R to evaluate a Java String

This program prints the current R version to the Web browser. 

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        /* Initialize the R session */
        RContainer R = new RContainer();

        /* Construct a label and add it to the UI layout */
        Label label = new Label();
        layout.addComponent(label);

        /* Generate some content to the label */
        String message = R.getString("paste('R Version: ', version$version.string)");
        label.setValue(message);

    }

This yields the following output

![R Version in Browser](man/img/R_Version_in_Browser.png?raw=true)


### Selecting a value from a list

Suppose that we need to provide several categorical options to choose from, and these options have been computed earlier in R. 

        /* Construct an R vector of different values */
        R.eval("input <- c('foo', 'bar', 'bar', 'baz', 'foobar', 'xyzzy')");

        /* Generate a ListSelect component with these options and save the
         * selection immediately into the R variable 'output' */
        final ListSelect ls = R.getListSelect("input", "output");
        
        /* Add the element into the UI */
        layout.addComponent(ls);

        /* Ask R about the new value */
        ls.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                Notification.show("The user chose: " + R.getString("output"));
            }
        });

The corresponding UI will look like

![User chose xyzzy](man/img/User_chose_xyzzy.png?raw=true)

The element style can be altered by changing *only one line*:

        final ComboBox ls = R.getComboBox("input", "output");

![ComboBox.png](man/img/ComboBox.png?raw=true)

        final OptionGroup ls = R.getOptionGroup("input", "output");

![OptionGroup.png](man/img/OptionGroup.png?raw=true)

### Uploading and downloading files through browser

Arbitrary data files can be uploaded with the RUpload element, and the processed results saved by making a link to the corresponding file. In the following example, the files are not processed in any way, but just saved to R working directory, and then downloaded back through a dynamically generated link.

        /* Initialize Vaadin */
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);

        /* Initialize R */
        final RContainer R = new RContainer();

        /* Construct an upload element with no caption */
        final RUpload upload = R.getUploadElement(null);
       
        layout.addComponent(upload);

        /* Make a button that generates a link for the selected file */
        Button getLink = new Button("Get Link", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                /* Ask the RUpload element for the selected file name */
                String fileName = upload.getSelection();

                if (fileName != null) {
                    Link link = R.getDownloadLink("Download: " + fileName,
                            fileName);
                    layout.addComponent(link);

                } else {
                    /* Nothing was selected */
                    Notification.show("Please choose a file.",
                            Notification.Type.HUMANIZED_MESSAGE);
                }
            }
        });
        layout.addComponent(getLink);

![Upload and Download](man/img/Upload+Download.png?raw=true)

*A proper application* should also provide an option to *log out* to make sure that no files are left to the server. Since [detecting a browser close is](https://vaadin.com/forum#!/thread/1553240)  inherently difficult, it is best to delete the temporal files from the R session as soon as they are not needed, and also provide the logout option:

        /* Add a button to log out and clean the session and delete the possible
         * files still remaining in the R working directory */
        Button logout = new Button("Logout");
        
        logout.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                /* Clean and close the RVaadin session */
                R.closeAndDeleteFiles();

                /* Since the session will be closed, we should relocate the user
                 * to an other page, like the institution main page or to a 
                 * static "Application Closed" page */
                getUI().getPage().setLocation("http://www.vtt.fi");
                
                /* Bye! */
                getSession().close();
            }
        });
        layout.addComponent(logout);

![Logout](man/img/Logout.png?raw=true)


### Finding errors in R code

Suppose that we wrote *ersion* instead of *version* in the previous *getString* example:

    String message = R.getString("paste('R Version: ', ersion$version.string)");



The browser now shows several Java error messages

![RVaadin R execution error](man/img/RVaadin_R_execution_error.png?raw=true)

Whereas the actual R error is shown in the open Terminal (which was used to launch Rserve):

    Error in paste("R Version: ", ersion$version.string) : 
      object 'ersion' not found
    RVaadin: eval failed, request status: error code: 127 


Errors are intentionally designed to be as visible and verbose as possible, since the other option, errors going unnoticed to a production software or a scientific publication, is much worse.

Further information
-------------------

At present, JavaDoc together with the source code are the definitive source of information. All proposals, ideas and concrete collaboration plans are warmly welcomed by the author(s) at *FirstName.LastName@vtt.fi*.


















