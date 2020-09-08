# FilesystemDataProvider

FilesystemDataProvider is a data model add-on for Vaadin 10/14 (Flow) providing hierarchical
data of the filesystem, and it can be used to supply it to Tree and TreeGrid components.
This is fully server side add-on.

The inspiration of this add-on is to provide similar functionality than Vaadin 7's 
built in FilesystemContainer had. Hence it is useful for Vaadin 7 -> Vaadin 10/14 migration projects.

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

### Version 2.1.0
- Add FTP variants

### Version 2.0.0
- First release for Vaadin 10+