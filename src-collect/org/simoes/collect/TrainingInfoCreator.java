package org.simoes.collect;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.simoes.common.StatusPlus;
import org.simoes.util.ConfigResources;
import org.simoes.util.DateUtil;
import org.simoes.util.MongoDbUtil;

import twitter4j.TwitterException;

import com.google.common.base.CharMatcher;

/**
 * This writes out a train and test file format for creating our classifier
 * 
 * @author csimoes
 *
 */
public class TrainingInfoCreator implements Runnable {
	static Logger log = Logger.getLogger(TrainingInfoCreator.class.getName());

	private static String fileNamePrefix = "trainingData-random-";
	private String fileNameTest;
	private String fileNameTrain;
	
	public TrainingInfoCreator() {
		super();
		String dateStr = DateUtil.createDateOnlyString(Calendar.getInstance().getTime());
		this.fileNameTest = fileNamePrefix + dateStr + "-test.txt";
		this.fileNameTrain = fileNamePrefix + dateStr + "-train.txt";
	}
	
	public void run() {
		createDataFromCollect();
	}

	private void createDataFromCollect() {
		// load all training Info objects into memory
		try {
			List<StatusPlus> statuses = new ArrayList<StatusPlus>(MongoDbUtil.getInstance().loadAllStatusPluses(MongoDbUtil.COLL_COLLECT, 0));
			// lets shuffle the List to ensure randomness
			Collections.shuffle(statuses);
			
			// write first ~20% to test and remaining ~80% to training File
			// we assume that since the Statuses are sorted by _id, that they the categories are reasonably well distributed
			
			int total = statuses.size();
			int testSize = total / 5;
			int trainSize = total - testSize;
			log.info("Writing out records, total count=" + total);
			log.info("Test size=" + testSize + ", trainSize=" + trainSize);
			
			List<StatusPlus> testSet = new ArrayList<StatusPlus>(statuses.subList(0, testSize));
			List<StatusPlus> trainSet = new ArrayList<StatusPlus>(statuses.subList(testSize, total));
			
			log.info("Adding Lift records to training set");
			statuses = new ArrayList<StatusPlus>(MongoDbUtil.getInstance().loadAllStatusPluses(MongoDbUtil.COLL_LIFT, 0));
			log.info("Lift records size=" + statuses.size());
			trainSet.addAll(statuses);
			
			// write them out to file
			File outputFileTest = new File(fileNameTest);
			File outputFileTrain = new File(fileNameTrain);
			
			writeListToFile(testSet, outputFileTest);
			writeListToFile(trainSet, outputFileTrain);
		} catch(TwitterException e) {
			log.severe(e.getErrorMessage());
			log.severe("Failed to write training info, probably Twitter4J is not configured correctly.");
			throw new IllegalStateException(e);
		}
	}
	
	private void writeListToFile(List<StatusPlus> list, File outputFile) {
		try(FileWriter fw = new FileWriter(outputFile)) {
			for (StatusPlus sp : list) {
				String trainText = sp.getClassifyText();
				if(StringUtils.isNotBlank(trainText)) {
					StringBuffer result = new StringBuffer();
					result.append(sp.getCategory().getName());
					result.append("\t");
					result.append(trainText);
					result.append("\t");
					result.append(IOUtils.LINE_SEPARATOR);
					fw.write(result.toString());
				}
			}
		} catch(IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	
	/*
	 * Create a training data file with 2 columns:
	 *  | column 1 | column 2  |
	 *  | category | tweetText |
	 */
	public static void main(String args[]) {
		ConfigResources.init();
		TrainingInfoCreator tic = new TrainingInfoCreator();
		tic.run();
		
	}
}
