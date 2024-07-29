package net.gnu.util;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.PasswordRequiredException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Date;
import java.util.regex.Pattern;
//import android.util.Log;

public class CompressedFile implements Serializable {

	private static final long serialVersionUID = 3856752785908915865L;

	private final static String TAG = "CompressedFile";
	private final static int LEN = 32768;

	private final String filePath;
	private final File dest7zFile;

	private ArchiveInputStream ais;
	private CompressorInputStream cis;
	private SevenZFile sevenZFile;
	private ZipFile zipFile;
	private List<String> resources;
	public String password;
	private long nano;
	private int noFile = 0;
	
	public static final Pattern COMPRESSOR_PATTERN = Pattern.compile("[^\n]*?\\.(br|gz|bz2|xz|lzma|zstd|z|lz4|sz)$", Pattern.CASE_INSENSITIVE);
	public static final Pattern TAR_PATTERN = Pattern.compile("[^\n]*?\\.((tar\\.(br|gz|bz2|xz|lzma|zstd|z|lz4|sz))|(tgz|tbz2|txz|tz|tsz))$", Pattern.CASE_INSENSITIVE);
	public static final Pattern ARCHIVE_PATTERN = Pattern.compile("[^\n]*?\\.(ar|arj|cpio|dump|tar|pack)$", Pattern.CASE_INSENSITIVE);
	public static final Pattern ZIP_PATTERN = Pattern.compile("[^\n]*?\\.(zip|jar|apk|epub|fb2|cbz|odt|ods|odp|docx|xlsx|pptx)$", Pattern.CASE_INSENSITIVE);
	public static final Pattern SZIP_PATTERN = Pattern.compile("[^\n]*?\\.(7z)", Pattern.CASE_INSENSITIVE);
	
	public CompressedFile(final String filePath) {
		this.filePath = filePath;
		this.dest7zFile = new File(this.filePath);
	}

	public CompressedFile(final String filePath, String password) {
		this.filePath = filePath;
		this.dest7zFile = new File(this.filePath);
		this.password = password;
	}

	class SevenZipInfo {
		File sevenFile;
		String password;
	}
	public static void main(String[] a) throws PasswordRequiredException, CompressorException, IOException, ArchiveException {
		File[] fs = new File[]{
//			new File("/storage/emulated/0/.aide/build-ffmpeg-armv7a.sh"),
//			new File("/storage/emulated/0/.mixplorer/util-zip.zip"),
//			new File("/storage/emulated/0/.mixplorer/util256.zip"),
//			new File("/storage/emulated/0/.mixplorer/util-noenc.zip"),
//			new File("/storage/emulated/0/.mixplorer/util-noenc.7z"),
			new File("/storage/emulated/0/.mixplorer/configure.zip"),
			//new File("/storage/emulated/0/.mixplorer/JavaScriptAPI.zip"),
			//new File("/storage/emulated/0/.mixplorer/framework.7z"),
			
		};
		File sevenZFile = new File("/storage/0067-7E11/Downloads/Lzma.7z");
		//new CompressedFile(sevenZFile.getAbsolutePath()).comressTo7z("a", fs);
		CompressedFile compressedFile = new CompressedFile(sevenZFile.getAbsolutePath(), "a");
		long took1 = 0;
		for (int i = 0 ; i < 5; i++) {
			took1 += testTime(compressedFile);
		}
		compressedFile = new CompressedFile("/storage/0067-7E11/Downloads/Lzma2.7z", "a");
		long took2 = 0;
		for (int i = 0 ; i < 5; i++) {
			took2 += testTime(compressedFile);
		}
		Log.d(TAG, Util.nf.format(took1));
		Log.d(TAG, Util.nf.format(took2));
		//File oZipFile = new File("/storage/0067-7E11/Downloads/Yt4.zip");
		//new CompressedFile(oZipFile.getAbsolutePath()).comressTo7z("a", fs);
		Log.d(TAG, "ok");
	}

	private static long testTime(CompressedFile compressedFile) throws PasswordRequiredException, CompressorException, IOException, ArchiveException {
		List<String> arr = compressedFile.list();//("a", fs);
		long curTime = System.nanoTime();
		for (String s : arr) {
			compressedFile.getResourceAsStream(s);
		}
		//long took = System.nanoTime() - curTime;
		//Log.d(TAG, Util.nf.format(curTime));
		return System.nanoTime() - curTime;
	}

