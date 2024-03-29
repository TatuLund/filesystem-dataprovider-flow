# FilesystemDataProvider

FilesystemDataProvider is a data model add-on for Vaadin 23(Flow) providing hierarchical
data of the filesystem, and it can be used to supply it to Tree and TreeGrid components.
This is fully server side add-on.

The inspiration of this add-on is to provide similar functionality than Vaadin 7's 
built in FilesystemContainer had. Hence it is useful for Vaadin 7 -> Vaadin 23 migration projects.

# Components

* FilesystemDataProvider: Data provider wrapper of the filesystem

* FileSelect: Field UI component that allows selecting a single file, value is File

* MultiFileSelect: Field UI component that allows selecting multiple files, value is Set<File>

* FtpDataProvider: Data provider wrapper of the FTP client, uses appache commons ftp

* FtpSelect: Field UI component that acts as FTP client, value is FtpFile
  
* FtpFile: Extended FTPFile, that implements Comparable, so that it works with Vaadin's DataProvider API's.

## Development instructions

Starting the test/demo server:
1. Run `mvn jetty:run`.
2. Open http://localhost:8080 in the browser.

## Publishing to Vaadin Directory

You can create the zip package needed for [Vaadin Directory](https://vaadin.com/directory/) using
```
mvn versions:set -DnewVersion=1.0.0 # You cannot publish snapshot versions 
mvn install -Pdirectory
```

The package is created as `target/filesystemdataprovider-1.0.0.zip`

For more information or to upload the package, visit https://vaadin.com/directory/my-components?uploadNewComponent

## History

### Version 3.1.0
- Fixed icons to be compatible with Vaadin 21 and newer

### Version 3.0.0
- Build against Vaadin 18

### Version 2.1.0
- Add FTP variants

### Version 2.0.0
- First release for Vaadin 10+
