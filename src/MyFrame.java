import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.channels.*;

import javax.swing.JFrame;

public class MyFrame extends JFrame implements ActionListener {
	Button downloadButton; 
	Label statusLabel; 
	TextField downloadPathText; 
	TextField usernameText; 
	TextField maxDownloadsText;
    TextField offsetText;
    TextField pauseText;

	
	public MyFrame() {
		this.getContentPane().setLayout(null); 
		
		// Window Listeners
		addWindowListener(new WindowAdapter() {
		  	public void windowClosing(WindowEvent e) {
			   System.exit(0);
		  	} //windowClosing
		} );

		Label pathLabel = new Label(); 
		pathLabel.setText("download path"); 
		pathLabel.setBounds(20, 10, 360, 20);
		this.getContentPane().add(pathLabel); 
		downloadPathText = new TextField(); 
		downloadPathText.setBounds(20, 30, 360, 20);
		downloadPathText.setText("/Users/alexmartin9999/Pictures/DeviantArt"); 
		this.getContentPane().add(downloadPathText); 
		
		Label usernameLabel = new Label(); 
		usernameLabel.setText("DA username"); 
		usernameLabel.setBounds(20, 60, 360, 20);
		this.getContentPane().add(usernameLabel); 
		usernameText = new TextField(); 
		usernameText.setBounds(20, 80, 360, 20);
		usernameText.setText("alexjohnmartin"); 
		this.getContentPane().add(usernameText); 
		
		Label maxDownloadsLabel = new Label(); 
		maxDownloadsLabel.setText("max downloads (0 = unlimited)"); 
		maxDownloadsLabel.setBounds(20, 110, 360, 20);
		this.getContentPane().add(maxDownloadsLabel); 
		maxDownloadsText = new TextField(); 
		maxDownloadsText.setBounds(20, 130, 360, 20);
		maxDownloadsText.setText("0"); 
		this.getContentPane().add(maxDownloadsText);

        Label offsetLabel = new Label();
        offsetLabel.setText("download offset (start at index X)");
        offsetLabel.setBounds(20, 160, 360, 20);
        this.getContentPane().add(offsetLabel);
        offsetText = new TextField();
        offsetText.setBounds(20, 180, 360, 20);
        offsetText.setText("0");
        this.getContentPane().add(offsetText);

        Label pauseLabel = new Label();
        pauseLabel.setText("milliseconds pause between downloads");
        pauseLabel.setBounds(20, 210, 360, 20);
        this.getContentPane().add(pauseLabel);
        pauseText = new TextField();
        pauseText.setBounds(20, 230, 360, 20);
        pauseText.setText("0");
        this.getContentPane().add(pauseText);

        downloadButton = new Button();
        downloadButton.setLabel("download");
        downloadButton.setBounds(50, 260, 300, 30);
        downloadButton.addActionListener(this);
        this.getContentPane().add(downloadButton);

        statusLabel = new Label("...");
        statusLabel.setBounds(20, 300, 360, 20);
        this.getContentPane().add(statusLabel);

        setTitle("DA favs downloader");
		setSize(400, 350);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		JFrame f = new MyFrame();
		f.setVisible(true); 
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		downloadButton.setEnabled(false); 
		Wget wget = new Wget(); 
		
		int pageCount = 0; 
		int overallItemCount = 0; 
		int itemsInPage = 60; 
		int itemsWithLargeDownloads = 0; 
		int max = Integer.parseInt(maxDownloadsText.getText());
        int offsetCount = Integer.parseInt(offsetText.getText());
		
		while (itemsInPage > 0) {
			itemsInPage = 0; 
			pageCount++; 
			statusLabel.setText("getting page " + pageCount); 
			String result = wget.getHTML("http://backend.deviantart.com/rss.xml?q=favby%3A" + usernameText.getText() + "&offset=" + overallItemCount);
			
			int itemStartIndex = result.indexOf("<item>"); 
			int itemEndIndex = result.indexOf("</item>") + 7;
            long pause = Long.parseLong(pauseText.getText());

			while (itemStartIndex >= 0) {
				itemsInPage++;
				overallItemCount++; 
				String itemXml = result.substring(itemStartIndex, itemEndIndex); 
				DeviantArtItem daItem = new DeviantArtItem(itemXml); 
							
				/*
				System.out.println("Title = " + daItem.getTitle()); 
				System.out.println("Media title = " + daItem.getMediaTitle()); 
				System.out.println("Link = " + daItem.getLink());				
				System.out.println("Author = " + daItem.getAuthor()); 
				System.out.println("Category = " + daItem.getCategory()); 
				System.out.println("Full category = " + daItem.getFullCategory()); 
				System.out.println("URL = " + daItem.getDownloadUrl()); 
				System.out.println("Filename = " + daItem.getFilename()); 
				*/

                if (overallItemCount >= offsetCount) {
                    statusLabel.setText("downloading file " + overallItemCount);
                    wget.downloadFile(daItem.getDownloadUrl(), downloadPathText.getText() + "/" + daItem.getAuthor() + "/" + daItem.getFilename());
                    try {
                        Thread.sleep(pause);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

				result = result.substring(itemEndIndex); 
				itemEndIndex = result.indexOf("</item>") + 7; 
				itemStartIndex = result.indexOf("<item>");
				
				if (max > 0 && overallItemCount - offsetCount >= max) break;
			}

			if (max > 0 && overallItemCount - offsetCount >= max) break;
			//if (pageCount > 1)
			//	break; 
		}
	
		statusLabel.setText("downloaded " + overallItemCount + " items");	
		downloadButton.setEnabled(true); 
	}
}

class Terminator extends WindowAdapter {
	public void windowClosing(WindowEvent e) {
	  System.exit(0); 
  	}
}

class Wget {
	
	public void downloadFile(String urlToDownload, String fileToSaveAs) {
		System.out.println("downloading " + urlToDownload); 				
		//System.out.println("to " + fileToSaveAs); 				
		try {
			File directory = new File(fileToSaveAs.substring(0, fileToSaveAs.lastIndexOf("/"))); 
			if (!directory.exists()) { directory.mkdirs(); }
			
			URL website = new URL(urlToDownload);
		    ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		    FileOutputStream fos = new FileOutputStream(fileToSaveAs);
		    fos.getChannel().transferFrom(rbc, 0, 1 << 24);
		}
		catch(Exception e) {
			System.out.println("ERROR - " + e.getMessage()); 
		}
	}

   public String getHTML(String urlToRead) {
      URL url;
      HttpURLConnection conn;
      BufferedReader rd;
      String line;
      String result = "";
      try {
         url = new URL(urlToRead);
         conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("GET");
         rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
         while ((line = rd.readLine()) != null) {
            result += line;
         }
         rd.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
      return result;
   }
}