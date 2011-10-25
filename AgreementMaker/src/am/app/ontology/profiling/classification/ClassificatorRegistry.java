package am.app.ontology.profiling.classification;

import weka.classifiers.*;
import weka.classifiers.bayes.*;
import weka.classifiers.functions.*;

import weka.classifiers.lazy.*;
import weka.classifiers.meta.*;
import weka.classifiers.mi.*;
import weka.classifiers.misc.*;
import weka.classifiers.pmml.consumer.*;
import weka.classifiers.rules.*;
import weka.classifiers.trees.*;
import weka.classifiers.trees.lmt.LogisticBase;
import weka.classifiers.trees.m5.*;

public enum ClassificatorRegistry {
    
//	C_ADTree										( ADTree.class ), 
//    C_AODE 											( AODE.class ), 
//    C_AODEsr 										( AODEsr.class ), 
//    C_BayesianLogisticRegression 					( BayesianLogisticRegression.class ),
    C_BayesNet										( BayesNet.class ), 
//    C_CitationKNN									( CitationKNN.class ), 
    C_ClassificationViaClustering					( ClassificationViaClustering.class ), 
    C_ComplementNaiveBayes							( ComplementNaiveBayes.class ), 
    C_ConjunctiveRule								( ConjunctiveRule.class ), 
    C_DecisionStump									( DecisionStump.class ), 
    C_DecisionTable									( DecisionTable.class ), 
    C_DMNBtext										( DMNBtext.class ), 
    C_FT											( FT.class ), 
//    C_GaussianProcesses								( GaussianProcesses.class ), 
//    C_HNB											( HNB.class ), 
    C_HyperPipes									( HyperPipes.class ), 
    C_IB1											( IB1.class ), 
    C_IBk											( IBk.class ), 
//    C_Id3											( Id3.class ), 
//    C_IsotonicRegression							( IsotonicRegression.class ), 
    C_J48											( J48.class ), 
    C_J48graft										( J48graft.class ), 
    C_JRip											( JRip.class ), 
    C_KStar											( KStar.class ), 
    C_LADTree										( LADTree.class ), 
//    C_LBR											( LBR.class ), 
//    C_LeastMedSq									( LeastMedSq.class ), 
//    C_LibLINEAR										( LibLINEAR.class ), 
//    C_LibSVM										( LibSVM.class ), 
//    C_LinearRegression								( LinearRegression.class ), 
    C_LMT											( LMT.class ), 
//    C_Logistic										( Logistic.class ), 
//    C_LogisticBase									( LogisticBase.class ), 
//    C_M5Base										( M5Base.class ), 
//    C_MDD											( MDD.class ), 
//    C_MIDD											( MIDD.class ), 
//    C_MILR											( MILR.class ),	 
//    C_MINND											( MINND.class ), 
//    C_MIOptimalBall									( MIOptimalBall.class ), 
//    C_MISMO											( MISMO.class ), 
//    C_MISVM											( MISVM.class ), 
//    C_MultilayerPerceptron							( MultilayerPerceptron.class ), 
//    C_MultipleClassifiersCombiner					( MultipleClassifiersCombiner.class ), 
    C_NaiveBayes									( NaiveBayes.class ), 
    C_NaiveBayesMultinomial							( NaiveBayesMultinomial.class ), 
    C_NaiveBayesSimple								( NaiveBayesSimple.class ), 
//    C_NBTree										( NBTree.class ), 
//    C_NNge											( NNge.class ), 
//    C_OneR											( OneR.class ), 
//    C_PaceRegression								( PaceRegression.class ), 
//    C_PART											( PART.class ), 
//    C_PLSClassifier									( PLSClassifier.class ), 
//    C_PMMLClassifier								( PMMLClassifier.class ), 
//    C_PreConstructedLinearModel						( PreConstructedLinearModel.class ),	 
//    C_Prism											( Prism.class ), 
//    C_RandomForest									( RandomForest.class ), 
//    C_RandomizableClassifier						( RandomizableClassifier.class ), 
//    C_RandomTree									( RandomTree.class ), 
    C_RBFNetwork									( RBFNetwork.class ), 
    C_REPTree										( REPTree.class ), 
    C_Ridor											( Ridor.class );
//    C_RuleNode										( RuleNode.class ), 
//    C_SerializedClassifier							( SerializedClassifier.class ); 
//   C_SimpleLinearRegression						( SimpleLinearRegression.class ), 
//    C_SimpleLogistic								( SimpleLogistic.class ), 
//    C_SingleClassifierEnhancer						( SingleClassifierEnhancer.class ), 
//    C_SMO											( SMO.class ), 
//    C_SMOreg										( SMOreg.class ); 
//    C_SPegasos										( SPegasos.class ), 
//    C_UserClassifier								( UserClassifier.class ), 
//    C_VFI											( VFI.class ), 
//    C_VotedPerceptron								( VotedPerceptron.class ),		 
//    C_Winnow										( Winnow.class ), 
//    C_ZeroR											( ZeroR.class ),
//	C_WAODE											( WAODE.class ); 
    
    
	private Class<? extends Classifier> cls;
	
	private ClassificatorRegistry( Class<? extends Classifier> c ) {
		cls = c;
	}
    
	public Class<? extends Classifier> getClassifier() { return cls; }
    
	

}
