package net.gnu.epub;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import net.gnu.util.FileUtil;
import net.gnu.util.HtmlUtil;
import net.gnu.util.FileNumberComparator;
import net.gnu.common.ExceptionLogger;
import android.os.AsyncTask;
import android.os.PowerManager;
import java.util.regex.Pattern;
import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.List;
import java.util.Collections;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

public class BookCreateTask extends AsyncTask<Void, String, String> {

	private static final String TAG = "BookCreateTask";
	private PowerManager.WakeLock mWakeLock;

	private Pattern HTML_PATTERN = Pattern.compile(".*?\\.[xds]?ht(m?|ml)", Pattern.CASE_INSENSITIVE);
	private Pattern IMAGE_PATTERN = Pattern.compile(".*?\\.(jpe?g|gif|png|webp|pcx|bmp|tiff?|wmf|ico)", Pattern.CASE_INSENSITIVE);

	private final Activity activity;
	private final String sourceDirPath;
	private final String saveTo;
	private final String title;
	private final String author;
	private final String startPage;
	private final String coverImage;
	private final String include;
	private final String exclude;
	private final String replace;
	private final String by;
	private final TextView statusTv;
	private final boolean deleteSource;
	private Pattern[] replacePat;
	private String[] bys;
	
	public BookCreateTask(Activity activity, String sourceDirPath, String saveTo, String title, String author, String startPage, String coverImage, String include, String exclude, String replace, String by, TextView statusTv, boolean deleteSource) {
		this.activity = activity;
		this.sourceDirPath = sourceDirPath;
		this.saveTo = saveTo;
		this.title = title;
		this.author = author;
		this.startPage = startPage;
		this.coverImage = coverImage;
		this.include = include;
		this.exclude = exclude;
		this.replace = replace;
		this.by = by;
		this.statusTv = statusTv;
		this.deleteSource = deleteSource;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// take CPU lock to prevent CPU from going off if the user
		// presses the power button during download
		final PowerManager pm = (PowerManager) activity.getSystemService(Activity.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
								   getClass().getName());
		mWakeLock.acquire();
		//replace = replaceEt.getText().toString();
		String[] ss = replace.split("\n+");
		replacePat = new Pattern[ss.length];
		for (int i = 0; i < ss.length; i++) {
			replacePat[i] = Pattern.compile(ss[i], Pattern.CASE_INSENSITIVE);
		}
		//by = byEt.getText().toString();
		ss = by.split("\n+");
		bys = new String[replacePat.length];
		for (int i = 0; i < replacePat.length; i++) {
			if (i < ss.length) {
				bys[i] = ss[i];
			} else {
				bys[i] = "";
			}
		}
	}

	@Override
	protected void onPostExecute(final String result) {
		if (deleteSource) {
			final File fstart = new File(startPage);
			if (fstart.exists()) {
				fstart.delete();
			}
			FileUtil.deleteFolders(new File(sourceDirPath), true, null, null);
		}
		mWakeLock.release();
		if (result != null)
			Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
	}

