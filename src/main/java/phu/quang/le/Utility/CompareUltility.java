package phu.quang.le.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import phu.quang.le.Model.AdvanceBookmark;

public class CompareUltility {
	public List<AdvanceBookmark> sortedBookmarks = new ArrayList<AdvanceBookmark>();
	public int sortBy;
	public final static double BASE_POINT = 1000;
	public final double firstPriority = 0.35;
	public final double secondPriority = 0.3;
	public final double thirdPriority = 0.2;
	public final double fourthPriority = 0.1;
	public final double fifthPriority = 0.05;
	public double sameTagPoint;
	public double timePoint;
	public double ratingPoint;
	public double viewPoint;
	public double copyPoint;
	public int defaultSameTags;
	public final int defaultRating = 5;
	public final int defaultViewTimes = 10;
	public final int defaultRatedTimes = 10;
	public final int defaultCopyTimes = 5;
	public Date currentDate = new Date();

	public CompareUltility(int sortBy, int userID) {
		this.defaultSameTags = UserSQL.getMostUsedTags(userID).size();
		this.sortBy = sortBy;
		switch (sortBy) {
		case 1:
			ratingPoint = firstPriority * BASE_POINT;
			sameTagPoint = secondPriority * BASE_POINT;
			viewPoint = thirdPriority * BASE_POINT;
			copyPoint = fourthPriority * BASE_POINT;
			timePoint = fifthPriority * BASE_POINT;
			break;
		case 2:
			viewPoint = firstPriority * BASE_POINT;
			sameTagPoint = secondPriority * BASE_POINT;
			ratingPoint = thirdPriority * BASE_POINT;
			copyPoint = fourthPriority * BASE_POINT;
			timePoint = fifthPriority * BASE_POINT;
			break;
		case 3:
			copyPoint = firstPriority * BASE_POINT;
			sameTagPoint = secondPriority * BASE_POINT;
			ratingPoint = thirdPriority * BASE_POINT;
			viewPoint = fourthPriority * BASE_POINT;
			timePoint = fifthPriority * BASE_POINT;
			break;
		case 4:
			timePoint = firstPriority * BASE_POINT;
			sameTagPoint = secondPriority * BASE_POINT;
			ratingPoint = thirdPriority * BASE_POINT;
			viewPoint = fourthPriority * BASE_POINT;
			copyPoint = fifthPriority * BASE_POINT;
			break;
		case 5:
			sameTagPoint = firstPriority * BASE_POINT;
			ratingPoint = secondPriority * BASE_POINT;
			viewPoint = thirdPriority * BASE_POINT;
			copyPoint = fourthPriority * BASE_POINT;
			timePoint = fifthPriority * BASE_POINT;
			break;
		}
		if (defaultSameTags != 0) {
			sameTagPoint = sameTagPoint / defaultSameTags;
		}
		viewPoint = viewPoint / 10;
		copyPoint = copyPoint / 5;
		timePoint = timePoint / 100;
	}

	public class BookmarkComparator implements Comparator<AdvanceBookmark> {
		@Override
		public int compare(AdvanceBookmark a1, AdvanceBookmark a2) {
			return (int) (a2.getPoint() - a1.getPoint());
		}

	}

	public double calculatePoint(AdvanceBookmark item) {
		double sameTagsDiff = 0;
		if (defaultSameTags == 0) {
			sameTagsDiff = sameTagPoint;
		} else {
			sameTagsDiff = ((defaultSameTags - item.getSameTags().size()))
					* sameTagPoint;
		}
		if (defaultRatedTimes == item.getRatedTimes()) {
			ratingPoint = ratingPoint / 8;
		} else {
			ratingPoint = ratingPoint
					/ (8 * (defaultRatedTimes - item.getRatedTimes()));
		}
		double ratingDiff = ((defaultRating * defaultRatedTimes - item
				.getTotalRating() * item.getRatedTimes()))
				* ratingPoint;
		double viewDiff = ((defaultViewTimes - item.getViewTimes()))
				* viewPoint;
		double copyDiff = ((defaultCopyTimes - item.getCopyTimes()))
				* copyPoint;
		double timeDiff = 0;
		long diff = currentDate.getTime() - item.getDate().getTime();
		int betweenDays = (int) TimeUnit.DAYS.convert(diff,
				TimeUnit.MILLISECONDS);
		if (currentDate.after(item.getDate())) {
			timeDiff = betweenDays * timePoint;
		}
		if (currentDate.before(item.getDate())) {
			timeDiff = -(betweenDays * timePoint);
		}
		System.out.println(sameTagsDiff + " " + ratingDiff + " " + viewDiff
				+ " " + copyDiff + " " + timeDiff);
		item.setPoint(BASE_POINT - sameTagsDiff - ratingDiff - viewDiff
				- copyDiff - timeDiff);
		sortedBookmarks.add(item);
		return item.getPoint();
	}

	public void sort() {
		Collections.sort(sortedBookmarks, new BookmarkComparator());
	}

	@Override
	public String toString() {
		for (int i = 0; i < sortedBookmarks.size(); i++) {
			System.out.println(sortedBookmarks.get(i).getPoint());
		}
		return null;
	}
}