	private void comressTo7z(final String oPassword, final File[] fs) {
		ExceptionLogger.d(TAG, dest7zFile.getName());
		ExceptionLogger.d(TAG, oPassword);
		try {
			boolean skip = false;
			boolean overwrite = false;
			final Scanner input = new Scanner(System.in);
			if (dest7zFile.exists()) {
				final CompressedFile compressedFile = new CompressedFile(dest7zFile.getAbsolutePath());
				ExceptionLogger.d(TAG, compressedFile.list());
				final byte[] is2Barr = FileUtil.is2Barr(compressedFile.getResourceAsStream(""), false);
				ExceptionLogger.d(TAG, "is2barr " + is2Barr.length + new String(is2Barr));
				System.out.print("dest exists " + dest7zFile.getName() + ", overwrite (y/n)? ");
				overwrite = input.next().trim().equalsIgnoreCase("y");
				if (overwrite) {
					dest7zFile.delete();
				}
			}
			SevenZOutputFile sevenZOutput = null;
			ZipFile oZipFile = null;
			String name = dest7zFile.getName();
			final ZipParameters parameters = new ZipParameters();
			if (SZIP_PATTERN.matcher(name).matches()) {
				sevenZOutput = new SevenZOutputFile(dest7zFile);
			} else if (ZIP_PATTERN.matcher(name).matches()) {
				oZipFile = new ZipFile(dest7zFile);
				if (oPassword != null && oPassword.length() > 0) {
					parameters.setCompressionMethod(CompressionMethod.DEFLATE);
					parameters.setCompressionLevel(CompressionLevel.FASTEST); 
					parameters.setEncryptFiles(true);
					parameters.setEncryptionMethod(EncryptionMethod.AES);
					parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
					oZipFile.setPassword(oPassword.toCharArray());
				}
			}

			final byte[] barr = new byte[LEN];
			String filePath;
			SevenZArchiveEntry entry;
			SevenZFile sevenZFile = null;
			String password = null;
			ZipFile iZipFile = null;
			final List<String> added = new LinkedList<>();
			boolean i7zip = false;
			boolean iZip = false;
			int num = 0;
			nano = 0;
			noFile = 0;
			final List<File> lf = new LinkedList<File>();
			for (File f : fs) {
				if (f.isDirectory()) {
					lf.addAll(FileUtil.getFiles(f, false, null, null));
				} else if (f.isFile()) {
					lf.add(f);
				}
			}
			for (File f : lf) {
				filePath = f.getAbsolutePath();
				i7zip = false;
				iZip = false;
				final String iName = f.getName();
				ExceptionLogger.d(TAG, ++num + ": " + iName + ", length " + f.length());
				if ((i7zip = (SZIP_PATTERN.matcher(filePath).matches()))
					|| (iZip = ZIP_PATTERN.matcher(filePath).matches())) {
					boolean finishScan = false;
					skip = false;
					while (!finishScan && !skip) {
						try {
							if (i7zip) {
								if (password == null || password.length() == 0) {
									sevenZFile = new SevenZFile(f);
									sevenZFile.getNextEntry();
									sevenZFile.read();
									sevenZFile.close();
									sevenZFile = new SevenZFile(f);
								} else {
									sevenZFile = new SevenZFile(f, password.toCharArray());
									password = null;
								}
								while ((entry = sevenZFile.getNextEntry()) != null) {
									ExceptionLogger.d(TAG, "entry " + entry.getName() + ", size " + entry.getSize() + ", lastModified " + entry.getLastModifiedDate());
									if (added.indexOf(entry.getName()) == -1) {
										if (sevenZOutput != null) {
											addEntry(entry, sevenZOutput, sevenZFile, barr, added);
										} else if (oZipFile != null) {
											addEntry(entry.getName(),
													 (int)entry.getSize(),
													 entry.getLastModifiedDate().getTime(),
													 oZipFile,
													 parameters,
													 sevenZFile,
													 added);
										}
									} else {
										System.out.print("Duplicate " + entry.getName() + ", skip (y/n)? ");
										skip = input.next().trim().equalsIgnoreCase("y");
										if (!skip) {
											if (sevenZOutput != null) {
												addEntry(entry, sevenZOutput, sevenZFile, barr, added);
											} else if (oZipFile != null) {
												oZipFile.removeFile(entry.getName());
												addEntry(entry.getName(),
														 (int)entry.getSize(),
														 entry.getLastModifiedDate().getTime(),
														 oZipFile,
														 parameters,
														 sevenZFile,
														 added);
											}
										}
									}
									//ExceptionLogger.d(TAG, entry.getName());
								}
							} else if (iZip) {
								if (password == null || password.length() == 0) {
									iZipFile = new ZipFile(f);
								} else if (iZip) {
									iZipFile = new ZipFile(f, password.toCharArray());
									password = null;
								}
								final List<FileHeader> fhs =  iZipFile.getFileHeaders();
								for (FileHeader fh : fhs) {
									String string = new String(fh.getFileName().getBytes("CP437"), "utf-8");
									ExceptionLogger.d(TAG, "isFileNameUTF8Encoded " + fh.isFileNameUTF8Encoded());
									ExceptionLogger.d(TAG, "string " + string.length());
									ExceptionLogger.d(TAG, "getFileNameLength " + fh.getFileNameLength());
									ExceptionLogger.d(TAG, "equals " + (string.length() == fh.getFileNameLength()));
									ExceptionLogger.d(TAG, "FileHeader name " + string + ", size " + fh.getUncompressedSize() + ", lastModified " + new Date(fh.getLastModifiedTimeEpoch()));
									if (added.indexOf(fh.getFileName()) == -1) {
										if (oZipFile != null) {
											addEntry(fh, oZipFile, parameters,
													 iZipFile, barr, added);
										} else if (sevenZOutput != null) {
											addEntry(fh,
													 sevenZOutput,
													 iZipFile,
													 barr,
													 added);
										}
									} else {
										System.out.print("Duplicate " + fh.getFileName() + ", skip (y/n)? ");
										skip = input.next().trim().equalsIgnoreCase("y");
										if (!skip) {
											if (oZipFile != null) {
												oZipFile.removeFile(fh.getFileName());
												addEntry(fh, oZipFile, parameters,
														 iZipFile, barr, added);
											} else if (sevenZOutput != null) {
												addEntry(fh,
														 sevenZOutput,
														 iZipFile,
														 barr,
														 added);
											}
										}
									}
									//ExceptionLogger.d(TAG, entry.getName());
								}
							}

							finishScan = true;
						} catch (IOException e) {
							ExceptionLogger.d(TAG, e.getMessage());
							if (sevenZFile != null) {
								sevenZFile.close();
							}
							System.out.print("There is password, skip this file(y/n): ");
							skip = input.next().trim().equalsIgnoreCase("y");
							if (!skip) {
								System.out.print("Enter password: ");
								password = input.next().trim();
							}
						} finally {
							if (sevenZFile != null) {
								sevenZFile.close();
							}
						}
					}
				} else {
					long l = System.nanoTime();
					if (sevenZOutput != null) {
						if (added.indexOf(iName) == -1) {
							addEntry(sevenZOutput, f, barr, added);
						} else {
							System.out.print("Duplicate " + iName + ", skip (y/n)? ");
							skip = input.next().trim().equalsIgnoreCase("y");
							if (!skip) {
								l = System.nanoTime();
								addEntry(sevenZOutput, f, barr, added);
							}
						}
					} else if (oZipFile != null) {
						parameters.setFileNameInZip(iName);
						parameters.setLastModifiedFileTime(f.lastModified());
						if (added.indexOf(iName) == -1) {
							oZipFile.addFile(f, parameters);
							added.add(iName);
						} else {
							System.out.print("Duplicate " + iName + ", skip (y/n)? ");
							skip = input.next().trim().equalsIgnoreCase("y");
							if (!skip) {
								l = System.nanoTime();
								oZipFile.addFile(f, parameters);
								added.add(iName);
							}
						}
					}
					nano += (System.nanoTime() - l);
					ExceptionLogger.d(TAG, "Compressed " + ++noFile + " files");
				}
			}
			if (sevenZOutput != null) {
				sevenZOutput.finish();
				sevenZOutput.close();
			}
			ExceptionLogger.d(TAG, noFile + " files, took " + Util.nf.format(nano) + " nano seconds");
		} catch (Throwable e) {
			ExceptionLogger.e(TAG, e);
		}
	}

