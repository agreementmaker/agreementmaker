package am.extension.userfeedback.WrongFBExperiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.evaluation.clustering.Cluster;
import am.extension.userfeedback.FeedbackPropagation;
import am.extension.userfeedback.UFLExperiment;
import am.extension.userfeedback.UserFeedback.Validation;
import am.utility.BitVector;

public class ReclusterPropagation extends FeedbackPropagation<UFLExperiment> {

	List<AbstractMatcher> inputMatchers = null;
	List<Object[]> signatureVectors = new ArrayList<Object[]>();
	double[][] similarityMtrx = null;
	List<ArrayList<Pair<Mapping, Integer>>> mapArray = null;
	Alignment<Mapping> alignment = null;

	public double calculateNorm(Object[] vector) {
		double norm = 0.0;
		for (int i = 0; i < vector.length; i++) {
			norm += Math.pow((double) vector[i], 2);
		}
		return Math.sqrt(norm);
	}

	public double getCosSimilarity(Object[] vector1, Object[] vector2) {
		double product = 0.0;
		double cosSimilarity = 0.0;
		double factor1 = 0.0;
		double factor2 = 0.0;
		if (vector1.length != vector2.length)
			return cosSimilarity;
		for (int i = 0; i < vector1.length; i++) {
			product += (double) vector1[i] * (double) vector2[i];
		}
		factor1 = calculateNorm(vector1);
		factor2 = calculateNorm(vector2);
		if (factor1 != 0 && factor2 != 0) {
			cosSimilarity = product / (factor1 * factor2);
		}
		return cosSimilarity;
	}

	public List<ArrayList<Pair<Mapping, Integer>>> fillMapArray(
			UFLExperiment experiment) {

/*		if (this.alignment == null)
			this.alignment = experiment.getFinalAlignment();
		// get the maximum value of index of source node and target node
		int targetMaxIndex = 0;
		int sourceMaxIndex = 0;
		for (int i = 0; i < alignment.size(); i++) {
			int tmp = alignment.get(i).getSourceKey();
			if (tmp > sourceMaxIndex)
				sourceMaxIndex = tmp;
			tmp = alignment.get(i).getTargetKey();
			if (tmp > targetMaxIndex)
				targetMaxIndex = tmp;
		}*/
		//sourceMaxIndex = sourceMaxIndex+10;
		//targetMaxIndex = targetMaxIndex+10;

		int sourceMaxIndex = experiment.getSourceOntology().getClassesList().size()+experiment.getSourceOntology().getPropertiesList().size();
		int targetMaxIndex = experiment.getTargetOntology().getClassesList().size()+experiment.getTargetOntology().getPropertiesList().size();
		// create the space for mapArray
		this.mapArray = new ArrayList<ArrayList<Pair<Mapping, Integer>>>();
		for (int i = 0; i < sourceMaxIndex + 1; i++) {
			ArrayList<Pair<Mapping, Integer>> tmpList = new ArrayList<Pair<Mapping, Integer>>();
			for (int j = 0; j < targetMaxIndex + 1; j++) {
				Mapping tmpMp = null;
				Pair<Mapping, Integer> tmpPair = new Pair<Mapping, Integer>(
						tmpMp, 0);
				tmpList.add(tmpPair);
			}
			this.mapArray.add(tmpList);
		}

		System.out.println("mapArray size: "+mapArray.size()+", sourceMaxIndex: "+sourceMaxIndex+", targetMaxIndex: "+targetMaxIndex);
		for (int i = 0; i < alignment.size(); i++) {
			Mapping mp = alignment.get(i);
			int sourceIndex = mp.getSourceKey();
			int targetIndex = mp.getTargetKey();
			// System.out.println(sourceIndex+"-----"+targetIndex);
			this.mapArray.get(sourceIndex).get(targetIndex)
					.set(new Pair<Mapping, Integer>(mp, i));
		}
		return this.mapArray;
	}

