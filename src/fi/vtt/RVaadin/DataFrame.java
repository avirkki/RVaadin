package fi.vtt.RVaadin;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A simple Java equivalent to the R data.frame object based on
 * ArrayList&lt;RVector&gt;.
 * 
 * @author Arho Virkki
 * 
 */
public class DataFrame extends ArrayList<RVector> {

	private static final long serialVersionUID = 1L;
	private int nrow = 0;

	public DataFrame() {
		// TODO Auto-generated constructor stub
	}

	public DataFrame(int initialCapacity) {
		super(initialCapacity);
		// TODO Auto-generated constructor stub
	}

	public boolean add(RVector v) {

		if (size() == 0) {
			/* This is the first vector */
			nrow = v.length();
		}

		/* Only allow adding vectors of same length */
		if (nrow == v.length()) {
			return super.add(v);

		} else {
			return false;
		}
	}

	public DataFrame(Collection<? extends RVector> c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return Number of rows
	 */
	public int nrow() {
		return nrow;
	}

	/**
	 * @return Number of columns
	 */
	public int ncol() {
		return size();
	}
}
