package org.hbaseom.client.meta.cache;


/**
 * 
 * @author rezo
 */
public class ContentCentricByteArray {

	private byte[] data;

	public byte[] getData() {
		return data;
	}

	public ContentCentricByteArray(byte[] data) {
		this.data = data;
	}

	@Override
	public int hashCode() {
		return new String(data).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ContentCentricByteArray)) {
			return false;
		}
		byte[] otherBytes = ((ContentCentricByteArray) obj).getData();
		if (data.length != otherBytes.length) {
			return false;
		}
		for (int i = 0; i < data.length; i++) {
			if (data[i] != otherBytes[i]) {
				return false;
			}
		}
		return true;
	}

	public Object getKey() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
