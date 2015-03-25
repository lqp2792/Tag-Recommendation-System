package phu.quang.le.Utility;

import java.io.IOException;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class UrlUtility {

	public static String getUrlKeywords (String url) throws IOException {
		String keywords = null;
		Document doc = Jsoup.connect (url).get ();
		Elements meta = doc.select ("meta[name=keywords]");
		if (!meta.isEmpty ()) {
			for (int i = 0; i < meta.size (); i++) {
				keywords = meta.get (i).attr ("content");
				System.out.println ("Meta keyword : " + keywords);
				StringTokenizer tokens = new StringTokenizer (keywords, ",");
				keywords = tokens.nextToken ().trim () + " ";
				while (tokens.hasMoreTokens ()) {
					keywords += tokens.nextToken ().trim () + " ";
				}
			}
		}
		//
		return keywords;
	}

	public static String getUrlDesciption (String url) throws IOException {
		String description = null;
		Document doc = Jsoup.connect (url).get ();
		Elements meta = doc.select ("meta[name=description]");
		if (!meta.isEmpty ()) {
			description = meta.get (0).attr ("content");
		}
		//
		return description;
	}
}
