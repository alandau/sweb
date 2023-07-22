package net.gnu.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.LinkedList;

public class CopyingProcess {
	
	boolean skipAll = false;
	boolean overwriteAll = false;
	boolean autoRenameAll = false;
	boolean keepBothIfNotMatchAll = false;
	boolean newNameAll = false;
	boolean mergeAll = false;
	long copied = 0;
	final List<SourceInfo> sourceInfoList = new LinkedList<>();
	TreeSet<SourceInfo> sourceInfoSet = new TreeSet<>();
	
	public static void main(String[] args) throws Exception {
		CopyingProcess cp = new CopyingProcess();
		cp.add("/storage/emulated/0/tmp/geometerplus");
		cp.add("/storage/emulated/0/tmp/geometerplus/fbreader");
		cp.add("/storage/emulated/0/Books/MoonReader");
		cp.copy("/storage/emulated/0/.net.gnu.explorer");
	}
	
	public static class SourceInfo implements Serializable, Comparable<SourceInfo> {
		final File src;
		final String srcPath;
		final int parentSrcLength;
		
		public SourceInfo(final File src, final String parentSrcFolder) {
			this.src = src;
			srcPath = src.getAbsolutePath();
			this.parentSrcLength = parentSrcFolder.endsWith("/") ? parentSrcFolder.length()-1: parentSrcFolder.length();
		}

		@Override
		public int compareTo(final SourceInfo p1) {
			return srcPath.compareTo(p1.srcPath);
		}
	}
	
	public void add(final String srcPath) {
		final File srcFile = new File(srcPath);
		final String parentSrcFolder = srcFile.getParent();
		final List<File> files = FileUtil.getFiles(srcFile, true, null, null);
		for (File f : files) {
			sourceInfoSet.add(new SourceInfo(f, parentSrcFolder));
		}
	}
	
	public void copy(final String destFolder) {
		sourceInfoList.addAll(sourceInfoSet);
		sourceInfoSet = null;
		while (sourceInfoList.size() > 0) {
			final SourceInfo sourceInfo = sourceInfoList.remove(0);
			try {
				copy(sourceInfo, new File(destFolder));
			} catch (IOException ioe) {
				System.out.print("Copying has error: " + ioe.getMessage() + "\nContinue (y/n)?");
				final Scanner input = new Scanner(System.in);
				final String reply = input.next().trim();
				if (reply.equalsIgnoreCase("n")) {
					break;
				}
			}
		}
	}
	
