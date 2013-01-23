package am.app.mappingEngine.structuralMatchers.similarityFlooding.sfm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Vector;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.structuralMatchers.SimilarityFloodingParameters;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.FullGraphMatcher;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.SimilarityFloodingMatcherParameters;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WGraphEdge;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WGraphVertex;
import am.app.mappingEngine.structuralMatchers.similarityFlooding.utils.WrappingGraph;
import am.userInterface.MatchingProgressDisplay;

public class DBGraphMatcher extends FullGraphMatcher {

	private static final long serialVersionUID = 7551045342225723696L;
	
	// information needed by the database
	protected Connection connect = null;
	protected Statement statement = null;
	
	protected String insertVertexStatement;
	protected String insertEdgeStatement;

	/**
	 * 
	 */
	public DBGraphMatcher() {
		super();
		minInputMatchers = 0;
		maxInputMatchers = 0;
		setSortEdges(false);
		this.setUp();
		this.insertVertexStatement = "INSERT INTO pcgschema.\"pcgvertices\"(" +
        	"\"id\", \"prev_value\", \"curr_value\", \"node_type\", \"left\", \"right\", \"id_left\", \"id_right\") ";
		this.insertEdgeStatement = "INSERT INTO pcgschema.\"pcgedges\"(" +
			"\"prop_coeff\", \"property\", \"o_id\", \"d_id\") ";
	}

	/**
	 * @param params_new
	 */
	public DBGraphMatcher(SimilarityFloodingParameters params_new) {
		super(params_new);
		minInputMatchers = 0;
		maxInputMatchers = 0;
		setSortEdges(false);
		this.setUp();
		this.insertVertexStatement = "INSERT INTO pcgschema.\"pcgvertices\"(" +
		"\"id\", \"prev_value\", \"curr_value\", \"node_type\", \"left\", \"right\", \"id_left\", \"id_right\") ";
		this.insertEdgeStatement = "INSERT INTO pcgschema.\"pcgedges\"(" +
    		"\"prop_coeff\", \"property\", \"o_id\", \"d_id\") ";
	}
	
	/**
	 * Similarity Flooding Algorithm. 
	 * @see am.app.mappingEngine.AbstractMatcher#align(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes)
	 * NOTE: we are using graphs instead of arrayList
	 */
	@Override
	 protected void align() throws Exception {
		for( MatchingProgressDisplay mpd : progressDisplays ) mpd.clearReport();
		
		// cannot align just one ontology (this is here to catch improper invocations)
		if( sourceOntology == null ) throw new NullPointerException("sourceOntology == null");   
		if( targetOntology == null ) throw new NullPointerException("targetOntology == null");
		
		for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("Creating Wrapping Graphs...");
		WrappingGraph sourceGraph = new WrappingGraph(sourceOntology);
		WrappingGraph targetGraph = new WrappingGraph(targetOntology);
		if( DEBUG_FLAG ) System.out.println(sourceGraph.toString());
		if( DEBUG_FLAG ) System.out.println(targetGraph.toString());
		for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("done.\n");
		
		// loading similarity matrices (null values are permitted if WrappingGraphs are not used to build the Matrices)
		loadSimilarityMatrices(null, null);
		
		// PHASE 0: sorting edges (for optimization purposes)
		if(isSortEdges()){
			for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("Sorting Wrapping Graphs...");
			sourceGraph.sortEdges();
			targetGraph.sortEdges();
			if( DEBUG_FLAG ) System.out.println(sourceGraph.toString());
			if( DEBUG_FLAG ) System.out.println(targetGraph.toString());
			for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("done.\n");
		}
		
		// PHASE 1: creating PCG
		for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("Creating Pairwise Connectivity Graph...");
		createFullPCG(sourceGraph, targetGraph);
		if( DEBUG_FLAG ) System.out.println(pcg.toString());
		for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("done.\n");
		
		// PHASE 2: creating IPG
		for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("Creating Induced Propagation Graph...");
		createInducedPropagationGraph();
		if( DEBUG_FLAG ) System.out.println(pcg.toString());
		for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("done.\n");
		
		// PHASE 3: computing fixpoint
		for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("Computing Fixpoints...");
		computeFixpoint();
		for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("done.\n");
		
		// PHASE 4: update values in matrix
		for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("Populating Similarity Matrices...");
		populateSimilarityMatrices(connect, classesMatrix, propertiesMatrix);
		for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("done.\n");
		
		// PHASE 5: compute relative similarities
		for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("Computing Relative Similarities...");
		computeRelativeSimilarities(classesMatrix);
		computeRelativeSimilarities(propertiesMatrix);
		for( MatchingProgressDisplay mpd : progressDisplays ) mpd.appendToReport("done.\n");
		
//		try {
//			fw.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
	 }
	
