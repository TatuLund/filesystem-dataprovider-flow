package org.vaadin.filesystemdataprovider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Date;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("ftp")
public class FtpView extends VerticalLayout {

	private FakeFtpServer fakeFtpServer;
	private FtpClient ftpClient;
	public FTPClient ftp;

	public FtpView() {
		try {
			setup();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
//	    String ftpUrl = String.format(
//	    	      "ftp://user:password@localhost:%d/foobar.txt", fakeFtpServer.getServerControlPort());
//	    	 
//        URLConnection urlConnection = null;
//        try {
//		    urlConnection = new URL(ftpUrl).openConnection();
//        } catch (IOException e) {
//	        e.printStackTrace();
//        }
//	    InputStream inputStream = null;
//	    try {
//            inputStream = urlConnection.getInputStream();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//        try {
//            Files.copy(inputStream, new File("downloaded_buz.txt").toPath());
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//        try {
//            inputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
	    	 	    	 
    	FtpSelect fileSelect = new FtpSelect(ftp);
    	fileSelect.addValueChangeListener(event -> {
        	FtpFile file = fileSelect.getValue();
    		Date date = Date.from(file.getTimestamp().toInstant());
        	if (!file.isDirectory()) {
        		Notification.show(file.getPath()+", "+date+", "+file.getSize());
        		ByteArrayOutputStream output = new ByteArrayOutputStream();
        		try {
					ftp.retrieveFile(file.getPath(),output);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	} else {
        		Notification.show(file.getPath()+", "+date);
        	}
    	});
    	fileSelect.setWidth("500px");
    	fileSelect.setHeight("500px");
    	setSizeFull();
    	this.setAlignItems(Alignment.CENTER);
    	this.setJustifyContentMode(JustifyContentMode.CENTER);
    	fileSelect.setLabel("Select file");
        add(fileSelect);

//        new File("downloaded_buz.txt").delete(); // cleanup
    }
	
    public void setup() throws IOException {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount("user", "password", "/data"));
 
        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/data"));
        fileSystem.add(new DirectoryEntry("/data/sub_1"));
        fileSystem.add(new DirectoryEntry("/data/sub_2"));
        fileSystem.add(new DirectoryEntry("/data/sub_2/subsub"));
        fileSystem.add(new FileEntry("/data/foobar_1.txt", "abcdef 1234567890"));
        fileSystem.add(new FileEntry("/data/foobar_2.txt", "abcdef 1234567890"));
        fileSystem.add(new FileEntry("/data/sub_1/barfoo_1.txt", "abcdef 1234567890"));
        fileSystem.add(new FileEntry("/data/sub_1/barfoo_2.txt", "abcdef 1234567890"));
        fileSystem.add(new FileEntry("/data/sub_2/barfoo_3.txt", "abcdef 1234567890"));
        fileSystem.add(new FileEntry("/data/sub_2/subsub/just_bar.txt", "abcdef 1234567890"));
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(0);
 
        fakeFtpServer.start();
 
        ftpClient = new FtpClient("localhost", fakeFtpServer.getServerControlPort(), "user", "password");
        ftpClient.open();
    }	

    public void teardown() throws IOException {
        ftpClient.close();
        fakeFtpServer.stop();
    }

    public class FtpClient {
		 
	    private String server;
	    private int port;
	    private String user;
	    private String password;
	 
	    // constructor
	 
	    public FtpClient(String server, int serverControlPort, String user, String password) {
			this.user = user;
			this.password = password;
			this.server = server;
			this.port = serverControlPort;
		}

		void open() throws IOException {
	        ftp = new FTPClient();
	 
	        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
	 
	        ftp.connect(server, port);
	        int reply = ftp.getReplyCode();
	        if (!FTPReply.isPositiveCompletion(reply)) {
	            ftp.disconnect();
	            throw new IOException("Exception in connecting to FTP Server");
	        }
	 
	        ftp.login(user, password);
	    }
	 
	    void close() throws IOException {
	        ftp.disconnect();
	    }
	}	
}
