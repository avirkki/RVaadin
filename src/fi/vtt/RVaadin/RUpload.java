/*
* Copyright 2013 VTT Technical Research Centre of Finland
*
* Licensed under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License. You may obtain a copy of
* the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations under
* the License.
*/

package fi.vtt.RVaadin;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RFileOutputStream;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;

/**
 * RUpload constructs an upload window as Vaadin CustomComponent for the R
 * session given as an argument. Uploading files through the browser window is
 * intentionally difficult, which does not give much room for creativity about
 * designing the element. Ultimately, this is a security issue to prevent people
 * from unintentionally uploading their confidential information into Internet.
 * See e.g. Book of Vaadin 6, page. 132.
 * 
 * @author Arho Virkki
 * 
 */
public class RUpload extends CustomComponent implements
		Upload.SucceededListener, Upload.FailedListener, Upload.Receiver {

	private static final long serialVersionUID = 1L;
	private Panel root;
	private ListSelect uploadedFiles;
	private Button remove;

	private RContainer R = null;
	private RConnection rc = null;
	private List<String> fileNames = new ArrayList<String>();
	private List<String> mimeTypes = new ArrayList<String>();

	/* The references for uploading the files (one-by-one) */
	private RFileOutputStream rfos = null;
	private String filename;
	private String mimeType;

	/**
	 * Contruct an upload element for R (implemented as Vaadin CustomComponent).
	 * 
	 * @param caption
	 *            String caption or null
	 * @param R
	 *            the corresponding RSession to upload the files to
	 */
	public RUpload(String caption, RContainer R) {

		/* Create the RUpload custom component */
		super.setSizeUndefined();
		root = new Panel(caption);
		root.setWidth("90ex");

		setCompositionRoot(root);

		HorizontalLayout hbox = new HorizontalLayout();
		hbox.setWidth("100%");

		/* Create the Upload component */
		final Upload upload = new Upload("Choose file", this);
		upload.setButtonCaption("Submit");

		/* Listen for events regarding the success of upload. */
		upload.addSucceededListener(this);
		upload.addFailedListener(this);
		hbox.addComponent(upload);

		Label hfill = new Label();
		hbox.addComponent(hfill);
		hbox.setExpandRatio(hfill, 1.0f);

		remove = new Button("Remove", new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				String current = getSelection();

				if (current != null) {
					/* Delete the file */
					delete(current);

					/* Update the lists and the notification area */
					int i = fileNames.indexOf(current);
					fileNames.remove(i);
					mimeTypes.remove(i);
					uploadedFiles.removeItem(current);

					/* Gray out the button, if this was the last item */
					if (fileNames.isEmpty()) {
						remove.setEnabled(false);
					}
				}
			}
		});

		hbox.addComponent(remove);
		remove.setEnabled(false);
		hbox.setComponentAlignment(remove, Alignment.BOTTOM_RIGHT);

		/* Notification area for already uploaded files */
		uploadedFiles = new ListSelect("Already submitted files");
		uploadedFiles.setMultiSelect(false);
		uploadedFiles.setNullSelectionAllowed(false);
		uploadedFiles.setHeight("4em");
		uploadedFiles.setWidth("100%");

		// Changed for Vaadin 7, not tested!!
		VerticalLayout vbox = new VerticalLayout();
		vbox.addComponent(hbox);
		vbox.addComponent(uploadedFiles);
		root.setContent(vbox);

		/* Bind the component to the given R session */
		this.R = R;
	}

	/**
	 * Get the name of the selected file from the list of already uploaded
	 * files.
	 * 
	 * @return Name of the file as String.
	 */
	public String getSelection() {
		return (String) uploadedFiles.getValue();
	}

	/**
	 * Get a list of already uploaded files.
	 * 
	 * @return The names of the already uploaded files as List of Strings.
	 */
	public List<String> getFileNames() {
		return fileNames;
	}

	/**
	 * Physically delete a file from the R working directory.
	 * 
	 * @param filename
	 */
	private void delete(String filename) {
		R.eval("unlink('" + filename + "')");
	}

	@Override
	public OutputStream receiveUpload(String filename, String mimeType) {

		try {
			/* This will also lock the R Session until we explicitly release it */
			rc = R.getRConnection();

			this.filename = filename;
			this.mimeType = mimeType;

			rfos = rc.createFile(filename);
			return rfos;

		} catch (Exception e) {
			/* Something went wrong. Still try to unlock the R session */
			System.err.println("receiveUpload failed: "
					+ "releasing the R Session.");
			R.releaseRConnection();
			rc = null;
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void uploadFailed(FailedEvent event) {
		System.err.println("Upload failed: releasing the R Session.");
		R.releaseRConnection();
		rc = null;
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {

		try {
			/* Close the stream and save the file information */
			rfos.close();
			uploadedFiles.addItem(filename);
			fileNames.add(filename);
			mimeTypes.add(mimeType);
			remove.setEnabled(true);

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			R.releaseRConnection();
			rc = null;
		}
	}
}
