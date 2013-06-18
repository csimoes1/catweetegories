package org.simoes.classify;

import java.io.File;

import org.simoes.util.FileUtil;

/**
 * Helper class that allows us to load up the Classifier at launch time instead of each time an 
 * Action is called.
 * @author csimoes
 *
 */
public class Classifier {
	
	private static TweetClassifier tc;

	public static void init() {
		File trainingPropsFile = FileUtil.getFile("training.properties");
		File classifierFile = FileUtil.getFile("tweet.classifier");
		//String trainingPropsFile = "/Users/csimoes/workspace-juno/twitter-feed/conf/training.properties";
		//String classifierFile = "/Users/csimoes/workspace-juno/twitter-feed/tweet.classifier";
		tc = new TweetClassifier(trainingPropsFile, classifierFile);
	}
	
	public static TweetClassifier getInstance() {
		return tc;
	}

	
}