	public List<Object[]> fillSignatureVector(UFLExperiment experiment) {
		// fill SignatureVectors
		if (this.alignment == null)
			this.alignment = experiment.getFinalAlignment();
		int size = alignment.size();

		if (!this.signatureVectors.isEmpty()) {
			this.signatureVectors.clear();
		}
		for (int i = 0; i < size; i++) {
			Mapping mp = alignment.get(i);
			Object[] vector = getSignatureVector(mp);
			this.signatureVectors.add(i, vector.clone());
		}
		return this.signatureVectors;
	}

	public double[][] fillSimilarityMtrx() { // cosine value for all mappings
		int size = this.signatureVectors.size();
		this.similarityMtrx = new double[size][size];

		for (int i = 0; i < size; i++) {
			for (int j = i; j < size; j++) {
				Object[] vector1 = signatureVectors.get(i);
				Object[] vector2 = signatureVectors.get(j);
				double similarity = getCosSimilarity(vector1, vector2);
				this.similarityMtrx[i][j] = similarity;
				this.similarityMtrx[j][i] = similarity;
			}
		}
		return this.similarityMtrx;
	}

	public Tuple<Mapping, Cluster<Mapping>, Double, HashMap<Mapping, Double>> getCluster(
			Mapping map, double threshold) {
		Cluster<Mapping> c = new Cluster<Mapping>();
		int targetIndex = map.getTargetKey();
		int sourceIndex = map.getSourceKey();
		HashMap<Mapping, Double> m = new HashMap<Mapping, Double>();
		// System.out.println(sourceIndex+"---"+targetIndex+"---"+this.mapArray.size());
		int index = this.mapArray.get(sourceIndex).get(targetIndex).getSecond();

		for (int i = 0; i < this.signatureVectors.size(); i++) {
			if (i == index)
				continue;
			double similarity = this.similarityMtrx[index][i];
			if (similarity < threshold) {
				c.addMapping(this.alignment.get(i));
				m.put(this.alignment.get(i), 0.0d);
			}
		}
		Tuple<Mapping, Cluster<Mapping>, Double, HashMap<Mapping, Double>> tuple = new Tuple<Mapping, Cluster<Mapping>, Double, HashMap<Mapping, Double>>(
				map, c, threshold, m);
		return tuple;
	}

	// public Cluster<Mapping> getCluster(Mapping map, double threshold){
	// //get cluster of mappings via the vector of each mapping,
	// // TreeSet<Point> intersectionSet = null;
	// Cluster<Mapping> c = new Cluster<Mapping>();
	// int targetIndex = map.getTargetKey();
	// int sourceIndex = map.getSourceKey();
	// int index = this.mapArray.get(sourceIndex).get(targetIndex).getSecond();
	//
	// for(int i=0; i< this.signatureVectors.size(); i++){
	// // if(i==index)
	// // continue;
	// double similarity = this.similarityMtrx[index][i];
	// if(similarity < threshold)
	// c.addMapping(this.alignment.get(i));
	// }
	// return c;
	// }

	public Tuple<Mapping, Cluster<Mapping>, Double, HashMap<Mapping, Double>> ReCluster(
			Tuple<Mapping, Cluster<Mapping>, Double, HashMap<Mapping, Double>> orginTuple,
			Mapping deletedMap) {
		Mapping centroidMap = orginTuple.getFirst();
		Cluster<Mapping> c = new Cluster<Mapping>();
		HashMap<Mapping, Double> m = new HashMap<Mapping, Double>();

		int index1 = this.mapArray.get(centroidMap.getSourceKey())
				.get(centroidMap.getTargetKey()).getSecond();
		int index2 = this.mapArray.get(deletedMap.getSourceKey())
				.get(deletedMap.getTargetKey()).getSecond();
		double distance = this.similarityMtrx[index1][index2];
		double maximum = 0.0;
		for (int i = 0; i < this.alignment.size(); i++) {
			;
			if (index1 == i)
				continue;
			double similarity = this.similarityMtrx[index1][i];
			if (similarity < distance) {
				c.addMapping(this.alignment.get(i));
				m.put(this.alignment.get(i), 0.0d);
				if (similarity > maximum)
					maximum = similarity;
			}
		}

		return new Tuple<Mapping, Cluster<Mapping>, Double, HashMap<Mapping, Double>>(
				centroidMap, c, maximum, m);
	}

