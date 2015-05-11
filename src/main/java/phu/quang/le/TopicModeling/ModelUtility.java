package phu.quang.le.TopicModeling;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import phu.quang.le.Model.RecommendTag;
import cc.mallet.pipe.Pipe;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;

public class ModelUtility {
	public static ParallelTopicModel model = null;
	public static final Object syncObject = new Object();
	public static boolean isLoaded = false;

	public static Pipe getPipe() throws URISyntaxException {
		InstanceList instances = InstanceList.load(new File(ModelUtility.class
				.getClassLoader().getResource("Instance.lda").toURI()));
		return instances.getPipe();
	}

	public static ParallelTopicModel getTopicModel() throws URISyntaxException,
			FileNotFoundException, IOException, ClassNotFoundException {
		if (model != null) {
			return model;
		} else {
			File file = new File(ModelUtility.class.getClassLoader()
					.getResource("TopicModel.lda").toURI());
			ObjectInputStream objectInput = new ObjectInputStream(
					new FileInputStream(file));
			model = (ParallelTopicModel) objectInput.readObject();
			objectInput.close();
			System.out.println("Finish loading Topic Model");
			return model;
		}
	}

	public static List<RecommendTag> getTopWords(int topicID,
			ParallelTopicModel model, InstanceList instances) {
		List<RecommendTag> topWords = new ArrayList<RecommendTag>();
		Alphabet dataAlphabet = instances.getDataAlphabet();
		ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
		Iterator<IDSorter> iterator = topicSortedWords.get(topicID).iterator();
		int rank = 0;
		while (iterator.hasNext() && rank < 5) {
			RecommendTag tag = new RecommendTag();
			IDSorter idCountPair = iterator.next();
			tag.setContent((String) dataAlphabet.lookupObject(idCountPair
					.getID()));
			topWords.add(tag);
			rank++;
		}
		//
		return topWords;
	}

}
