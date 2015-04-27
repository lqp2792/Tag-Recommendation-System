package phu.quang.le.Utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import phu.quang.le.Controller.PanelController;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;

public class ThreadWorker implements Runnable {
	ArrayList<TreeSet<IDSorter>> topicSortedWords = null;
	Alphabet dataAlphabet = null;
	int from = 0;
	int to = 0;

	public ThreadWorker(ArrayList<TreeSet<IDSorter>> topicSortedWords,
			Alphabet dataAlphabet, int from, int to) {
		this.topicSortedWords = topicSortedWords;
		this.dataAlphabet = dataAlphabet;
		this.from = from;
		this.to = to;
	}

	@Override
	public void run() {
		System.out.println("FROM: " + from + " - TO: " + to);
		for (int i = from; i < to; i++) {
			Iterator<IDSorter> iterator = topicSortedWords.get(i).iterator();
			int rank = 0;
			while (iterator.hasNext() && rank < 5) {
				IDSorter idCountPair = iterator.next();
				String word = (String) dataAlphabet.lookupObject(idCountPair
						.getID());
				if (word.matches("[A-Za-z]+")) {
					PanelController.availableTags.add(word);
					rank++;
				}
			}
		}
	}
}