	public void copy(final SourceInfo sourceInfo, final File destFolder) throws IOException {
		final File src = sourceInfo.src;
		final String srcPath = sourceInfo.srcPath;
		final int parentSrcLength = sourceInfo.parentSrcLength;
		final String destFilePath = destFolder.getAbsolutePath() + srcPath.substring(parentSrcLength);
		final File destFile = new File(destFilePath);
		final Scanner input = new Scanner(System.in);
		if (src.isFile()) {
			if (!destFile.exists()) {
				// destFile not exist
				FileUtil.copy(src, destFile);
			} else {
				// destFile existed
				if (destFile.isDirectory()) {
					System.out.print(destFilePath + " is a folder, remove to copy file " + srcPath + " (y/n)? ");
					final String reply = input.next().trim();
					if (reply.equalsIgnoreCase("y")) {
						FileUtil.deleteFolders(destFile, true, null, null);
						FileUtil.copy(src, destFile);
					}
				} else {
					// destFile is a File
					if (skipAll) {
					} else if (overwriteAll) {
						FileUtil.copy(src, destFile);
					} else if (autoRenameAll) {
						FileUtil.copyKeepBothAutoRename(src, destFile);
					} else if (newNameAll) {
						File file = null;
						final String parent = destFile.getParent();
						String reply = "";
						do {
							System.out.print("Duplicate, enter new name: ");
							reply = input.next().trim();
							file = new File(parent, reply);
						} while (file.exists() || reply.length() == 0);
						FileUtil.copy(src, file);
					} else if (keepBothIfNotMatchAll) {
						if (!FileUtil.compareFileContent(src, destFile)) {
							FileUtil.copyKeepBothAutoRename(src, destFile);
						}
					} else {
						//question
						System.out.print("Duplicate file " + destFilePath + ", Skip (S)? Overwrite (O)? AutoRename (A)? New Name (N)? Keep Both If Not Match (K)?\n"
										 + "   Skip All (Sa)? Overwrite All (Oa)? AutoRename All (Aa)? New Name All (Na)? Keep Both If Not Match All (Ka)?");
						final String reply = input.next().trim();
						if (reply.equalsIgnoreCase("s")
							|| reply.equalsIgnoreCase("sa")) {
							if (reply.equalsIgnoreCase("sa")) {
								skipAll = true;
							}
						} else if (reply.equalsIgnoreCase("o")
								   || reply.equalsIgnoreCase("oa")) {
							if (reply.equalsIgnoreCase("oa")) {
								overwriteAll = true;
							}
							FileUtil.copy(src, destFile);
						} else if (reply.equalsIgnoreCase("n")
								   || reply.equalsIgnoreCase("na")) {
							if (reply.equalsIgnoreCase("na")) {
								newNameAll = true;
							}
							File file = null;
							final String parent = destFile.getParent();
							String reply2 = "";
							do {
								System.out.print("Duplicate, enter new name: ");
								reply2 = input.next().trim();
								file = new File(parent, reply2);
							} while (file.exists() || reply2.length() == 0);
							FileUtil.copy(src, file);
						} else if (reply.equalsIgnoreCase("a")
								   || reply.equalsIgnoreCase("aa")) {
							if (reply.equalsIgnoreCase("aa")) {
								autoRenameAll = true;
							}
							FileUtil.copyKeepBothAutoRename(src, destFile);
						} else if (reply.equalsIgnoreCase("k")
								   || reply.equalsIgnoreCase("ka")) {
							if (reply.equalsIgnoreCase("ka")) {
								keepBothIfNotMatchAll = true;
							}
							if (!FileUtil.compareFileContent(src, destFile)) {
								FileUtil.copyKeepBothAutoRename(src, destFile);
							}
						}
					}
				}
			}
			copied += destFile.length();
		} else {
			// src is a folder
			if (!destFile.exists()) {
				// destFile not exist
				destFile.mkdir();
			} else {
				// destFile existed
				if (destFile.isFile()) {
					System.out.print(destFilePath + " is a file, remove to copy folder " + srcPath + " (y/n)? ");
					final String reply = input.next().trim();
					if (reply.equalsIgnoreCase("y")) {
						destFile.delete();
						destFile.mkdir();
					}
				} else {
					// destFile is a folder
					if (skipAll) {
						for (int i = sourceInfoList.size()-1; i >= 0; i--) {
							final SourceInfo si = sourceInfoList.get(i);
							if (si.src.getParent().startsWith(srcPath)) {
								sourceInfoList.remove(i);
							}
						}
					} else if (mergeAll) {
					} else {
						System.out.print("Duplicate folder " + destFilePath + ", Skip (S)? Merge (M)? Skip All (Sa)? Merge All (Ma)?");
						final String reply = input.next().trim();
						if (reply.equalsIgnoreCase("m")
							|| reply.equalsIgnoreCase("ma")) {
							if (reply.equalsIgnoreCase("ma")) {
								mergeAll = true;
							}
						} else if (reply.equalsIgnoreCase("s")
								   || reply.equalsIgnoreCase("sa")) {
							if (reply.equalsIgnoreCase("sa")) {
								skipAll = true;
							}
							for (int i = sourceInfoList.size()-1; i >= 0; i--) {
								final SourceInfo si = sourceInfoList.get(i);
								if (si.src.getParent().startsWith(srcPath)) {
									sourceInfoList.remove(i);
								}
							}
						}
					}
				}
			}

		}
	}
}