	private void addEntry(final FileHeader fh,
						  final ZipFile oZipFile,
						  final ZipParameters zipParameters,
						  final ZipFile iZipFile,
						  final byte[] barr,
						  final List<String> added) throws IOException {
		final long l = System.nanoTime();
		final String fileName;
		if (fh.isFileNameUTF8Encoded()) {
			fileName = fh.getFileName();
		} else {
			fileName = new String(fh.getFileName().getBytes("CP437"), "utf-8");
		}
		zipParameters.setFileNameInZip(fileName);
		zipParameters.setLastModifiedFileTime(fh.getLastModifiedTimeEpoch());
		oZipFile.addStream(iZipFile.getInputStream(fh), zipParameters);
		added.add(fileName);
		ExceptionLogger.d(TAG, "Compressed " + ++noFile + " files");
		nano += (System.nanoTime() - l);
	}

	private void addEntry(final String fileName,
						  final int length,
						  final long lastModified,
						  final ZipFile oZipFile,
						  final ZipParameters zipParameters,
						  final SevenZFile i7zFile,
						  final List<String> added) throws IOException {
		final long l = System.nanoTime();
		zipParameters.setFileNameInZip(fileName);
		zipParameters.setLastModifiedFileTime(lastModified);
		final SevenZInputStream zis = new SevenZInputStream(i7zFile, length, i7zFile.getDefaultName(), fileName);
		oZipFile.addStream(zis, zipParameters);
		added.add(fileName);
		ExceptionLogger.d(TAG, "Compressed " + ++noFile + " files");
		nano += (System.nanoTime() - l);
	}

