package com.alchitry.labs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

/**
 * This utility extracts files and directories of a standard zip file to a destination directory.
 * 
 * @author www.codejava.net
 *
 */
public class ExtractUtility {
	/**
	 * Size of the buffer to read/write data
	 */
	private static final int BUFFER_SIZE = 4096;

	private ExtractUtility() {
	}

	/**
	 * Extracts a zip file specified by the zipFilePath to a directory specified by destDirectory (will be created if does not exists)
	 * 
	 * @param zipFilePath
	 * @param destDirectory
	 * @throws IOException
	 */
	public static void unzip(String zipFilePath, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				// if the entry is a file, extracts it
				extractFile(zipIn, filePath);
			} else {
				// if the entry is a directory, make the directory
				File dir = new File(filePath);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	/**
	 * Extracts a zip entry (file entry)
	 * 
	 * @param zipIn
	 * @param filePath
	 * @throws IOException
	 */
	private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

	public static void untargz(String targzFilePath, String destDirectory) throws IOException {
		/** create a TarArchiveInputStream object. **/

		FileInputStream fin = new FileInputStream(targzFilePath);
		BufferedInputStream in = new BufferedInputStream(fin);
		GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
		TarArchiveInputStream tarIn = new TarArchiveInputStream(gzIn);

		TarArchiveEntry entry = null;

		/** Read the tar entries using the getNextEntry method **/

		while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {

			/** If the entry is a directory, create the directory. **/

			if (entry.isDirectory()) {

				File f = new File(destDirectory + entry.getName());
				f.mkdirs();
			}
			/**
			 * If the entry is a file,write the decompressed file to the disk and close destination stream.
			 **/
			else {
				int count;
				byte data[] = new byte[BUFFER_SIZE];

				FileOutputStream fos = new FileOutputStream(destDirectory + entry.getName());
				BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER_SIZE);
				while ((count = tarIn.read(data, 0, BUFFER_SIZE)) != -1) {
					dest.write(data, 0, count);
				}
				dest.close();
				
				File f = new File(destDirectory + entry.getName());
				
				int mode = entry.getMode();
				
				if ((mode & 0x01) != 0)
					f.setExecutable(true, false);
				if ((mode & 0x02) != 0)
					f.setWritable(true, false);
				if ((mode & 0x04) != 0)
					f.setReadable(true, false);
				
				mode >>= 6;
				
				if ((mode & 0x01) != 0)
					f.setExecutable(true);
				if ((mode & 0x02) != 0)
					f.setWritable(true);
				if ((mode & 0x04) != 0)
					f.setReadable(true);
			}
		}

		/** Close the input stream **/

		tarIn.close();

	}
}