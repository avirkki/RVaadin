package fi.vtt.RVaadin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * A factory class to generate XLSX files from DataFrames using the Apache POI
 * library.
 * 
 * @author Arho Virkki
 * 
 */
public class SpreadSheetFactory {

	long fileCount = 1;
	String DEFAULT_FONT = "Calibri";

	public SpreadSheetFactory() {
	}

	/**
	 * Set the default for name for the Worksheet, e.g. "Arial" or "Calibri".
	 * 
	 * @param fontName
	 */
	public void setDefaultFont(String fontName) {
		DEFAULT_FONT = fontName;
	}

	/**
	 * Get the default font for the Worksheet.
	 * 
	 * @return Font name
	 */
	public String getDefaultFont() {
		return DEFAULT_FONT;
	}

	/**
	 * Generates an Excel XLSX file from a given DataFrame using the Apache POI
	 * library.
	 * 
	 * @param df
	 *            input DataFrame
	 * @return File object.
	 */
	public File getXLSXFile(DataFrame df) {

		return getXLSXFile(df, null, null, null);
	}

	/**
	 * Generates an Excel XLSX file from a given DataFrame using the Apache POI
	 * library.
	 * 
	 * @param df
	 *            input DataFrame
	 * @param columnNames
	 *            Column names
	 * @return File object.
	 */
	public File getXLSXFile(DataFrame df, String[] columnNames) {

		return getXLSXFile(df, columnNames, null, null);
	}

	/**
	 * Generates an Excel XLSX file from a given DataFrame using the Apache POI
	 * library.
	 * 
	 * @param df
	 *            input DataFrame
	 * @param columnNames
	 *            Column names
	 * @param fileName
	 *            Name for the XLSX file.
	 * @return File object.
	 */
	public File getXLSXFile(DataFrame df, String[] columnNames, String fileName) {

		return getXLSXFile(df, columnNames, fileName, null);
	}

	/**
	 * Generates an Excel XLSX file from a given DataFrame using the Apache POI
	 * library.
	 * 
	 * @param df
	 *            input DataFrame
	 * @param columnNames
	 *            Column names
	 * @param fileName
	 *            Name for the XLSX file.
	 * @param sheetName
	 *            Name of the first sheet containing the data.
	 * @return File object.
	 */
	public File getXLSXFile(DataFrame df, String[] columnNames,
			String fileName, String sheetName) {

		Workbook wb = new XSSFWorkbook();

		/*
		 * Use two style: Bold for the column names and normal for the rest of
		 * the worksheet,
		 */
		Font boldFont = wb.createFont();
		boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		boldFont.setFontName(DEFAULT_FONT);

		CellStyle boldStyle = wb.createCellStyle();
		boldStyle.setFont(boldFont);

		Font normalFont = wb.createFont();
		normalFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
		normalFont.setFontName(DEFAULT_FONT);

		CellStyle normalStyle = wb.createCellStyle();
		normalStyle.setFont(normalFont);

		String safeName = WorkbookUtil.createSafeSheetName(sheetName);
		Sheet sheet = wb.createSheet(safeName);

		int nrow = df.nrow();
		int ncol = df.ncol();
		int sheetRow = 0;

		/*
		 * Add column names to the first row. If the names are missing, use R
		 * style X1, X2, ...
		 */
		if (columnNames == null) {
			columnNames = new String[ncol];
			for (int j = 0; j < ncol; j++) {
				columnNames[j] = "X" + (j + 1);
			}
		}
		
		/*
		 * Declare default file and sheet names 
		 */
		if( fileName == null ) {
			String dateAndCount = RContainer.getDateAndCount(fileCount);
			fileCount++;
			fileName = "Workbook_" + dateAndCount + ".xlsx";
		}

		if( sheetName == null ) {
			sheetName = "R Data Frame";
		}
		

		Row titleRow = sheet.createRow(sheetRow);
		titleRow.setRowStyle(boldStyle);

		for (int j = 0; j < columnNames.length; j++) {
			titleRow.createCell(j).setCellValue(columnNames[j]);
		}
		sheetRow++;

		/*
		 * Create the sheet contents by iterating over the DataFrame row-wise.
		 */
		for (int i = 0; i < nrow; i++) {
			Row row = sheet.createRow(sheetRow);
			row.setRowStyle(normalStyle);

			for (int j = 0; j < ncol; j++) {

				RVector column_j = df.get(j);

				/* We need a switch for every RVector type */
				RVector.Type type = column_j.type();

				switch (type) {
				case CHARACTER:
					String strval = column_j.getStrings()[i];
					row.createCell(j).setCellValue(strval);
					break;

				case INTEGER:
					int intval = column_j.getInts()[i];

					/*
					 * Integer.MIN_VALUE means missing value, in which case the
					 * cell should be left empty
					 */
					if (intval != Integer.MIN_VALUE) {
						row.createCell(j).setCellValue(intval);
					} else {
						row.createCell(j);
					}
					break;

				case NUMERIC:
					double dval = column_j.getdoubles()[i];

					/* Double.NaN == missing */
					if (!Double.isNaN(dval)) {
						row.createCell(j).setCellValue(dval);
					} else {
						row.createCell(j);
					}
					break;

				default:
					row.createCell(j).setCellValue(
							"Error: Unknown RVector.Type");
					break;
				}
			}
			sheetRow++;
		}

		/*
		 * Write the Workbook into a temporal Excel file. Use folders with
		 * random UUID names to distinguish files in different sessions
		 */
		String dirName = UUID.randomUUID().toString();
		Path filePath = Paths
				.get(System.getProperty("java.io.tmpdir"), dirName);
		filePath.toFile().mkdir();
		File file = new File(filePath.toFile(), fileName);

		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			wb.write(bos);
			bos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return file;
	}

	/**
	 * Deletes the temporal XLSX file created by
	 * {@link SpreadSheetFactory#getXLSXFile(DataFrame, String[], String, String)}
	 * .
	 * 
	 * @param file
	 */
	void deleteXLSXFile(File file) {
		try {
			/*
			 * Since we used folders with random UUID names to distinguish files
			 * in different sessions and/or SpreadSheetFactories, we also need
			 * to delete the parent folder of the file.
			 */
			String absolutePath = file.getAbsolutePath();
			String directoryPath = absolutePath.substring(0,
					absolutePath.lastIndexOf(File.separator));

			Files.deleteIfExists(Paths.get(absolutePath));
			Files.deleteIfExists(Paths.get(directoryPath));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
