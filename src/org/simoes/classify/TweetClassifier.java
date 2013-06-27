package org.simoes.classify;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.google.common.base.Ascii;

import edu.stanford.nlp.classify.ColumnDataClassifier;
import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.util.ErasureUtils;

/**
 * Takes the tweet string passed in and returns the Enum that represents the
 * category this tweet is in
 * 
 * @author csimoes
 * 
 */
public class TweetClassifier {
	static Logger log = Logger.getLogger(TweetClassifier.class.getName());
	private LinearClassifier<String, String> classifier;
	private ColumnDataClassifier cdc;

	public TweetClassifier(File trainingPropsFile, File classifierFile) {
		try (FileInputStream fis = new FileInputStream(classifierFile)) {
			ObjectInputStream ois = new ObjectInputStream(fis);
			classifier = ErasureUtils.uncheckedCast(ois.readObject());
			ois.close();
			
			cdc = new ColumnDataClassifier(trainingPropsFile.getCanonicalPath());
		} catch(IOException e) {
			log.severe(e.getMessage());
		} catch(ClassNotFoundException e) {
			log.severe(e.getMessage());
		}
	}
	
	/**
	 * Classifies the Tweet status into one of our Category Enums and returns that Category
	 * @param status
	 * @return Category Enum this tweet most strongly belongs too
	 */
	public Category classifyText(String text) {
		text = "?\t" + text;
		Datum datum = cdc.makeDatumFromLine(text, 0);
		String classChosen = (String)getClassifier().classOf(datum);
		Category result = Category.valueOf(Ascii.toUpperCase(classChosen));
		if(null == result) {
			log.warning("classifyText(): Category returned was null! text=" + text);
		}
		return result;
	}

	public ClassifierStats getStatistics(String text) {
		ClassifierStats result = new ClassifierStats();
		
		text = "?\t" + text;
		Datum datum = cdc.makeDatumFromLine(text, 0);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(baos);
		getClassifier().justificationOf(datum, pw);
		pw.flush();
		String justificationOf = baos.toString();
		
		TreeMap<Category, Double> categoryScores = new TreeMap<Category, Double>();
		Counter counter = getClassifier().scoresOf(datum);
		Set<String> keys = counter.keySet();
		for (String c : keys) {
			Category cat = Category.getCategoryByName(c);
			double score = counter.getCount(c);
			categoryScores.put(cat, score);
		}

		result.setScoringTable(justificationOf);
		result.setCategoryScores(categoryScores);
		
		return result;
	}
	
	protected LinearClassifier getClassifier() {
		return classifier;
	}
	
	public static void main(String[] args) {
		ColumnDataClassifier cdc = new ColumnDataClassifier("/Users/csimoes/workspace-juno/catweetegories/conf/training.properties");
		
		File trainFile = new File("/Users/csimoes/workspace-juno/catweetegories/conf/training.properties");
		File classifyFile = new File("tweet.classifier");
		TweetClassifier tc = new TweetClassifier(trainFile, classifyFile);
		Collection labels = tc.getClassifier().labels();
		for (Iterator iterator = labels.iterator(); iterator.hasNext();) {
			Object o = iterator.next();
			System.out.println("Object=" + o.toString());
			
		}
		String text = "The 4 remaining venues for #USMNT home qualifiers will be revealed at 12:30 p.m. ET live on @SportsCenter. @J_Klinsmann will on to discuss.";
		String text2 = "Happy 21st Birthday to #USWNT defender Julie Johnston! She celebrates in two countries: Germany and? http://t.co/QoE3upnm93";
		String text3 = "Some pics from #USWNT physical testing yesterday. With that over, it's all soccer the rest of the week: http://t.co/JiKuQsUV";
		
		Datum datum = cdc.makeDatumFromLine("?\t" + text3, 0);
		Object classChosen = tc.getClassifier().classOf(datum);
		System.out.println("classChosen=" + classChosen);
		Counter counter = tc.getClassifier().scoresOf(datum);
		System.out.println("counter=" + counter.toString());

		Category c = tc.classifyText(text3);
		System.out.println("category=" + c);
		
		ClassifierStats cs = tc.getStatistics(text3);
		System.out.println("scoreTable=" + cs.getScoringTable());
		System.out.println("scoreTree=" + cs.getCategoryScores());
		
		
//		ColumnDataClassifier cdc = new ColumnDataClassifier("tweet.classifier");
//		Classifier<String, String> cl = cdc.makeClassifier(cdc
//				.readTrainingExamples("examples/cheeseDisease.train"));
//		for (String line : ObjectBank
//				.getLineIterator("examples/cheeseDisease.test")) {
//			Datum<String, String> d = cdc.makeDatumFromLine(line, 0);
//			System.out.println(line + "  ==>  " + cl.classOf(d));
//		}
	}

/*	
	public static void demonstrateSerialization(String[] args)
			throws IOException, ClassNotFoundException {
		ColumnDataClassifier cdc = new ColumnDataClassifier(
				"examples/cheese2007.prop");
		Classifier<String, String> cl = cdc.makeClassifier(cdc
				.readTrainingExamples("examples/cheeseDisease.train"));

		// Exhibit serialization and deserialization working. Serialized to
		// bytes in memory for simplicity
		System.out.println();
		System.out.println();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(cl);
		oos.close();
		byte[] object = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(object);
		ObjectInputStream ois = new ObjectInputStream(bais);
		LinearClassifier<String, String> lc = ErasureUtils.uncheckedCast(ois
				.readObject());
		ois.close();
		ColumnDataClassifier cdc2 = new ColumnDataClassifier(
				"examples/cheese2007.prop");

		for (String line : ObjectBank
				.getLineIterator("examples/cheeseDisease.test")) {
			Datum<String, String> d = cdc.makeDatumFromLine(line, 0);
			Datum<String, String> d2 = cdc2.makeDatumFromLine(line, 0);
			System.out.println(line + "  =origi=>  " + cl.classOf(d));
			System.out.println(line + "  =deser=>  " + lc.classOf(d2));
		}
	}
*/	
}