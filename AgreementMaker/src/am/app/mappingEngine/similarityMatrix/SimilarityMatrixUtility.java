package am.app.mappingEngine.similarityMatrix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import am.app.mappingEngine.SimilarityMatrix;

public class SimilarityMatrixUtility {

	public boolean saveMatrix(File outputFile, SimilarityMatrix matrix) {
		
		try {
/*			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFile));
			BufferedOutputStream buf = new BufferedOutputStream(zos);
			DataOutputStream data = new DataOutputStream(buf);
		
			ZipEntry entry = new ZipEntry("matrix.bin");
			
			zos.putNextEntry(entry);
			
			int rows = matrix.getRows();
			int cols = matrix.getColumns();
			
			buf.write(rows);
			buf.write(cols);
			
			for( int i = 0; i < rows; i++ ) {
				for( int j = 0; j < cols; j++ ) {
					data.writeDouble(matrix.getSimilarity(i, j));
				}
			}
			
			data.flush();
			data.close();*/
			
			FileOutputStream fos = new FileOutputStream(outputFile);
			GZIPOutputStream gz = new GZIPOutputStream(fos);
		    ObjectOutputStream oos = new ObjectOutputStream(gz);
		    
		    oos.writeObject(matrix);
		    
		    oos.flush();
		    oos.close();
		    fos.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return false;
	}
	
	public SimilarityMatrix loadMatrix(File inputFile) {
		try {
			FileInputStream fis = new FileInputStream(inputFile);
			GZIPInputStream gs = new GZIPInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(gs);
			SimilarityMatrix matrix = (SimilarityMatrix) ois.readObject();
			ois.close();
			fis.close();
			
			return matrix;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
