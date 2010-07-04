package uk.co.seesaw.android.whatsonseesaw;

public class SearchResult {

	public final String title;
	public final String url;
	
	public SearchResult(String title, String url) {
		this.title = title;
		this.url = url;
	}
	
	@Override
	public String toString() {
		return title;
	}
}