	public Cluster<Mapping> ReCluster(Mapping centroidMap, Mapping deletedMap) {
		Cluster<Mapping> c = new Cluster<Mapping>();

		int index1 = this.mapArray.get(centroidMap.getSourceKey())
				.get(centroidMap.getTargetKey()).getSecond();
		int index2 = this.mapArray.get(deletedMap.getSourceKey())
				.get(deletedMap.getTargetKey()).getSecond();
		double distance = this.similarityMtrx[index1][index2];
		double maximum = 0.0;
		for (int i = 0; i < this.alignment.size(); i++) {
			;
			// if(index1 == i)
			// continue;
			double similarity = this.similarityMtrx[index1][i];
			if (similarity < distance) {
				c.addMapping(this.alignment.get(i));
				if (similarity > maximum)
					maximum = similarity;
			}
		}
		return c;
	}

	public Object[] getSignatureVector(Mapping map) {
		int size = inputMatchers.size();
		Node sourceNode = map.getEntity1();
		Node targetNode = map.getEntity2();
		AbstractMatcher a;
		Object[] ssv = new Object[size];

		for (int i = 0; i < size; i++) {
			a = inputMatchers.get(i);
			ssv[i] = a.getAlignment().getSimilarity(sourceNode, targetNode);
		}
		return ssv;
	}

	public boolean validSsv(Object[] ssv) {
		Object obj = 0.0;
		for (int i = 0; i < ssv.length; i++) {
			if (!ssv[i].equals(obj))
				return true;
		}
		return false;
	}

	public Object[] addToSV(Mapping map, Boolean label) {
		// initialMatcher
		int size = inputMatchers.size();
		Node sourceNode = map.getEntity1();
		Node targetNode = map.getEntity2();
		AbstractMatcher a;
		Object obj = new Object();
		Object[] ssv = new Object[size + 1];
		for (int i = 0; i < size; i++) {
			a = inputMatchers.get(i);
			obj = a.getAlignment().getSimilarity(sourceNode, targetNode);
			if (obj != null)
				ssv[i] = obj;
			else
				ssv[i] = 0.0;
		}
		if (label)
			ssv[size] = 1.0;
		else
			ssv[size] = 0.0;

		return ssv;
	}

