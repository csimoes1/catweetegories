package org.simoes.collect;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.simoes.util.FileUtil;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class LuceneTextUtils {
	
	static Logger log = Logger.getLogger(LuceneTextUtils.class.getName());
	private static LuceneTextUtils INSTANCE = new LuceneTextUtils();
	private CharArraySet stopWords;
	
	public static LuceneTextUtils getInstance() {
		return INSTANCE;
	}

	private LuceneTextUtils() {
		try {
			// we have validated that this txt File from WEKA is far superior to Lucene's default STOP_WORD_SET
			File stopWordsFile = FileUtil.getFile("stopwords1.txt");
			List<String> words = Files.readLines(stopWordsFile, Charsets.UTF_8);
			stopWords = StopFilter.makeStopSet(Version.LUCENE_43, words, true);
		} catch (IOException e) {
			log.severe(e.getMessage());
		}
	}
	
	public String cleanText(String a) {
		String result = a;
		try {
			result = removeStopWords(a);
		} catch (IOException e) {
			log.warning("cleanText failed, returning original string: " + a);
			log.warning(e.getMessage());
		}
		return result;
	}
	
	
	private String removeStopWords(String input) throws IOException {
		StringBuilder result = new StringBuilder();
	    StringReader readerInput = new StringReader(input);
		StandardTokenizer st = new StandardTokenizer(Version.LUCENE_43, readerInput);
	    TokenStream stream = new StopFilter(Version.LUCENE_43, st, stopWords);
	    CharTermAttribute termAtt = null;
		termAtt = stream.getAttribute(CharTermAttribute.class);
		
		stream.reset();
		while(stream.incrementToken()) {
			result.append(termAtt.toString());
			result.append(" ");
		}
		return result.toString().trim();
	}

	public static void main(String args[]) {
		String test1 = "A lady walked to the store.";

		LuceneTextUtils ltu = LuceneTextUtils.getInstance();
		String result = ltu.cleanText(test1);
		System.out.println("Result=" + result);
	}
		
}
