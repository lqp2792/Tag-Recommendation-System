package phu.quang.le.TopicModeling;

import java.io.IOException;
import java.net.URISyntaxException;

import phu.quang.le.Controller.PanelController;

public class ModelLoadThread extends Thread {
	@Override
	public void run() {
		try {
			if (ModelUtility.model == null) {
				System.out.println("Prepare Topic Model");
				ModelUtility.model = ModelUtility.getTopicModel();
				ModelUtility.isLoaded = true;
			}
		} catch (ClassNotFoundException | URISyntaxException | IOException e) {
			System.err.println("Get Topic Model exception : " + e);
		} finally {
			if(PanelController.availableTags.size() == 0) {
				(new ModelAvailableTagsPrepareThread()).start();
			}
		}
	}
}