	@Override
	public void propagate(UFLExperiment experiment) {
		System.out.print("*********\nHERE WE GO\n##########");

		double threshold = 0.4d;
		double e = 0.1d;
		boolean allDone = false;

		// initialize
		inputMatchers = experiment.initialMatcher.getComponentMatchers();
		UFLExperiment log = experiment;
		this.fillSignatureVector(experiment); // use for cluster
		this.fillMapArray(experiment);
		this.fillSimilarityMtrx(); // use for cluster

		int iteration = experiment.getIterationNumber();

		// get current mapping for user validation
		Mapping candidateMapping = experiment.userFeedback
				.getCandidateMapping();

		// get cluster
		System.out.println(candidateMapping.getSourceKey()+"-------"+candidateMapping.getTargetKey());
		Tuple<Mapping, Cluster<Mapping>, Double, HashMap<Mapping, Double>> cluster = getCluster(
				candidateMapping, threshold);

		System.out.println("Here or not?");
		// get user's validation
		Validation userfeedback = experiment.userFeedback.getUserFeedback();

		// The ith tuple in tuple list contains candidate mapping;
		ArrayList<Integer> conflict = new ArrayList<Integer>();
		boolean exist = false;
		for (int i = 0; i < experiment.tupleList.size(); i++) {
			Tuple<Mapping, Cluster<Mapping>, Double, HashMap<Mapping, Double>> t = experiment.tupleList
					.get(i);
			for (Mapping map : t.getSecond()) {
				if (candidateMapping.equals(map)) {
					conflict.add(i);
					exist = true;
					break;
				}
			}
		}

		// if tuple list is null, add the first tuple in it
		// Or if candidate mapping hasn't been changed, set candidate mapping's
		// similarity
		// then propagate and add to tupleList
		if (iteration == 0 || !exist) {
			// propagate
			System.out.println("Iteration: " + iteration);
			propagationExecute(experiment, candidateMapping, userfeedback, log,
					cluster, e);
		} else { // if exist, check conflict list.
			System.out.println("Iteration: " + iteration);
			if (conflict.size() == 1) { // the ith tuple in tupleList is
										// conflict
				Tuple<Mapping, Cluster<Mapping>, Double, HashMap<Mapping, Double>> t = experiment.tupleList
						.get(conflict.get(0));

				if ((userfeedback == Validation.CORRECT && t.getForth().get(
						candidateMapping) > 0)
						|| (userfeedback == Validation.INCORRECT && t
								.getForth().get(candidateMapping) < 0)) { // valid
																			// answer
																			// is
																			// the
																			// same
																			// with
																			// reward
																			// or
																			// penalty
					propagationExecute(experiment, candidateMapping,
							userfeedback, log, cluster, e);
				} else { // valid answer is different from reward or penalty
							// if two mapping is close enough, ignore feedback.
					Mapping map = (Mapping) t.getFirst();
					int index = this.mapArray.get(t.getFirst().getSourceKey())
							.get(t.getFirst().getTargetKey()).getSecond(); // index
																			// of
																			// mapping
																			// in
																			// SimilarityMtrx
					int index2 = this.mapArray
							.get(candidateMapping.getSourceKey())
							.get(candidateMapping.getTargetKey()).getSecond(); // index
																				// of
																				// candidate
																				// mapping
																				// in
																				// SimilarityMtrx

					// distance of two conflict mapping
					double dist = similarityMtrx[index][index2];

					// calculate average distance to the mapping in its cluster
					double dist_avg = 0.0d;
					for (Mapping m : t.getSecond()) {
						int tmp = this.mapArray.get(m.getSourceKey())
								.get(m.getTargetKey()).getSecond();
						dist_avg += similarityMtrx[index][tmp];
					}
					dist_avg = dist_avg / t.getSecond().getMappingList().size();

					// if two mapping is too far, re-cluster
					// restore candidate mapping's value
					if (dist > dist_avg) {
						Tuple<Mapping, Cluster<Mapping>, Double, HashMap<Mapping, Double>> tmp = ReCluster(
								t, candidateMapping);
						Cluster<Mapping> oldCluster = t.getSecond();
						Cluster<Mapping> newCluster = tmp.getSecond();
						// if a mapping is still in the cluster, don't change
						// anything
						for (Mapping mn : newCluster) {
							if (oldCluster.getMappingList().contains(mn)) {
								tmp.getForth().put(mn, t.getForth().get(mn));
							} else {
								// if a mapping is not in the cluster, retore
								// their values
								if (candidateMapping.getAlignmentType() == alignType.aligningClasses) {
									SimilarityMatrix feedbackClassMatrix = experiment.initialMatcher
											.getFinalMatcher()
											.getClassesMatrix();
									double simi = feedbackClassMatrix.get(
											mn.getSourceKey(),
											mn.getTargetKey()).getSimilarity();
									feedbackClassMatrix.setSimilarity(
											mn.getSourceKey(),
											mn.getTargetKey(), simi
													- t.getForth().get(mn));
								} else if (candidateMapping.getAlignmentType() == alignType.aligningProperties) {
									SimilarityMatrix feedbackPropertyMatrix = experiment.initialMatcher
											.getFinalMatcher()
											.getPropertiesMatrix();
									double simi = feedbackPropertyMatrix.get(
											mn.getSourceKey(),
											mn.getTargetKey()).getSimilarity();
									feedbackPropertyMatrix.setSimilarity(
											mn.getSourceKey(),
											mn.getTargetKey(), simi
													- t.getForth().get(mn));
								}
							}

						}
						experiment.tupleList.set(0, tmp);
						propagationExecute(experiment, candidateMapping,
								userfeedback, log, cluster, e);

					}

				}
			} else {
				// if conflict > 1. Then vote for the correct answer according
				// to all tuples that are related to this mapping.
				int positive = 0;
				int negative = 0;
				for (int i = 0; i < conflict.size(); i++) {
					Tuple<Mapping, Cluster<Mapping>, Double, HashMap<Mapping, Double>> t = experiment.tupleList
							.get(conflict.get(i));
					if (t.getForth().get(candidateMapping) > 0)
						positive++;
					else
						negative++;
				}
				// if the feedback from user is different from the vote answer,
				// ignore this feedback

				// if the feedback show same result with vote answer, re-cluster
				// all the wrong tuples and restore all the values. then
				// propagate.
				ArrayList<Tuple<Mapping, Cluster<Mapping>, Double, HashMap<Mapping, Double>>> tmp = new ArrayList<Tuple<Mapping, Cluster<Mapping>, Double, HashMap<Mapping, Double>>>();
				ArrayList<Integer> index = new ArrayList<Integer>();
				if ((positive >= negative && userfeedback == Validation.CORRECT)
						|| (positive < negative && userfeedback == Validation.INCORRECT)) {

					if (userfeedback == Validation.CORRECT) {
						for (int i = 0; i < conflict.size(); i++) {
							Tuple<Mapping, Cluster<Mapping>, Double, HashMap<Mapping, Double>> tup = experiment.tupleList
									.get(conflict.get(i));
							if (tup.getForth().get(candidateMapping) < 0) {
								tmp.add(tup);
								index.add(conflict.get(i));
							}
						}
					}
					if (userfeedback == Validation.INCORRECT) {
						for (int i = 0; i < conflict.size(); i++) {
							Tuple<Mapping, Cluster<Mapping>, Double, HashMap<Mapping, Double>> tup = experiment.tupleList
									.get(conflict.get(i));
							if (tup.getForth().get(candidateMapping) > 0) {
								tmp.add(tup);
								index.add(conflict.get(i));
							}
						}
					}

					// ArrayList tmp store all the tuples that should be
					// re-cluster and re-store the values of candidate mapping.
					for (int i = 0; i < tmp.size(); i++) {
						Tuple<Mapping, Cluster<Mapping>, Double, HashMap<Mapping, Double>> tups = ReCluster(
								tmp.get(i), candidateMapping);
						Cluster<Mapping> oldCluster = tmp.get(i).getSecond();
						Cluster<Mapping> newCluster = tups.getSecond();

						for (Mapping mn : newCluster) {
							// if a mapping is still in the cluster, don't
							// change anything
							if (oldCluster.getMappingList().contains(mn)) {
								tups.getForth().put(mn,
										tmp.get(i).getForth().get(mn));
							}
							// if a mapping is not in the cluster, retore their
							// values
							else {
								if (candidateMapping.getAlignmentType() == alignType.aligningClasses) {
									SimilarityMatrix feedbackClassMatrix = experiment.initialMatcher
											.getFinalMatcher()
											.getClassesMatrix();
									double simi = feedbackClassMatrix.get(
											mn.getSourceKey(),
											mn.getTargetKey()).getSimilarity();
									feedbackClassMatrix.setSimilarity(
											mn.getSourceKey(),
											mn.getTargetKey(), simi
													- tmp.get(i).getForth()
															.get(mn));
								} else if (candidateMapping.getAlignmentType() == alignType.aligningProperties) {
									SimilarityMatrix feedbackPropertyMatrix = experiment.initialMatcher
											.getFinalMatcher()
											.getPropertiesMatrix();
									double simi = feedbackPropertyMatrix.get(
											mn.getSourceKey(),
											mn.getTargetKey()).getSimilarity();
									feedbackPropertyMatrix.setSimilarity(
											mn.getSourceKey(),
											mn.getTargetKey(), simi
													- tmp.get(i).getForth()
															.get(mn));
								}
							}

						}
						experiment.tupleList.set(index.get(i), tups);
					}
					propagationExecute(experiment, candidateMapping,
							userfeedback, log, cluster, e);
				}

			}

		}
		allDone = true;
		SimilarityMatrix feedbackClassMatrix = experiment.initialMatcher
				.getFinalMatcher().getClassesMatrix();
		SimilarityMatrix feedbackPropertyMatrix = experiment.initialMatcher
				.getFinalMatcher().getPropertiesMatrix();

		try {
			if (candidateMapping.getAlignmentType() == alignType.aligningClasses) {
				WriteMatrix(feedbackClassMatrix, "Classes",
						experiment.getIterationNumber());
			} else {
				WriteMatrix(feedbackPropertyMatrix, "Properties",
						experiment.getIterationNumber());
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if(allDone) {
			System.out.println("final size before: "
					+ experiment.getFinalAlignment().size());
			log.info("");

			Alignment<Mapping> finalAlignment = experiment.getFinalAlignment();

			log.info("Propagation is done.  Creating the new alignment.");

			experiment.initialMatcher.getFinalMatcher().select();

			finalAlignment = experiment.getFinalAlignment();
			System.out.println("final size after: "
					+ experiment.getFinalAlignment().size());

			log.info("");
		}

		
		done();

	}

	private void WriteMatrix(SimilarityMatrix sm, String type, int iteration)
			throws IOException {
		// TODO Auto-generated method stub
		File file = new File("C:/Users/xulin/Desktop/SimilarityMatrix" + type
				+ "/similarityMatrix_" + iteration + ".txt");
		// if file doesnt exists, then create it
		if (!file.exists())
			file.createNewFile();
		FileWriter fw = null;

		fw = new FileWriter(file.getAbsoluteFile());

		BufferedWriter bw = new BufferedWriter(fw);

		for (int i = -1; i < sm.getRows(); i++) {

			bw.write(i + "\t");
			for (int j = 0; j < sm.getColumns(); j++) {
				if (i == -1) {
					bw.write(j + "\t");
				} else {
					bw.write(sm.getSimilarity(i, j) + "\t");
				}

			}
			bw.write("\n");
		}

		bw.close();
	}

	private void propagationExecute(
			UFLExperiment experiment,
			Mapping candidateMapping,
			Validation userfeedback,
			UFLExperiment log,
			Tuple<Mapping, Cluster<Mapping>, Double, HashMap<Mapping, Double>> cluster,
			double e) {
		// TODO Auto-generated method stub
		SimilarityMatrix feedbackClassMatrix = experiment.initialMatcher
				.getFinalMatcher().getClassesMatrix();
		SimilarityMatrix feedbackPropertyMatrix = experiment.initialMatcher
				.getFinalMatcher().getPropertiesMatrix();

		if (candidateMapping.getAlignmentType() == alignType.aligningClasses) {
			Mapping m = feedbackClassMatrix.get(
					candidateMapping.getSourceKey(),
					candidateMapping.getTargetKey());
			if (m == null)
				m = new Mapping(candidateMapping);

			if (userfeedback == Validation.CORRECT) {
				m.setSimilarity(1.0d);
			} else if (userfeedback == Validation.INCORRECT) {
				m.setSimilarity(0.0d);
			}

			feedbackClassMatrix.set(candidateMapping.getSourceKey(),
					candidateMapping.getTargetKey(), m);
		} else if (candidateMapping.getAlignmentType() == alignType.aligningProperties) {
			Mapping m = feedbackPropertyMatrix.get(
					candidateMapping.getSourceKey(),
					candidateMapping.getTargetKey());
			if (m == null)
				m = new Mapping(candidateMapping);

			if (userfeedback == Validation.CORRECT) {
				m.setSimilarity(1.0d);
			} else if (userfeedback == Validation.INCORRECT) {
				m.setSimilarity(0.0d);
			}
			feedbackPropertyMatrix.set(candidateMapping.getSourceKey(),
					candidateMapping.getTargetKey(), m);
		}
		log.info("The cluster of the candidate mapping contains "
				+ cluster.getSecond().size() + " mappings.");
		if (userfeedback == Validation.CORRECT) {
			log.info("The user's validation is CORRECT. The mappings in the cluster will be REWARDED.");
		} else if (userfeedback == Validation.INCORRECT) {
			log.info("The user's validation is INCORRECT. The mappings in the cluster will be PENALIZED.");
		}
		// for every mapping in the cluster, penalize or reward the mappings
		// depending on the user's feedback
		int mappingNumber = 0;

		int correctlyPropagated = 0;
		int totalPropagated = 0;

		for (Mapping clusterMapping : cluster.getSecond()) {
			int i = clusterMapping.getSourceKey();
			int j = clusterMapping.getTargetKey();

			// do not propagate to excluded mappings (first if statement checks
			// assume 1-1 cardinality)
			if (experiment.correctMappings != null
					&& (experiment.correctMappings.contains(
							clusterMapping.getEntity1(), Ontology.SOURCE) != null || experiment.correctMappings
							.contains(clusterMapping.getEntity2(),
									Ontology.TARGET) != null))
				continue;
			if (experiment.incorrectMappings != null
					&& experiment.incorrectMappings.contains(i, j)) {
				continue;
			}

			if (userfeedback == Validation.CORRECT) {
				// reward
				if (candidateMapping.getAlignmentType() == alignType.aligningClasses) {
					Mapping m = feedbackClassMatrix.get(i, j);
					if (m == null) {
						m = new Mapping(clusterMapping);
					}
					double oldSimilarity = m.getSimilarity();
					double newSimilarity = (1.0d - e) * m.getSimilarity() + e;
					if (newSimilarity > 1.0d)
						newSimilarity = 1.0d;
					m.setSimilarity(newSimilarity);
					feedbackClassMatrix.set(i, j, m);
					cluster.getForth().put(clusterMapping,
							newSimilarity - oldSimilarity);

					String inRef = " (in reference = no) ";
					boolean binRef = false;
					if (experiment.getReferenceAlignment().contains(
							candidateMapping.getEntity1(),
							candidateMapping.getEntity2(),
							candidateMapping.getRelation())) {
						inRef = " (in reference = yes) ";
						binRef = true;
					}

					String inAlignment = " (in alignment = no) ";
					if (experiment.getFinalAlignment().contains(
							candidateMapping.getEntity1(),
							candidateMapping.getEntity2(),
							candidateMapping.getRelation())) {
						inAlignment = " (in alignment = yes) ";
					}

					// propagation quality
					if (binRef)
						correctlyPropagated++;
					totalPropagated++;

					log.info(mappingNumber + ". Rewarding " + m + inRef
							+ inAlignment + ".  Similarity updated from "
							+ oldSimilarity + " to " + newSimilarity + ".");
					mappingNumber++;
				} else if (candidateMapping.getAlignmentType() == alignType.aligningProperties) {
					Mapping m = feedbackPropertyMatrix.get(i, j);
					if (m == null) {
						m = new Mapping(clusterMapping);
					}
					double oldSimilarity = m.getSimilarity();
					double newSimilarity = (1.0d - e) * m.getSimilarity() + e;
					if (newSimilarity > 1.0d)
						newSimilarity = 1.0d;
					m.setSimilarity(newSimilarity);
					feedbackPropertyMatrix.set(i, j, m);
					cluster.getForth().put(clusterMapping,
							newSimilarity - oldSimilarity);

					String inRef = " (in reference = no) ";
					boolean binRef = false;
					if (experiment.getReferenceAlignment().contains(
							candidateMapping.getEntity1(),
							candidateMapping.getEntity2(),
							candidateMapping.getRelation())) {
						inRef = " (in reference = yes) ";
						binRef = true;
					}

					String inAlignment = " (in alignment = no) ";
					if (experiment.getFinalAlignment().contains(
							candidateMapping.getEntity1(),
							candidateMapping.getEntity2(),
							candidateMapping.getRelation())) {
						inAlignment = " (in alignment = yes) ";
					}

					if (binRef)
						correctlyPropagated++;
					totalPropagated++;

					log.info(mappingNumber + ". Rewarding " + m + inRef
							+ inAlignment + ".  Similarity updated from "
							+ oldSimilarity + " to " + newSimilarity + ".");
					mappingNumber++;
				}
			} else if (userfeedback == Validation.INCORRECT) {
				// penalize
				if (candidateMapping.getAlignmentType() == alignType.aligningClasses) {
					Mapping m = feedbackClassMatrix.get(i, j);
					if (m == null) {
						m = new Mapping(clusterMapping);
					}
					double oldSimilarity = m.getSimilarity();
					double newSimilarity = (1.0d - e) * m.getSimilarity();
					m.setSimilarity(newSimilarity);
					feedbackClassMatrix.set(i, j, m);
					cluster.getForth().put(clusterMapping,
							newSimilarity - oldSimilarity);

					String inRef = " (in reference = no) ";
					boolean binRef = false;
					if (experiment.getReferenceAlignment().contains(
							candidateMapping.getEntity1(),
							candidateMapping.getEntity2(),
							candidateMapping.getRelation())) {
						inRef = " (in reference = yes) ";
						binRef = true;
					}

					String inAlignment = " (in alignment = no) ";
					if (experiment.getFinalAlignment().contains(
							candidateMapping.getEntity1(),
							candidateMapping.getEntity2(),
							candidateMapping.getRelation())) {
						inAlignment = " (in alignment = yes) ";
					}

					if (!binRef)
						correctlyPropagated++;
					totalPropagated++;

					log.info(mappingNumber + ". Penalizing " + m + inRef
							+ inAlignment + ".  Similarity updated from "
							+ oldSimilarity + " to " + newSimilarity + ".");
					mappingNumber++;
				} else if (candidateMapping.getAlignmentType() == alignType.aligningProperties) {
					Mapping m = feedbackPropertyMatrix.get(i, j);
					if (m == null) {
						m = new Mapping(clusterMapping);
					}
					double oldSimilarity = m.getSimilarity();
					double newSimilarity = (1.0d - e) * m.getSimilarity();
					m.setSimilarity(newSimilarity);
					feedbackPropertyMatrix.set(i, j, m);
					cluster.getForth().put(clusterMapping,
							newSimilarity - oldSimilarity);

					String inRef = " (in reference = no) ";
					boolean binRef = false;
					if (experiment.getReferenceAlignment().contains(
							candidateMapping.getEntity1(),
							candidateMapping.getEntity2(),
							candidateMapping.getRelation())) {
						inRef = " (in reference = yes) ";
						binRef = true;
					}

					String inAlignment = " (in alignment = no) ";
					if (experiment.getFinalAlignment().contains(
							candidateMapping.getEntity1(),
							candidateMapping.getEntity2(),
							candidateMapping.getRelation())) {
						inAlignment = " (in alignment = yes) ";
					}

					if (!binRef)
						correctlyPropagated++;
					totalPropagated++;

					log.info(mappingNumber + ". Penalizing " + m + inRef
							+ inAlignment + ".  Similarity updated from "
							+ oldSimilarity + " to " + newSimilarity + ".");
					mappingNumber++;
				}
			}
		}
		experiment.tupleList.add(cluster);
	}

}