	@Override
	protected void createFullPCG(WrappingGraph sourceOnt, WrappingGraph targetOnt){

		 Iterator<WGraphEdge> sourceIterator = sourceOnt.edges();
		 Iterator<WGraphEdge> targetIterator = targetOnt.edges();
		 Integer vertID = new Integer(0); Integer edgeId = new Integer(0);
		 Integer originID = -1, destID = -1;
		 
			 while(sourceIterator.hasNext()){
				 WGraphEdge sEdge = sourceIterator.next();
				 
				 while(targetIterator.hasNext()){
					 WGraphEdge tEdge = targetIterator.next();
					 
					 // condition where we add a new element in the pairwise connectivity graph:
					 // comparison of predicates (now string labels)
					 if(sEdge.getObject().equals(tEdge.getObject())){
						 // target property is equal to source property (go compute)
						 if( ((SimilarityFloodingMatcherParameters)param).omitAnonymousNodes && 
								 ( sEdge.getOrigin().getObject().isAnon() || sEdge.getDestination().getObject().isAnon() ||
								   tEdge.getOrigin().getObject().isAnon() || tEdge.getDestination().getObject().isAnon() )  ) {
							// these nodes are anonymous
							// parameter is set to not insert anonymous nodes
							// do nothing
						 } else {
							 
							 // insert source
							 try{
								 
								 boolean originFound = true, destFound = true, edgeFound = true;
								 
								 String aliasA = "a", vTable = "pcgschema.pcgvertices as " + aliasA; 
								 String informationQuery = "SELECT " + aliasA + ".id" + " FROM " + vTable +
								 " WHERE " +
								 aliasA + ".left=\'" + (WGraphVertex)sEdge.getOrigin() + "\'" +
								 " AND " +
								 aliasA + ".right=\'" + (WGraphVertex)tEdge.getOrigin() + "\'";
//								 System.out.println(informationQuery);
							 	try {
									statement = connect.createStatement();
									ResultSet rs = statement.executeQuery(informationQuery);
									if (rs.next()) {
										originID = rs.getInt(1);
										System.out.println(originID);
									}
									else{
										originFound = false;
									}
								} catch (SQLException e) {
									e.printStackTrace();
								}
								 
								if(!originFound){
									vertID++;
									originID = vertID;
									String valuesString = "'" + Integer.toString(originID) + "', " + // id		
											"'1.0', '1.0', " + // old, new sim values
											"'" + ((WGraphVertex)sEdge.getOrigin()).getNodeType().toString() + "', " + // type of the node couple
											"'" + (WGraphVertex)sEdge.getOrigin() + "', " + // source Wgraph node
											"'" + (WGraphVertex)tEdge.getOrigin() + "', " + // target Wgraph node
											"'" + ((WGraphVertex)sEdge.getOrigin()).getMatrixIndex() + "', " + // source Wgraph node id
											"'" + ((WGraphVertex)tEdge.getOrigin()).getMatrixIndex() + "' "; // target Wgraph node id
											
											
									String sqlInsertScript = insertVertexStatement+ "VALUES (" + valuesString  + ");";
									System.out.println(sqlInsertScript);
									try {
										statement = connect.createStatement();
										statement.executeUpdate(sqlInsertScript);
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
									
								// insert target
								aliasA = "a"; vTable = "pcgschema.pcgvertices as " + aliasA; 
								informationQuery = "SELECT " + aliasA + ".id" + " FROM " + vTable +
								 	 " WHERE " +
								 	aliasA + ".left=\'" + (WGraphVertex)sEdge.getDestination()+ "\'" +
									 " AND " +
									 aliasA + ".right=\'" + (WGraphVertex)tEdge.getDestination() + "\'";
								System.out.println(informationQuery);
							 	try {
									statement = connect.createStatement();
									ResultSet rs = statement.executeQuery(informationQuery);
									if (rs.next()) {
										destID = rs.getInt(1);
									}
									else{
										destFound = false;
									}
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								if(!destFound){
									vertID++;
									destID = vertID;
									String valuesString = "'" + Integer.toString(destID) + "', " + // id		
										"'1.0', '1.0', " + // old, new sim values
										"'" + ((WGraphVertex)sEdge.getDestination()).getNodeType().toString() + "', " + // type of the node couple
										"'" + (WGraphVertex)sEdge.getDestination() + "', " + // source Wgraph node 
										"'" + (WGraphVertex)tEdge.getDestination() + "', " + // target Wgraph node
										"'" + ((WGraphVertex)sEdge.getDestination()).getMatrixIndex() + "', " + // source Wgraph node id
										"'" + ((WGraphVertex)tEdge.getDestination()).getMatrixIndex() + "' "; // target Wgraph node id
									
									String sqlInsertScript = insertVertexStatement+ "VALUES (" + valuesString  + ");";
									System.out.println(sqlInsertScript);
									try {
										statement = connect.createStatement();
										statement.executeUpdate(sqlInsertScript);
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
								
								// insert edge
								String aliasB = "b", eTable = "pcgschema.pcgedges as " + aliasB; 
								informationQuery = "SELECT " + aliasB + ".o_id," + aliasB + ".d_id" + " FROM " + eTable +
							 	 " WHERE " +
								 aliasB + ".o_id=\'" + Integer.toString(originID) + "\'" +
								 " AND " +
								 aliasB + ".d_id=\'" + Integer.toString(destID) + "\'";
						 	
							 	try {
									statement = connect.createStatement();
									ResultSet rs = statement.executeQuery(informationQuery);
									if (!rs.next()) {
										edgeFound = false;
									}
								} catch (SQLException e) {
									e.printStackTrace();
								}
								if(!edgeFound){
									edgeId++;
									String valuesString = "'1.0', " + // propCoefficient						
										"'" + sEdge.getObject() + "', " + // edgeName
										"'" + Integer.toString(originID) + "', " + // source Wgraph id
										"'" + Integer.toString(destID) + "'"; // target Wgraph id 
									
									String sqlInsertScript = insertEdgeStatement+ "VALUES (" + valuesString  + ");";
									System.out.println(sqlInsertScript);
									try {
										statement = connect.createStatement();
										statement.executeUpdate(sqlInsertScript);
									} catch (SQLException e) {
										e.printStackTrace();
									}
								}

							 }
							 catch(com.hp.hpl.jena.rdf.model.ResourceRequiredException e){
								 e.printStackTrace();
							 }
						 }
						 
					 }
					 else{
						 // target property is greater than source property
						 // (since egdes are sorted we break the target cycle and go to the next source edge)
						 continue;
					 }
				 }
				 targetIterator = targetOnt.edges();
			 }
	 }
	
	@Override
	protected void createInducedPropagationGraph(){
		
		// PART 1: create backward edges
		String aliasA = "a", eTable = "pcgschema.pcgedges as " + aliasA;
		
		int count = 0;
		String countQuery = "SELECT COUNT(*) FROM " + eTable;
		String retrieval_query = "SELECT * FROM pcgschema.pcgedges as a" +
								" EXCEPT " +
								"SELECT a.* FROM pcgschema.pcgedges as a, pcgschema.pcgedges as b " +
								"WHERE a.o_id=b.d_id AND a.d_id=b.o_id;";
	 	try {
			statement = connect.createStatement();
			ResultSet rs = statement.executeQuery(countQuery);
			if (rs.next()) {
				count = rs.getInt(1); // number of edges in the table
			}
			else{
				new Exception("No results");
			}
			
			// creating backedges (if table changes, code has to change too)
			Statement statement1 = connect.createStatement();
			rs = statement1.executeQuery(retrieval_query);
			int i = 0;
			while(rs.next()){
				
				String valuesString = "'" + rs.getString(1) + "', " + // prop_coeff		
					"'" + rs.getString(2) + "', " + // property
					"'" + rs.getString(4) + "', " + // target Wgraph node 
					"'" + rs.getString(3) + "' "; // source Wgraph node 
				
				// edges
				String sqlInsertScript = insertEdgeStatement + "VALUES (" + valuesString  + ");";
				System.out.println(sqlInsertScript);
				statement.executeUpdate(sqlInsertScript);
				if(i >= count) break;
				i++;
			}
			
			// PART 2: applyCoefficients
			Statement statement2 = connect.createStatement();
			rs = statement2.executeQuery(countQuery); // take the number of the edges in the IPGs
			rs.next();
			count = rs.getInt(1);
			

			Statement statement3 = connect.createStatement();
			retrieval_query = "SELECT DISTINCT property,o_id,COUNT(*) FROM pcgschema.pcgedges GROUP BY property,o_id;";
			rs = statement3.executeQuery(retrieval_query);
			i = 0;
			while(rs.next()){
				
				// we take the number of edges out (we apply the changes only if there are only more than one egdes out)
				int edgesOut = rs.getInt(3);
				if(edgesOut > 1){

					// we consider a couple (property,originID) at a time
					String single_group_query = "SELECT DISTINCT * FROM pcgschema.pcgedges WHERE ";
					String where_clause = "property='" + rs.getString(1) + "'" + "AND" + " o_id='" + rs.getString(2) + "'";
					
					Statement statement4 = connect.createStatement();
					ResultSet innerRS = statement4.executeQuery(single_group_query + where_clause);
					while(innerRS.next()){
						String update_query = "UPDATE pcgschema.pcgedges SET prop_coeff='" + innerRS.getDouble(1)/rs.getDouble(3) + "'";
						String update_where_clause = " WHERE o_id='" + innerRS.getInt(3) + "' AND d_id='" + innerRS.getInt(4) + "'";
						
						Statement statement5 = connect.createStatement();
						System.out.println(update_query + update_where_clause);
						statement5.executeUpdate(update_query + update_where_clause);
					}
				
				}
				
				if(i >= count) break;
				i++;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 }
	
	@Override
	protected void computeFixpoint(){
		 int round = 0;
		 Vector<Double> oldV , newV;
		 do {
			 // new round starts
			 round++;
			 
			 // update old value with new value of previous round
			 updateOldSimValues(this.connect, round);
			 
			 // compute fixpoint round and max value per that round
			 double maxSimilarity = computeFixpointRound(this.connect);
			 
			 // normalize all the similarity values of all nodes
			 normalizeSimilarities(this.connect, maxSimilarity);

			 // stop condition check: delta or maxRound			 
			 oldV = getSimValueVector(true);
			 newV = getSimValueVector(false);

		 } while(!checkStopCondition(round, oldV, newV));
	 }
	
	private void updateOldSimValues(Connection connect, int round){
		
		if (round != 1) {
			try {
				// get new values
				Statement s = connect.createStatement();
				String retrieveOldValueScript = "SELECT id,curr_value FROM pcgschema.pcgvertices;";
				ResultSet rs = s.executeQuery(retrieveOldValueScript);

				while (rs.next()) {
					// update old values with the new ones
					Statement s1 = connect.createStatement();
					String updateNewValueScript = "UPDATE pcgschema.pcgvertices "
							+ "SET curr_value='" + rs.getDouble(2) + "' "
							+ "WHERE id='" + rs.getInt(1) + "';";
					s1.executeUpdate(updateNewValueScript);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	 }
	
	private double computeFixpointRound(Connection connect){
		try {
			// get new values
			Statement s = connect.createStatement();
			String computeValuesFromScript = "SELECT y.id, y.prev_value + y.product_sum as sum " +
												"FROM( " +
													"SELECT x.id, x.prev_value, sum(x.product) as product_sum " +
													"FROM( " +
														"SELECT c.id, a.prev_value, b.prop_coeff * c.curr_value as product " +
														"FROM pcgschema.pcgvertices as a, pcgschema.pcgedges as b, pcgschema.pcgvertices as c " +
														"WHERE a.id=b.o_id AND c.id=b.d_id " +
														") as x " +
													"GROUP BY x.id, x.prev_value" +
												") as y;";
			ResultSet rs = s.executeQuery(computeValuesFromScript);

			while (rs.next()) {
				// update old values with the new ones
				Statement s1 = connect.createStatement();
				String updateNewValueScript = "UPDATE pcgschema.pcgvertices "
						+ "SET curr_value='" + rs.getDouble(2) + "' "
						+ "WHERE id='" + rs.getInt(1) + "';";
				s1.executeUpdate(updateNewValueScript);
			}
			
			Statement s2 = connect.createStatement();
			String computeMaxScript = "SELECT MAX(curr_value) " +
									"FROM pcgschema.pcgvertices as a;";
			ResultSet rs2 = s2.executeQuery(computeMaxScript);
			rs2.next();
			return rs2.getDouble(1);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0.0;
	}
	
	//@Override
	protected void normalizeSimilarities(Connection connect, double roundMax){
		
		try {
			Statement s = connect.createStatement();
			String retrieveValuesScript = "SELECT a.id,a.curr_value FROM pcgschema.pcgvertices as a;";
			ResultSet rs = s.executeQuery(retrieveValuesScript);

			while (rs.next()) {
				// update normalize new values
				Statement s1 = connect.createStatement();
				String updateNewValueScript = "UPDATE pcgschema.pcgvertices "
						+ "SET curr_value='" + rs.getDouble(2)/roundMax + "' "
						+ "WHERE id='" + rs.getInt(1) + "';";
				s1.executeUpdate(updateNewValueScript);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	 }
	
	public Vector<Double> getSimValueVector(boolean old){
		Vector<Double> simVector = new Vector<Double>();
		try {
			Statement s = connect.createStatement();
			String retrieveValuesScript = "";
			if(old){
				retrieveValuesScript = "SELECT a.prev_value FROM pcgschema.pcgvertices as a;";
			}
			else{
				retrieveValuesScript = "SELECT a.curr_value FROM pcgschema.pcgvertices as a;";
			}
			ResultSet rs = s.executeQuery(retrieveValuesScript);

			while (rs.next()) {
				// get values and copy in vector
				simVector.add(rs.getDouble(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return simVector;
	}
	
	//@Override
	protected void populateSimilarityMatrices(Connection connect, SimilarityMatrix cMatrix, SimilarityMatrix pMatrix){
		
		try {
			Statement s = connect.createStatement();
			String retrieveValuesScript = "SELECT curr_value,node_type,id_left,id_right FROM pcgschema.pcgvertices;";
			ResultSet rs = s.executeQuery(retrieveValuesScript);
			while(rs.next()){
				Mapping m;
				if(rs.getString(2).equals("CLASS")){
					m = new Mapping(sourceOntology.getNodefromIndex(rs.getInt(3), alignType.aligningClasses),
										targetOntology.getNodefromIndex(rs.getInt(4), alignType.aligningClasses),
										rs.getDouble(1));
					cMatrix.set(m.getSourceKey(), m.getTargetKey(), m);
				 }
				 else if(rs.getString(2).equals("PROPERTY")){
					m = new Mapping(sourceOntology.getNodefromIndex(rs.getInt(3), alignType.aligningProperties),
										targetOntology.getNodefromIndex(rs.getInt(4), alignType.aligningProperties),
										rs.getDouble(1));
					 pMatrix.set(m.getSourceKey(), m.getTargetKey(), m);
				 }
				 else{
					 // TODO: manage type error
				 }
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	 }
	
	public void setUp() {
		// This will load the PostgreSQL driver, each DB has its own driver
		try {
			Class.forName("org.postgresql.Driver");
			// Setup the connection with the DB
			// TODO: ask for user and password
			// connect = DriverManager.getConnection("jdbc:postgresql://hostname:port/dbname","username", "password");
			connect = DriverManager.getConnection("jdbc:postgresql://localhost:5432/pcgStorer","postgres", "legione");
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void tearDown() {
		close();
	}
	
	protected void close() {
		try {

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}
	
	/**
	 *
	 */
	@Override
	protected void loadSimilarityMatrices(WrappingGraph s, WrappingGraph t){
		// load classesMatrix
		classesMatrix = new ArraySimilarityMatrix(sourceOntology, targetOntology, alignType.aligningClasses);
		// load propertiesMatrix
		propertiesMatrix = new ArraySimilarityMatrix(sourceOntology, targetOntology, alignType.aligningProperties);

	}
	

}
