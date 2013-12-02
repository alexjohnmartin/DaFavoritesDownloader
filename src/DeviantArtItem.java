public class DeviantArtItem {
	public DeviantArtItem(String itemXml) {
		rawXml = itemXml; 
	}

	public String getCategory() { 
		String catTag = "<media:category label=\""; 
		if (!rawXml.contains(catTag)) return "unknown category";
		return getTextBetween(rawXml, catTag, "\"");
	}
	
	public String getFullCategory() { 
		String catTag = "<media:category"; 
		if (!rawXml.contains(catTag)) return "unknown category";
		String catXml = getTextBetween(rawXml, catTag, "/media:category>");
		return getTextBetween(catXml, "\">", "<");
	}
	
	public String getAuthor() { 
		String mediaTag = "<media:credit role=\"author\" scheme=\"urn:ebu\">"; 
		if (!rawXml.contains(mediaTag)) return "unknown author";
		return getTextBetween(rawXml, mediaTag, "</media:credit>");
	}
	
	public String getDownloadUrl() {
		String xml = rawXml; 
		String url = "unknown"; 
		
		while (xml.contains("<media:content")) { 
			String mediaXml = getTextBetween(xml, "<media:content", "/>"); 
			url = getTextBetween(mediaXml, "url=\"", "\""); 
			
			xml = xml.substring(xml.indexOf(mediaXml) + mediaXml.length()); 
		}
		
		return url; 
	}
	
	public String getFilename() {
		if (!rawXml.contains("<media:thumbnail")) return "unknown"; 
		
		String mediaXml = rawXml.substring(rawXml.indexOf("<media:thumbnail")); 
		String url = getTextBetween(mediaXml, "url=\"", "\""); 
		return url.substring(url.lastIndexOf("/") + 1); 
	}
	
	public String getTitle() {
		if (!rawXml.contains("<title>")) return "unknown title"; 		
		return getTextBetween(rawXml, "<title>", "</title>"); 
	}
	
	public String getLink() {
		if (!rawXml.contains("<link>")) return "unknown link"; 		
		return getTextBetween(rawXml, "<link>", "</link>"); 
	}
	
	public String getMediaTitle() {
		if (!rawXml.contains("<media:title type=\"plain\">")) return "unknown media title";	
		return getTextBetween(rawXml, "<media:title type=\"plain\">", "</media:title>"); 
	}
	
	private String getTextBetween(String mainText, String startText, String endText) {
		int indexOfStart = mainText.indexOf(startText) + startText.length(); 
		int indexOfEnd = mainText.indexOf(endText, indexOfStart); 
		return mainText.substring(indexOfStart, indexOfEnd); 
	}
	
	private String rawXml; 
}