	private void addEntry(final FileHeader fh,
						  final SevenZOutputFile sevenZOutput,
						  final ZipFile iZipFile,
						  final byte[] barr,
						  final List<String> added) throws IOException {
		final long l = System.nanoTime();
		final String fileName;
		if (fh.isFileNameUTF8Encoded()) {
			fileName = fh.getFileName();
		} else {
			fileName = new String(fh.getFileName().getBytes("CP437"), "utf-8");
		}
		final SevenZArchiveEntry en = createArchiveEntry(fh.isDirectory(), fh.getLastModifiedTimeEpoch(), fileName);
		sevenZOutput.putArchiveEntry(en);
		int read = 0;
		try (InputStream is = iZipFile.getInputStream(fh)) {
			while ((read = is.read(barr)) != -1) {
				sevenZOutput.write(barr, 0, read);
			}
		}
		sevenZOutput.closeArchiveEntry();
		added.add(fileName);
		ExceptionLogger.d(TAG, "Compressed " + ++noFile + " files");
		nano += (System.nanoTime() - l);
	}

	private void addEntry(final SevenZArchiveEntry entry,
						  final SevenZOutputFile sevenZOutput,
						  final SevenZFile i7zFile,
						  final byte[] barr,
						  final List<String> added) throws IOException {
		final long l = System.nanoTime();
		final SevenZArchiveEntry en = createArchiveEntry(entry.isDirectory(), entry.getLastModifiedDate().getTime(), entry.getName());
		sevenZOutput.putArchiveEntry(en);
		int totalRead = 0;
		int read = 0;
		final int length = (int) entry.getSize();
		int min;
		while ((min = Math.min(LEN, length - totalRead)) > 0) {
			read = i7zFile.read(barr, 0, min);
			totalRead += read;
			sevenZOutput.write(barr, 0, read);
		}
		sevenZOutput.closeArchiveEntry();
		added.add(entry.getName());
		ExceptionLogger.d(TAG, "Compressed " + ++noFile + " files");
		nano += (System.nanoTime() - l);
	}

