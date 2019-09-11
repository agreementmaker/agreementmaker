package am.app.mappingEngine.similarityMatrix;


public class OptmizedSparseMatrixTest {
	/*@Test public void insertAndGet16000000() {
		for(int j = 0; j < 1; j++){
			System.out.println("staring run: "+j);
			Random r = new Random();
			//ArrayList<Integer> xVals = new ArrayList<Integer>();
			//ArrayList<Integer> yVals = new ArrayList<Integer>();
			
			for(int i = 0; i < 4000; i++){
				xVals.add(new Integer(i));
				yVals.add(new Integer(i));
			}
			
			OptimizedSparseMatrix m = new OptimizedSparseMatrix(3000,3000);
			//int[] x=new int[16000000];
			//int[] y=new int[16000000];
			//double[][] z=new double[3000][3000];
			long startInsert = System.currentTimeMillis();
			System.out.print("inserting:");
			for(int i=0;i<3000;i++){
				for(int k=0;k<3000;k++){
					double z1=r.nextDouble();
					//z[i][k]=z1;
					//System.out.println("inserting i="+i+" : ("+i+","+k+","+z1+")");
					m.set(i, k, new Mapping(z1));
				}
				if(i%400==0)
					System.out.print("->");
			}
			long endInsert=System.currentTimeMillis();
			
			
			
			//insert  entries in the matrix
			for(int i=0;i<4000;i++){
				int x1=r.nextInt(xVals.size());
				int y1=r.nextInt(yVals.size());
				double z1=r.nextDouble();
				
				//System.out.println("inserting i="+i+" : ("+xVals.get(x1)+","+yVals.get(y1)+","+z1+")");
				m.set(xVals.get(x1), yVals.get(y1), new Mapping(z1));
				x[i]=xVals.get(x1);
				y[i]=yVals.get(y1);
				z[i]=z1;
				
				xVals.remove(x1);
				yVals.remove(y1);
			}
			
			System.out.println();
			System.out.print("getting:");
			long startGet = System.currentTimeMillis();
			for(int i=0;i<3000;i++){
				for(int k=0;k<3000;k++){
					Mapping temp=m.get(i, k);
					//if(temp==null)
					//	System.out.println("mapping ("+i+","+k+") is null");
					//else
					//	System.out.println("got:("+i+","+k+")= "+temp.getSimilarity()+", run number: "+j);
					//System.out.println(z[i][k]);
					//assertTrue(temp.getSimilarity()==z[i][k]);
				}
				if(i%400==0)
					System.out.print("->");
			}
			
			//check the entries
			for(int i=0;i<4000;i++){
				//System.out.println("getting i="+i+" : ("+x[i]+","+y[i]+")");
				Mapping temp=m.get(x[i], y[i]);
				//System.out.println(x[i]+","+ y[i]);
				//System.out.println(temp);
				assertTrue(temp.getSimilarity()==z[i]);
			}
			
			System.out.println();
			long totalIn=endInsert-startInsert;
			System.out.println("total time, insert (in seconds): "+((double)totalIn/1000.0));
			
			long endGet=System.currentTimeMillis();
			long totalGet=endGet-startGet;
			
			System.out.println("total time, get (in seconds): "+((double)totalGet/1000.0));
		}
	}*/
}
