package org.hbaseom.client.translators.util;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.hbaseom.client.annotations.HCompoundSize;
import org.hbaseom.client.meta.MetaEntity;
import org.hbaseom.client.meta.reflect.KeyField;

/**
 * Defines null entity fields (for HTable key) by key size
 * 
 * @author levan
 * 
 */
public class KeySize {

	private ConcurrentMap<Integer, boolean[]> keySizes = new ConcurrentHashMap<Integer, boolean[]>();

	private int fullSize;

	private MetaEntity metaEntity;

	private KeyField[] keyFields;

	public KeySize(MetaEntity metaEntity) {
		this.metaEntity = metaEntity;
		this.keyFields = metaEntity.getKeyFields();
	}

	public void setKeySize() {
		boolean[] defineds = new boolean[keyFields.length];
		boolean defined = false;
		List<KeyField> nullables = new ArrayList<KeyField>();
		KeyField keyField;
		for (int i = 0; i < keyFields.length; i++) {
			keyField = keyFields[i];
			if (keyField.getHCompoundSize().equals(HCompoundSize.DEFINED)) {
				if (keyField.isNullable()) {
					nullables.add(keyField);
				}
				defineds[i] = true;
				defined = true;
			}
		}
		if (nullables.size() == 0 || !(defined && checkNullables(nullables))) {
			return;
		}
		this.fullSize = getSize(this.keyFields);
		metaEntity.setPredefinedNullables(true);
		findNulls(nullables);
		metaEntity.setKeySizes(keySizes);
	}

	public boolean checkNullables(List<KeyField> nullables) {
		boolean valid = true;
		int coinsides;
		for (KeyField nullable : nullables) {
			coinsides = 0;
			for (KeyField keyField : nullables) {
				if (nullable.getName().equals(keyField.getName())) {
					coinsides++;
				}
				valid = (coinsides <= 1);
				if (!valid) {
					break;
				}
			}
			if (!valid) {
				break;
			}
		}
		return valid;
	}

	public int getSize(Iterable<KeyField> keyFields) {
		int size = 0;
		for (KeyField keyField : keyFields) {
			size += keyField.getSize();
		}
		return size;
	}

	private int getSize(KeyField[] keyFields) {
		int size = 0;
		for (KeyField keyField : keyFields) {
			size += keyField.getSize();
		}
		return size;
	}

	public void findNulls(List<KeyField> nullables) {
		int n = nullables.size();
		int[] indices;
		List<KeyField> subnulls = new ArrayList<KeyField>();
		for (int i = 1; i <= n; i++) {
			CombinationGenerator generator = new CombinationGenerator(n, i);
			while (generator.hasMore()) {
				subnulls.clear();
				indices = generator.getNext();
				for (int k = 0; k < indices.length; k++) {
					subnulls.add(nullables.get(indices[k]));
				}
				putNulls(subnulls);
			}
		}
	}

	public boolean[] getIndexes(List<KeyField> nullables) {
		KeyField keyField;
		boolean[] indexes = new boolean[keyFields.length];
		for (int i = 0; i < keyFields.length; i++) {
			keyField = keyFields[i];
			for (KeyField nullable : nullables) {
				if (nullable.equals(keyField)) {
					indexes[i] = true;
					break;
				}
			}
		}
		return indexes;
	}

	public void putNulls(List<KeyField> nullables) {
		if (fullSize == 0) {
			fullSize = getSize(keyFields);
		}
		int size = fullSize - getSize(nullables);
		if (keySizes.containsKey(size)) {
			keySizes.clear();
			metaEntity.setPredefinedNullables(false);
			metaEntity.setHCompoundSize(HCompoundSize.NONE);
		}
		keySizes.put(size, getIndexes(nullables));
	}

}

class CombinationGenerator {
	private int[] a;
	private int n;
	private int r;
	private BigInteger numLeft;
	private BigInteger total;

	// ------------
	// Constructor
	// ------------

	public CombinationGenerator(int n, int r) {
		if (r > n) {
			throw new IllegalArgumentException();
		}
		if (n < 1) {
			throw new IllegalArgumentException();
		}
		this.n = n;
		this.r = r;
		a = new int[r];
		BigInteger nFact = getFactorial(n);
		BigInteger rFact = getFactorial(r);
		BigInteger nminusrFact = getFactorial(n - r);
		total = nFact.divide(rFact.multiply(nminusrFact));
		reset();
	}

	// ------
	// Reset
	// ------

	private void reset() {
		for (int i = 0; i < a.length; i++) {
			a[i] = i;
		}
		numLeft = new BigInteger(total.toString());
	}

	// ------------------------------------------------
	// Return number of combinations not yet generated
	// ------------------------------------------------

	public BigInteger getNumLeft() {
		return numLeft;
	}

	// -----------------------------
	// Are there more combinations?
	// -----------------------------

	public boolean hasMore() {
		return numLeft.compareTo(BigInteger.ZERO) == 1;
	}

	// ------------------------------------
	// Return total number of combinations
	// ------------------------------------

	public BigInteger getTotal() {
		return total;
	}

	// ------------------
	// Compute factorial
	// ------------------

	private BigInteger getFactorial(int n) {
		BigInteger fact = BigInteger.ONE;
		for (int i = n; i > 1; i--) {
			fact = fact.multiply(new BigInteger(Integer.toString(i)));
		}
		return fact;
	}

	// --------------------------------------------------------
	// Generate next combination (algorithm from Rosen p. 286)
	// --------------------------------------------------------

	public int[] getNext() {

		if (numLeft.equals(total)) {
			numLeft = numLeft.subtract(BigInteger.ONE);
			return a;
		}

		int i = r - 1;
		while (a[i] == n - r + i) {
			i--;
		}
		a[i] = a[i] + 1;
		for (int j = i + 1; j < r; j++) {
			a[j] = a[i] + j - i;
		}

		numLeft = numLeft.subtract(BigInteger.ONE);
		return a;

	}
}