	private static void addEntry(final SevenZOutputFile sevenZOutput,
								 final File f,
								 final byte[] barr,
								 final List<String> added) throws IOException {
		final SevenZArchiveEntry entry = sevenZOutput.createArchiveEntry(f, f.getName());
		sevenZOutput.putArchiveEntry(entry);
		int read = 0;
		try (final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f))) {
			while ((read = bis.read(barr)) != -1) {
				sevenZOutput.write(barr, 0, read);
			}
			sevenZOutput.closeArchiveEntry();
			added.add(entry.getName());
		}
	}

	public static SevenZArchiveEntry createArchiveEntry(final boolean isDirectory, final long lastModified, final String entryName) {
		ExceptionLogger.d(TAG, "createArchiveEntry isDirectory " + isDirectory + ", lastModified " + lastModified);
		final SevenZArchiveEntry entry = new SevenZArchiveEntry();
        entry.setDirectory(isDirectory);
        entry.setName(entryName);
        entry.setLastModifiedDate(new Date(lastModified));
        return entry;
	}

	private void initStream() throws FileNotFoundException, ArchiveException, IOException, PasswordRequiredException, CompressorException {
		//ExceptionLogger.d(TAG, "initStream filePath " + filePath);
		if (filePath == null || filePath.length() == 0) {
			return;
		}
		if (ZIP_PATTERN.matcher(filePath).matches()) {
			//ExceptionLogger.d(TAG, "initStream ZIP_PATTERN");
			if (password == null || password.length() == 0) {
				zipFile = new ZipFile(dest7zFile);
			} else {
				zipFile = new ZipFile(dest7zFile, password.toCharArray());
			}
		} else if (filePath.toLowerCase().endsWith(".7z")) {
			//ExceptionLogger.d(TAG, "initStream 7z password " + (password != null && password.length() > 0));
//			boolean openOk = false;
//			while (!openOk) {
//				try {
			if (password == null || password.length() == 0) {
				sevenZFile = new SevenZFile(dest7zFile);
				sevenZFile.getNextEntry();
				sevenZFile.read();
				sevenZFile.close();
				sevenZFile = new SevenZFile(dest7zFile);
			} else {
				sevenZFile = new SevenZFile(dest7zFile, password.toCharArray());
			}
//					openOk = true;
//				} catch (PasswordRequiredException e) {
//					ExceptionLogger.d(TAG, e.getMessage());
//					if (sevenZFile != null) {
//						sevenZFile.close();
//					}
//					final Scanner input = new Scanner(System.in);
//					System.out.print("Enter password: ");
//					password = input.next().trim();
//					if (password == null || password.length() == 0) {
//						openOk = true;
//					}
//				} 
//			}
		} else {
			final InputStream bis = new BufferedInputStream(new FileInputStream(dest7zFile));
			if (ARCHIVE_PATTERN.matcher(filePath).matches()) {
				ais = new ArchiveStreamFactory().createArchiveInputStream(bis);
				//ExceptionLogger.d(TAG, "initStream ARCHIVE_PATTERN");
			} else if (TAR_PATTERN.matcher(filePath).matches()) {
				final InputStream cis = new CompressorStreamFactory().createCompressorInputStream(bis);
				ais = new TarArchiveInputStream(cis);
				//ExceptionLogger.d(TAG, "initStream TAR_PATTERN");
			} else if (COMPRESSOR_PATTERN.matcher(filePath).matches()) {
				cis = new CompressorStreamFactory().createCompressorInputStream(bis);
				//ExceptionLogger.d(TAG, "initStream COMPRESSOR_PATTERN");
			}
		}
	}

	public void close() {
		try {
			if (zipFile != null) {
				zipFile.close();
				zipFile = null;
			}
			if (ais != null) {
				ais.close();
				ais = null;
			}
			if (cis != null) {
				cis.close();
				cis = null;
			}
			if (sevenZFile != null) {
				sevenZFile.close();
				sevenZFile = null;
			}
		} catch (IOException e) {
			ExceptionLogger.e(TAG, e.getMessage());
		}

	}

	/**
	 * Get the name of the resources in the CHM. Caches perform better when iterate
	 * the CHM using order of this returned list.
	 */
	public synchronized List<String> list() throws PasswordRequiredException {
		ExceptionLogger.d(TAG, "list " + filePath);
		if (resources == null) {
			resources = new ArrayList<String>();
			try {
				initStream();
				String name;
				if (zipFile != null) {
					List<FileHeader> fileHeaders = zipFile.getFileHeaders();
					for (FileHeader fh : fileHeaders) {
						if (fh.isFileNameUTF8Encoded()) {
							name = fh.getFileName();
						} else {
							name = new String(fh.getFileName().getBytes("CP437"), "utf-8");
						}
						resources.add(name);
						ExceptionLogger.d(TAG, name);
					}
//					final Enumeration<ZipArchiveEntry> iter = zipFile.getEntries();
//					ZipArchiveEntry entry;
//					while (iter.hasMoreElements()) {
//						entry = iter.nextElement();
//						if (!zipFile.canReadEntryData(entry)) {
//							ExceptionLogger.d(TAG, entry.getName() + " can't be read");
//							continue;
//						}
//						resources.add(entry.getName());
//						//ExceptionLogger.d(TAG, "list: " + entry.getName());
//					}
				} else if (ais != null) {
					ArchiveEntry entry = null;
					while ((entry = ais.getNextEntry()) != null) {
						//ExceptionLogger.d(TAG, entry.getName());
						if (!ais.canReadEntryData(entry)) {
							ExceptionLogger.d(TAG, entry.getName() + " can't be read");
							continue;
						}
						name = entry.getName();
						resources.add(name);
						ExceptionLogger.d(TAG, name);
					}
				} else if (sevenZFile != null) {
					final Iterable<SevenZArchiveEntry> iter = sevenZFile.getEntries();
					for (SevenZArchiveEntry entry : iter) {
						name = entry.getName();
						resources.add(name);
						ExceptionLogger.d(TAG, name);
					}
				} else if (cis != null) {
					name = dest7zFile.getName();
					resources.add(name.substring(0, name.lastIndexOf(".")));
					ExceptionLogger.d(TAG, name);
				}
				Collections.sort(resources, new NumberComparator());
				resources = Collections.unmodifiableList(resources); // protect the list, since the reference will be
			} catch (PasswordRequiredException t) {
				throw t;
			} catch (Throwable t) {
				ExceptionLogger.e(TAG, t);
			} finally {
				close();
			}
		}
		return resources;
	}

	/**
	 * Get an InputStream object for the named resource in the CHM.
	 * 
	 * @throws CompressorException
	 * @throws ArchiveException
	 */
	public synchronized InputStream getResourceAsStream(final String name) throws IOException, ArchiveException, CompressorException {
		ExceptionLogger.d(TAG, "getResourceAsStream name=" + name);
		if (name == null || name.trim().length() == 0) {
			return null;
		}
		initStream();
		if (zipFile != null) {
			final List<FileHeader> fileHeaders = zipFile.getFileHeaders();
			String fileName;
			FileHeader fileHeader = null;
			final String name1 = (name.startsWith("/") ? name.substring(1) : name);
			final String name2 = (name.startsWith("/") ? name : "/" + name);
			for (FileHeader fh : fileHeaders) {
				fileName = fh.getFileName();
				if (fh.isFileNameUTF8Encoded()) {
					if (name1.equalsIgnoreCase(fileName)
						|| name2.equalsIgnoreCase(fileName)) {
						fileHeader = fh;
						break;
					}
				} else {
					fileName = new String(fileName.getBytes("CP437"), "utf-8");
					if (name1.equalsIgnoreCase(fileName)
						|| name2.equalsIgnoreCase(fileName)) {
						fileHeader = fh;
						break;
					}
				}
			}
			if (fileHeader == null) {
				ExceptionLogger.d(TAG, name + " not found");
			} else {
				final ZipInputStream inputStream = zipFile.getInputStream(fileHeader);
				if (inputStream != null) {
					final int length = (int) fileHeader.getUncompressedSize();
					return new BufferedInputStream(new ZipIS(inputStream, length));
//					ZipArchiveEntry entry = zipFile.getEntry(name.startsWith("/")?name.substring(1):name);
//					//ExceptionLogger.d(TAG, "getResourceAsStream entry " + entry);
//					if ((entry != null && zipFile.canReadEntryData(entry))
//						|| ((entry = zipFile.getEntry(name.startsWith("/")?name:"/"+name)) != null && zipFile.canReadEntryData(entry))) {
//						return new BufferedInputStream(zipFile.getInputStream(entry));
//					}
				}
			}
			return null;
		} else if (ais != null) {
			ArchiveEntry entry = null;
			while ((entry = ais.getNextEntry()) != null) {
				//ExceptionLogger.d(TAG, entry.getName() + ", " + name);
				final String nam = entry.getName();
				if (name.equalsIgnoreCase(nam)
					|| name.equalsIgnoreCase("/"+nam)) {
					if (!ais.canReadEntryData(entry)) {
						ExceptionLogger.d(TAG, nam + " can't be read");
						close();
						return null;
					}
					return new BufferedInputStream(ais);
				}
			}
		} else if (sevenZFile != null) {
			SevenZArchiveEntry entry = null;
			while ((entry = sevenZFile.getNextEntry()) != null) {
				//ExceptionLogger.d(TAG, name + ", " + entry.getName() + " ok");
				final String nam = entry.getName();
				if (nam.equalsIgnoreCase(name)
					|| ("/"+nam).equalsIgnoreCase(name)) {
					final int length = (int) entry.getSize();
					ExceptionLogger.d(TAG, "found " + nam + ", length=" + Util.nf.format(length));
//					final byte[] content = new byte[length];
//					int read = 0;
//					while (read < length) {
//						read += sevenZFile.read(content, read, length - read);
//					}
					//return new ByteArrayInputStream(content);
					//return new BufferedInputStream(sevenZFile.getInputStream(e));
					return new BufferedInputStream(new SevenZInputStream(sevenZFile, length, dest7zFile.getName(), nam));
				}
			}
		} else {
			return cis;
		}
		ExceptionLogger.d(TAG, "can't getResourceAsStream entry " + name);
		close();
		return null;
	}

}

