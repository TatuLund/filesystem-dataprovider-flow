package org.vaadin.filesystemdataprovider;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.net.ftp.FTPFile;

/**
 * Extended version FTPFile including comparable nature and path
 * 
 * @author Tatu Lund
 * @since 2.1.0
 */
public class FtpFile extends FTPFile implements Comparable<FtpFile> {

	private String path;
	private boolean root = false;

	public FtpFile(FTPFile file) {
		this.setGroup(file.getGroup());
		this.setName(file.getName());
		this.setSize(file.getSize());
		this.setTimestamp(file.getTimestamp());
		this.setUser(file.getUser());
		this.setType(file.getType());
		this.setLink(file.getLink());
	}

	public FtpFile() {
		this.setName("Root");
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		this.setType(DIRECTORY_TYPE);
		this.setTimestamp(cal);
	}

	@Override
	public int compareTo(FtpFile file) {
		return this.getName().compareTo(file.getName());
	}

	public String getPath() {
		if (this.isDirectory()) {
		    return path;
		} else {
			return path.substring(0, path.length()-1);
		}
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

}
