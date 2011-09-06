package freebase;

import java.io.IOException;

import com.narphorium.freebase.query.Query;
import com.narphorium.freebase.query.io.QueryParser;
import com.narphorium.freebase.results.Result;
import com.narphorium.freebase.results.ResultSet;
import com.narphorium.freebase.services.ReadService;
import com.narphorium.freebase.services.exceptions.FreebaseServiceException;


public class Test {
	
	public static void main(String[] args){
				
		ReadService readService = new ReadService();
		QueryParser queryParser = new QueryParser();
		String queryText = "[{\"p0:id\":null,\"p1:name\":null,\"type\":\"/location/country\"}]";
		Query query = queryParser.parse("q1", queryText );
		try {
			ResultSet results = readService.read(query);
			while (results.hasNext()) {
				Result result = results.next();
				String countryId = result.getString("p0");
				String countryName = result.getString("p1");
				System.out.println(countryId + "\t" + countryName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FreebaseServiceException e) {
			e.printStackTrace();
		}
	}
}
