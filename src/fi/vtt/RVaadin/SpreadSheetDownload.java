package fi.vtt.RVaadin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.annotation.XmlAccessOrder;

import com.vaadin.server.FileResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

public class SpreadSheetDownload extends CustomComponent {

	private static final long serialVersionUID = 1L;

	VerticalLayout root = new VerticalLayout();
	Button xlsxButton;
	FileResource resource;
	File file;
	Link fileLink;

	DataFrame df;
	String[] columnNames;
	String fileName;
	String sheetName;
	SpreadSheetFactory ssf;

	@SuppressWarnings("serial")
	public SpreadSheetDownload(DataFrame df, String[] columnNames,
			String fileName, String sheetName, SpreadSheetFactory ssf) {

		this.df = df;
		this.columnNames = columnNames;
		this.fileName = fileName;
		this.sheetName = sheetName;
		this.ssf = ssf;

		/* Intiate this UI element */
		setCompositionRoot(root);

		xlsxButton = new Button("XLSX", new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				/*
				 * Generate the xlsx file only when the button is clicked
				 */
				createFile();
				resource = new FileResource(file);
				fileLink = new Link("xlsx", resource);

				root.removeComponent(xlsxButton);
				root.addComponent(fileLink);
				root.setComponentAlignment(fileLink, Alignment.MIDDLE_LEFT);
			}
		});
		xlsxButton.setDisableOnClick(true);
		xlsxButton.setStyleName(Reindeer.BUTTON_SMALL);
		root.addComponent(xlsxButton);
	}

	public void clear() {
		if (file != null) {
			root.removeComponent(fileLink);
			deleteFile();
			root.addComponent(xlsxButton);
			xlsxButton.setEnabled(true);
		}
	}

	private void createFile() {
		file = ssf.getXLSXFile(df, columnNames, fileName, sheetName);
	}

	private void deleteFile() {
		ssf.deleteXLSXFile(file);
	}
}
