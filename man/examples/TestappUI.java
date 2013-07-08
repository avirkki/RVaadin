package fi.vtt.testapp;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fi.vtt.RVaadin.RContainer;

@SuppressWarnings("serial")
@Theme("testapp")
public class TestappUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = TestappUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		
		/* Initialize Vaadin */
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		setContent(layout);

        /* Initialize R */
        final RContainer R = new RContainer();
        R.eval("w <- seq(0,2*pi, len=500)");
        R.eval("cat('Hello R World!\n')");

        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
                public void buttonClick(ClickEvent event) {

                        /* Compute two random frequencies and construct
                         * the R plot command */
                        R.eval("freq <- runif(n=2, min=1, max=10)");
                        String plotSrt = "plot(sin(w*freq[1]), sin(w*freq[2]), "
                                        + "type='l', bty='L', lty='dashed',"
                                        + "main='Random Lissajous Curve')";

                        /* Get the R plot object embedded into a window
                         * and add it to the user interface */
                        Window lissajous = R.getGraph(plotSrt, 400, 400);
                        getUI().addWindow(lissajous);
                }
        });

        layout.addComponent(button);
	}
}