package phu.quang.le.TopicModeling;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicAssignment;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.LabelAlphabet;

public class ModelUtility {

	public static ParallelTopicModel getTopicModel () throws URISyntaxException,
			FileNotFoundException, IOException, ClassNotFoundException {
		ParallelTopicModel model = null;
		File file = new File (ModelUtility.class.getClassLoader ().
				getResource ("TopicModel.lda").toURI ());
		ObjectInputStream objectInput = new ObjectInputStream (new FileInputStream (file));
		model = (ParallelTopicModel) objectInput.readObject ();
		objectInput.close ();
		//
		return model;
	}

	public static TopicInferencer getTopicInferencer () throws FileNotFoundException,
			IOException, ClassNotFoundException, URISyntaxException {
		TopicInferencer inferencer = null;
		File file = new File (ModelUtility.class.getClassLoader ().
				getResource ("TopicInferencer.lda").toURI ());
		ObjectInputStream objectInput = new ObjectInputStream (new FileInputStream (file));
		inferencer = (TopicInferencer) objectInput.readObject ();
		objectInput.close ();
		//
		return inferencer;
	}

	public static SerialPipes createPipes () throws IOException,
			URISyntaxException {
		ArrayList<Pipe> pipeList = new ArrayList<Pipe> ();
		pipeList.add (new CharSequenceLowercase ());
		pipeList.add (new CharSequence2TokenSequence (Pattern
				.compile ("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
		pipeList.add (new TokenSequenceRemoveStopwords (new File (ModelUtility.class
				.getClassLoader ().getResource ("stoplist/en.txt").toURI ()),
				"UTF-8", false, false, false));
		pipeList.add (new TokenSequence2FeatureSequence ());
		//
		return new SerialPipes (pipeList);
	}

	public static void main (String[] args) throws FileNotFoundException,
			ClassNotFoundException, IOException, URISyntaxException {
		ParallelTopicModel model = getTopicModel ();
		List<TopicAssignment> data = model.getData ();
		System.out.println (model.getAlphabet ().size ());
	}
}
