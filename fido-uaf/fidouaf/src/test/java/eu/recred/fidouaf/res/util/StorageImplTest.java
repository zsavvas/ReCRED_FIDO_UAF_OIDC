package eu.recred.fidouaf.res.util;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class StorageImplTest {

	@Test
	public void basic() {
		assertNotNull(StorageImpl.getInstance());
	}

}
