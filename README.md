# Android Storage API Demo

## Overview

This Android application demonstrates how to properly handle file storage operations on Android devices, with special attention to the changes introduced in Android 10 (API level 29) and above. The app showcases best practices for:

- Copying images to shared storage
- Listing files from storage directories
- Deleting files with user confirmation
- Sharing files with other applications using FileProvider

The app implements different strategies based on the Android version, using direct file access for older Android versions and MediaStore API for Android 10 and above.

## Features

- **Copy Images**: Copy a sample image from app resources to the device's shared Pictures directory.
- **List Files**: Display all image files in the Pictures directory with their names and sizes.
- **Delete Files**: Remove stored images with a confirmation dialog.
- **Share Files**: Share images with other applications securely using FileProvider.

## Technical Implementation

### Android Version-Specific Approach

The app uses different implementation strategies based on the Android version:

#### For Android 9 (Pie) and Below:
- Direct file system access using `Environment.getExternalStoragePublicDirectory()`
- Runtime permission requests for storage access
- Traditional file operations using Java I/O classes

#### For Android 10 (Q) and Above:
- MediaStore API for accessing shared storage
- Scoped storage compliance
- No need for runtime storage permissions

### Key Classes and Methods

#### File Copy Operations
- `copyImageFile()`: Entry point that selects the appropriate method based on device API level
- `copyImageToSharedStorage()`: For devices running Android 9 or below
- `copyImageToMediaStore()`: For devices running Android 10 or above

#### File Listing
- `listFilesInDirectory()`: Shows files from the appropriate storage location
- `listDirectoryFiles()`: Traditional file listing for older devices
- `listMediaStoreImages()`: ContentResolver-based listing for newer devices

#### File Deletion
- `showDeleteConfirmationDialog()`: Confirms file deletion with the user
- `deleteFile()`: Performs deletion on older Android versions
- `showMediaStoreDeleteConfirmationDialog()` and `deleteMediaStoreFile()`: Handle deletion on Android 10+

#### File Sharing
- `shareFile()`: Traditional sharing using FileProvider
- `shareMediaStoreImage()`: Sharing MediaStore images on newer Android versions

## Permissions

The app requires the following permissions:

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
                 android:maxSdkVersion="28" />
```

Note: The WRITE_EXTERNAL_STORAGE permission is only needed for Android 9 and below, hence the `maxSdkVersion="28"` attribute.

## Setup Requirements

### File Provider Configuration

To use the sharing functionality, you must configure a FileProvider in your AndroidManifest.xml:

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

And create a file at `res/xml/file_paths.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path name="external_files" path="Pictures/" />
    <external-files-path name="external_files" path="Pictures/" />
</paths>
```

### Resource Requirements

The app expects a sample image in the raw resources folder:
- Create a directory named `raw` in your `res` folder
- Add an image named `sample_image.jpg` to this directory

## UI Components

The app's layout needs to include:
- Four buttons with the IDs:
  - `btnCopyImage`
  - `btnListFiles`
  - `btnDeleteFile`
  - `btnShareFile`
- A TextView with ID `tvFileList` to display the file listing

## Best Practices Implemented

1. **Version-specific logic**: The app adapts its behavior based on the Android version
2. **Proper permission handling**: Requests permissions only when necessary
3. **User consent for deletion**: Implements confirmation dialogs before deletion
4. **Secure file sharing**: Uses FileProvider to share files securely
5. **MediaStore integration**: Uses the recommended MediaStore API for Android 10+
6. **Scoped Storage compliance**: Works correctly with Android's scoped storage model

## Common Issues and Troubleshooting

### File Not Found Exceptions
- Ensure the sample_image.jpg exists in the res/raw directory
- Check that the storage permissions are properly granted on devices below Android 10

### Permission Denied
- Verify that the application has requested and been granted the appropriate permissions
- For Android 10+, specific files may require user consent through the system file picker

### FileProvider Authority Issues
- Ensure the FileProvider authority in the code matches the one in AndroidManifest.xml
- Check that the file paths in file_paths.xml are correctly configured

### MediaStore Operations Failing
- Verify that the correct MIME types and display names are being used
- Ensure proper handling of IS_PENDING flag when inserting new files

## Further Improvements

Potential enhancements for this application:
1. Add support for different file types beyond images
2. Implement batch operations for copying/deleting multiple files
3. Add search functionality to filter files
4. Include file metadata viewing
5. Add support for cloud storage services
6. Implement a more sophisticated UI with thumbnails and detailed file information

## References

- [Android Storage Documentation](https://developer.android.com/training/data-storage)
- [Scoped Storage in Android 10+](https://developer.android.com/about/versions/10/privacy/changes#scoped-storage)
- [MediaStore API Guide](https://developer.android.com/reference/android/provider/MediaStore)
- [FileProvider Documentation](https://developer.android.com/reference/androidx/core/content/FileProvider)
