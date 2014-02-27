package fi.vtt.RVaadin;

/**
 * <p>
 * A generic vector object which can represent any R vector of type
 * RVector.Type.
 * </p>
 * 
 * <p>
 * This object is needed for representing data.frames as ArrayLists in Java,
 * where the columns can be of different Java type, say, double[], int[] or
 * String[].
 * </p>
 * 
 * <p>
 * To indicate missing data in R (NA), use {@code null} for Strings,
 * {@code Double.NaN} for doubles, and {@code Integer.MIN_VALUE} for the int
 * type. Locical values are not supported, since indicating missing values in
 * boolean[] would require another boolean[] to express the value presence.
 * </p>
 * 
 * @author Arho Virkki
 * 
 */
public class RVector {

	public enum Type {
		INTEGER, NUMERIC, CHARACTER
	};

	private Type type;

	int[] iv = null;
	double[] dv = null;
	String[] sv = null;

	public RVector(int[] dv) {
		this.iv = dv;
		type = Type.INTEGER;
	}

	public RVector(double[] dv) {
		this.dv = dv;
		type = Type.NUMERIC;
	}

	public RVector(String[] sv) {
		this.sv = sv;
		type = Type.CHARACTER;
	}

	public Type type() {
		return type;
	}

	public int length() {
		if (type == Type.INTEGER) {
			return iv.length;
		}
		if (type == Type.NUMERIC) {
			return dv.length;
		}
		if (type == Type.CHARACTER) {
			return sv.length;
		
		} else {
			// An error
			return -1;
		}
	}

	/**
	 * Get value with no implicit conversion in case of incompatible type.
	 * 
	 * @return int[]
	 */
	public int[] getInts() {
		if (type == Type.INTEGER) {
			return iv;
		}
		return null;
	}

	/**
	 * Get value with no implicit conversion in case of incompatible type.
	 * 
	 * @return double[]
	 */
	public double[] getdoubles() {
		if (type == Type.NUMERIC) {
			return dv;
		}
		return null;
	}

	/**
	 * Get value with no implicit conversion in case of incompatible type.
	 * 
	 * @return String[]
	 */
	public String[] getStrings() {
		if (type == Type.CHARACTER) {
			return sv;
		}
		return null;
	}
}