	@Override
	protected String doInBackground(Void[] p1) {
		try {
			final Book book = new Book();

			//final String sourceDirPath = fileET.getText().toString();
			ExceptionLogger.d(TAG, "sourceDirPath " + sourceDirPath + ", startPage " + startPage);
			final File sourceDir = new File(sourceDirPath);
			final File fstart = new File(startPage);
			final boolean equalsIgnoreCase = sourceDir.getParent().equalsIgnoreCase(fstart.getParent());
			final int lengthDir = (equalsIgnoreCase ? sourceDir.getParent().length() : sourceDirPath.length()) + 1;
			if (lengthDir > 0 && sourceDir.exists()) {
				final Metadata metadata = book.getMetadata();

				//final String title = titleET.getText().toString().trim();
				ExceptionLogger.d(TAG, "title " + title);
				String epubName = "";
				if (title.length() > 0) {
					metadata.addTitle(title);
					epubName += title;
				}
				//final String author = authorET.getText().toString().trim();
				ExceptionLogger.d(TAG, "author " + author);
				if (author.length() > 0) {
					final int idx = author.lastIndexOf(" ");
					if (idx > 0) {
						metadata.addAuthor(new Author(author.substring(0, idx).trim(),
													  author.substring(idx).trim()));
					} else {
						metadata.addAuthor(new Author("", author));
					}
					if (epubName.length() > 0) {
						epubName += " - " + author;
					} else {
						epubName = author;
					}
				}

				//final String startPage = startPageET.getText().toString().trim();
				if (startPage.length() > 0 && fstart.exists() && fstart.isFile()) {
//						final String[] readFileByMetaTag = FileUtil.readFileByMetaTag(fstart);
//						final String doc = readFileByMetaTag[0];//Jsoup.parse(ff, null);
//						final String htmlTitle = HtmlUtil.getTagValue("title", doc).trim();//doc.title();
//						if (htmlTitle != null && htmlTitle.length() > 0) {
//							book.addSection(htmlTitle,
//										getResource(sourceDirPath, startPage, lengthDir));
//						} else {
//							book.addSection(fstart.getName(),
//											getResource(sourceDirPath, startPage, lengthDir));
//						}
					add(book, fstart, startPage, fstart.getName(), lengthDir);
				}
				//final String coverImage = coverImageET.getText().toString().trim();
				ExceptionLogger.d(TAG, "coverImage " + coverImage);
				if (coverImage.length() > 0 && new File(coverImage).exists() && IMAGE_PATTERN.matcher(coverImage).matches()) {
					book.setCoverImage(getResource(sourceDirPath, coverImage, lengthDir));
				}
				Pattern includePat = null, excludePat = null;
				//final String include = includeET.getText().toString().trim();
				//final String exclude = excludeET.getText().toString().trim();
				if (include.length() > 0) {
					includePat = Pattern.compile(include, Pattern.CASE_INSENSITIVE);
				}
				if (exclude.length() > 0) {
					excludePat = Pattern.compile(exclude, Pattern.CASE_INSENSITIVE);
				}
				final List<File> fs = FileUtil.getFiles(sourceDir, false, includePat, excludePat);
				Collections.sort(fs, new FileNumberComparator());
				String name;
				String filePath;
				for (File ff : fs) {
					filePath = ff.getAbsolutePath();
					ExceptionLogger.d(TAG, "filePath " + filePath);
					if (!startPage.equalsIgnoreCase(filePath)
						&& (!coverImage.equalsIgnoreCase(filePath) || !IMAGE_PATTERN.matcher(coverImage).matches())) {
						name = ff.getName();
						if (HTML_PATTERN.matcher(name).matches()) {
							add(book, ff, filePath, name, lengthDir);
						} else {
							book.getResources().add(
								getResource(sourceDirPath, filePath, lengthDir));
						}
					}
				}
				//final String saveTo = saveToET.getText().toString();
				if (saveTo.length() > 0) {
					final File file = new File(saveTo);
					if (file.exists()) {
						if (file.isDirectory()) {
							if (epubName.length() > 0) {
								name = file.getAbsolutePath() + "/" + epubName + ".epub";
							} else {
								name = file.getAbsolutePath() + "/" + sourceDir.getName() + ".epub";
							}
						} else {
							name = file.getAbsolutePath();
							if (!name.toLowerCase().endsWith(".epub")) {
								name = name + ".epub";
							}
						}
					} else {
						file.getParentFile().mkdirs();
						name = file.getAbsolutePath();
						if (!name.toLowerCase().endsWith(".epub")) {
							name = name + ".epub";
						}
					}
				} else {
					if (fstart.exists()) {
						name = fstart.getAbsolutePath() + ".epub";
					} else if (sourceDir.isDirectory()) {
						name = sourceDir.getParentFile().getAbsolutePath() + "/" + sourceDir.getName() + ".epub";
					} else {
						name = sourceDirPath;
						if (!name.toLowerCase().endsWith(".epub")) {
							name = name + ".epub";
						}
					}
				}
				ExceptionLogger.d(TAG, name);
				final EpubWriter epubWriter = new EpubWriter();
				epubWriter.write(book, new BufferedOutputStream(new FileOutputStream(name)));
				return name + " created";
			} else {
				return "No file to create epub";
			}
		} catch (Throwable e) {
			publishProgress(e.getMessage());
			ExceptionLogger.e(TAG, e);
			return "Error " + e.getMessage();
		}
	}

	private void add(final Book book, final File ff, final String filePath, final String name, final int lengthDir) throws IOException {
		final String[] readFileByMetaTag = FileUtil.readFileByMetaTag(ff);
		String doc = readFileByMetaTag[0];//Jsoup.parse(ff, null);
		final String htmlTitle = HtmlUtil.getTagValue("title", doc).trim();//doc.title();
		//ExceptionLogger.d(TAG, "htmlTitle " + htmlTitle);
		publishProgress(filePath);
		if (replace.length() > 0) {
			for (int i = 0; i < bys.length; i++) {
				doc = replacePat[i].matcher(doc).replaceAll(bys[i]);
			}
		}
		final InputStream is = new ByteArrayInputStream(doc.getBytes(readFileByMetaTag[1]));
		final Resource resource = new Resource(is, filePath.substring(lengthDir));
		if (htmlTitle != null && htmlTitle.length() > 0) {
			book.addSection(htmlTitle,// + "_" + name,
							//getResource(sourceDirPath, filePath, lengthDir, is)
							resource);
		} else {
			book.addSection(name,
							//getResource(sourceDirPath, filePath, lengthDir, is)
							resource);
		}
	}

	@Override
	protected void onProgressUpdate(String[] values) {
		super.onProgressUpdate(values);
		if (statusTv != null) {
			statusTv.setText("adding " + values[0]);
		}
		//AndroidUtils.toast(EPUBActivity.this, values[0] + "");
	}

	private Resource getResource(final String sourceDirPath, final String path, final int lengthDir, final BufferedInputStream bis) throws FileNotFoundException, IOException {
		//ExceptionLogger.d(TAG, "getResource " + path + ", " + path.substring(lengthDir));
		if (path.startsWith(sourceDirPath)) {
			return new Resource(bis, path.substring(lengthDir));
		} else {
			return new Resource(bis, new File(path).getName());
		}
	}

	private Resource getResource(final String sourceDirPath, final String path, final int lengthDir) throws FileNotFoundException, IOException {
		//ExceptionLogger.d(TAG, "getResource sourceDirPath " + sourceDirPath + ", path " + path + ", " + path.substring(lengthDir));
		final File file = new File(path);
		final FileInputStream fis = new FileInputStream(file);
		final BufferedInputStream bis = new BufferedInputStream(fis);
		if (path.startsWith(sourceDirPath)) {
			return new Resource(bis, path.substring(lengthDir));
		} else {
			return new Resource(bis, new File(path).getName());
		}
	}


}
