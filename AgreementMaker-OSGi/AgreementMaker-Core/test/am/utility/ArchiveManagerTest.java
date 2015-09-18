package am.utility;

import org.junit.Test;

import am.app.Core;

public class ArchiveManagerTest {

	public static final String benchmarksArchive = "OAEI/2013/benchmarks/benchmarks.7z";
	
	@Test
	public void testDecoding() {
		ArchiveManager manager = new ArchiveManager();
		
		String root = Core.getInstance().getRoot();
		
		manager.decodeFile(root + benchmarksArchive);
		
	}
}
