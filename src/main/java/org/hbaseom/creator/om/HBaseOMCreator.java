package org.hbaseom.creator.om;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.hbaseom.client.HBaseOM;
import org.hbaseom.client.meta.EntityDescriptor;
import org.hbaseom.creator.types.HBaseOMType;

public interface HBaseOMCreator {

	HBaseOM createHBaseClient(HBaseOMType hBaseClientType, boolean autoFlush,
			URL... urls) throws IOException;

	HBaseOM createHBaseClient(HBaseOMType hBaseClientType, URL... urls);

	void createTable(EntityDescriptor descriptor) throws IOException;

	HBaseOM createStand(String name, boolean autoFlush) throws IOException;

	HBaseOM createApp(InputStream client, InputStream site, String name,
			boolean autoFlush) throws IOException;

	void provide();
}
