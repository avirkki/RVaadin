package fi.vtt.RVaadin;

import com.vaadin.ui.Table;

import fi.vtt.RVaadin.RVector.Type;

public class RTable extends Table {

	private static final long serialVersionUID = 1L;
	private DataFrame dataFrame = null;
	private String[] columnNames = null;

	/**
	 * Build Vaadin Table from Java-side presentation of R data.frame,
	 * ArrayList<RVector> and (optinal) columnNames vector.
	 * 
	 * @param dataFrame
	 *            Statistical data frame to be displayed
	 * @param columnNames
	 *            Optional column names (otherwise X1,X2,...)
	 */
	public RTable(DataFrame dataFrame, String[] columnNames) {

		this.dataFrame = dataFrame;
		this.columnNames = columnNames;

		if (dataFrame.ncol() > 0) {
			buildTable();
			setPageLength(dataFrame.nrow());

		} else {
			/*
			 * Empty List, nothing to do! => The constructor will return just an
			 * ordinary, empty Vaadin Table.
			 */
		}
	}
	/**
	 * @return The number of columns
	 */
	public int ncol() {
		return dataFrame.ncol();
	}

	/**
	 * @return The number of rows
	 */
	public int nrow() {
		return dataFrame.nrow();
	}

	/**
	 * @return The underlying Data frame as DataFrame
	 */
	public DataFrame getDataFrame() {
		return dataFrame;
	}
	

	@SuppressWarnings("unchecked")
	private void buildTable() {
		
		int ncol = dataFrame.ncol();
		int nrow = dataFrame.nrow();

		/*
		 * If the names are missing, use R style X1, X2, ...
		 */
		if (columnNames == null) {
			columnNames = new String[ncol];
			for (int j = 0; j < ncol; j++) {
				columnNames[j] = "X" + (j + 1);
			}
		}

		RVector.Type[] colTypes = new RVector.Type[ncol];

		/*
		 * Declare the columns
		 */
		for (int j = 0; j < ncol; j++) {
			colTypes[j] = dataFrame.get(j).type();

			if (colTypes[j] == Type.CHARACTER) {
				addContainerProperty(columnNames[j], String.class, null);

			} else if (colTypes[j] == Type.INTEGER) {
				addContainerProperty(columnNames[j], Integer.class, null);

			} else if (colTypes[j] == Type.NUMERIC) {
				addContainerProperty(columnNames[j], Double.class, null);
			}
		}

		/*
		 * Add the Items for every i:th row and j:column
		 */
		for (int i = 0; i < nrow; i++) {

			/* Add new row */
			addItem(i);

			/* Fill the columns */
			for (int j = 0; j < ncol; j++) {

				if (colTypes[j] == RVector.Type.CHARACTER) {

					String[] sv = dataFrame.get(j).getStrings();
					getContainerProperty(i, columnNames[j]).setValue(sv[i]);
				}

				else if (colTypes[j] == RVector.Type.INTEGER) {
					int[] iv = dataFrame.get(j).getInts();
					/*
					 * Integer.MIN_VALUE indicates a missing value. In his case,
					 * the Property (=Table Cell) should be left empty (=null)
					 */
					if (iv[i] != Integer.MIN_VALUE) {
						getContainerProperty(i, columnNames[j]).setValue(iv[i]);
					}
				}

				else if (colTypes[j] == RVector.Type.NUMERIC) {

					double[] dv = dataFrame.get(j).getdoubles();
					/*
					 * Double.NaN indicates a missing value. In his case, the
					 * Property (=Table Cell) should be left empty (=null)
					 */
					System.out.println(dv[i]);
					if (!Double.isNaN(dv[i])) {
						getContainerProperty(i, columnNames[j]).setValue(dv[i]);
					}
				}
			}
		}
	}
}
