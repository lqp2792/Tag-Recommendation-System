package phu.quang.le.TopicModeling;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import phu.quang.le.Controller.PanelController;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.InstanceList;

public class ModelAvailableTagsPrepareThread extends Thread {
	@Override
	public void run() {
		try {
			System.out.println("Prepare Available Tags!");
			InstanceList instances = InstanceList.load(new File(
					ModelUtility.class.getClassLoader()
							.getResource("Instance.lda").toURI()));
			Alphabet dataAlphabet = instances.getDataAlphabet();
			ArrayList<TreeSet<IDSorter>> topicSortedWords = ModelUtility.model
					.getSortedWords();
			for (int i = 0; i < ModelUtility.model.getNumTopics(); i++) {
				Iterator<IDSorter> iterator = topicSortedWords.get(i)
						.iterator();
				int rank = 0;
				while (iterator.hasNext() && rank < 5) {
					IDSorter idCountPair = iterator.next();
					String word = (String) dataAlphabet
							.lookupObject(idCountPair.getID());
					if (word.matches("[A-Za-z]+")) {
						if (!PanelController.availableTags.contains(word)) {
							PanelController.availableTags.add(word);
							rank++;
						}
					}
				}
			}
			System.out.println("Total Available Tags: "
					+ PanelController.availableTags.size());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

	}
}
