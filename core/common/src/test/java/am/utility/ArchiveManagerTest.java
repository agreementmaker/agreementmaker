package am.utility;

import org.junit.Ignore;
import org.junit.Test;

import am.app.Core;

public class ArchiveManagerTest {

	public static final String benchmarksArchive = "OAEI/2013/benchmarks/benchmarks.7z";
	
	@Test
	@Ignore // benchmarks.7z doesn't exist, can't run this test until that's fixed
	public void testDecoding() {
		ArchiveManager manager = new ArchiveManager();
		
		String root = Core.getInstance().getRoot();
		
		manager.decodeFile(root + benchmarksArchive);
		
	}
}
