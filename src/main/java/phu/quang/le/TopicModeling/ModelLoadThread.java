package phu.quang.le.TopicModeling;

import java.io.IOException;
import java.net.URISyntaxException;

public class ModelLoadThread extends Thread {
	@Override
	public void run() {
		synchronized (ModelUtility.syncObject) {
			try {
				if (ModelUtility.model == null) {
					System.out.println("Prepare Topic Model");
					ModelUtility.model = ModelUtility.getTopicModel();
				}
			} catch (ClassNotFoundException | URISyntaxException | IOException e) {
				System.err.println("Get Topic Model exception : " + e);
			}
			ModelUtility.syncObject.notify();
		}
	}
}
