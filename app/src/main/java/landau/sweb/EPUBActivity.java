package landau.sweb;

import android.app.*;
import android.content.*;
import android.database.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import landau.sweb.utils.*;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.*;
import android.webkit.*;

public class EPUBActivity extends Activity {
	
	private static final String TAG = "EPUBActivity";
	
	public static File externalLogFilesDir;
	private Pattern HTML_PATTERN = Pattern.compile(".*?\\.[xds]?ht(m?|ml)", Pattern.CASE_INSENSITIVE);
	private Pattern IMAGE_PATTERN = Pattern.compile(".*?\\.(jpe?g|gif|png|webp|pcx|bmp|tiff?|wmf|ico)", Pattern.CASE_INSENSITIVE);
	
	private EditText coverImageET, titleET, authorET, fileET, saveToET, startPageET, includeET, excludeET;

	private static final int PICK_REQUEST_CODE = 1;
	private static final int PICK_SAVE_REQUEST_CODE = 2;
	private static final int PICK_COVER_REQUEST_CODE = 3;
	private static final int PICK_START_REQUEST_CODE = 4;
	
	@Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		externalLogFilesDir = getExternalFilesDir("logs");
		MainActivity.externalLogFilesDir = externalLogFilesDir;
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
				private Thread.UncaughtExceptionHandler defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
				@Override
				public void uncaughtException(final Thread t, final Throwable e) {
					ExceptionLogger.e(TAG, e);
					defaultUEH.uncaughtException(t, e);
				}
			});
			
		setContentView(R.layout.epub_activity_main);
		
		fileET = (EditText) findViewById(R.id.files);
		coverImageET = (EditText) findViewById(R.id.coverFiles);
		titleET = (EditText) findViewById(R.id.title);
        authorET = (EditText) findViewById(R.id.author);
		saveToET = (EditText) findViewById(R.id.saveTo);
		startPageET = (EditText) findViewById(R.id.startPage);
		includeET = (EditText) findViewById(R.id.include);
		excludeET = (EditText) findViewById(R.id.exclude);
		
	}
	
	public void fileBtn(View p1) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
			i.addCategory(Intent.CATEGORY_DEFAULT);
			i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			startActivityForResult(Intent.createChooser(i, "Choose directory"), PICK_REQUEST_CODE);
		}
	}

	public void saveToBtn(View p1) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
			i.addCategory(Intent.CATEGORY_DEFAULT);
			i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			startActivityForResult(Intent.createChooser(i, "Choose directory"), PICK_SAVE_REQUEST_CODE);
		}
	}
	
	public void coverFilesBtn(View p1) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
			i.addCategory(Intent.CATEGORY_DEFAULT);
			i.setType("*/*");
			coverImageET.requestFocus();
			startActivityForResult(Intent.createChooser(i, "Choose directory"), PICK_COVER_REQUEST_CODE);
		}
	}
	
	public void startPageBtn(View p1) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
			i.addCategory(Intent.CATEGORY_DEFAULT);
			i.setType("*/*");
			startPageET.requestFocus();
			startActivityForResult(Intent.createChooser(i, "Choose directory"), PICK_START_REQUEST_CODE);
		}
	}

	private class BookCreateTask extends AsyncTask<Void, String, Void> {
		@Override
		protected Void doInBackground(Void[] p1) {
			try {
				final Book book = new Book();

				final String path = fileET.getText().toString().trim();
				final File f = new File(path);
				int lengthDir = path.length();
				if (lengthDir > 0 && f.exists()) {
					if (f.isDirectory()) {
						if (!path.endsWith("/")) {
							lengthDir++;
						}
					} else {
						lengthDir = f.getParent().length();
					}
					final Metadata metadata = book.getMetadata();
					
					final String title = titleET.getText().toString().trim();
					ExceptionLogger.d("title", title);
					String epubName = "";
					if (title.length() > 0) {
						metadata.addTitle(title);
						epubName += title;
					}
					final String author = authorET.getText().toString().trim();
					ExceptionLogger.d("author", author);
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

					final String startPage = startPageET.getText().toString().trim();
					ExceptionLogger.d("startPage", startPage);
					final File fstart = new File(startPage);
					if (startPage.length() > 0 && fstart.exists() && fstart.isFile()) {
						book.addSection(fstart.getName(),
										getResource(startPage, lengthDir));
					}
					final String coverImage = coverImageET.getText().toString().trim();
					ExceptionLogger.d("coverImage", coverImage);
					if (coverImage.length() > 0 && new File(coverImage).exists() && IMAGE_PATTERN.matcher(coverImage).matches()) {
						book.setCoverImage(getResource(coverImage, lengthDir));
					}
					Pattern includePat = null, excludePat = null;
					final String include = includeET.getText().toString().trim();
					final String exclude = excludeET.getText().toString().trim();
					if (include.length() > 0) {
						includePat = Pattern.compile(include, Pattern.CASE_INSENSITIVE);
					}
					if (exclude.length() > 0) {
						excludePat = Pattern.compile(exclude, Pattern.CASE_INSENSITIVE);
					}
					final Pattern numPat = Pattern.compile(".*?(\\d+).*?");
					final List<File> fs = FileUtil.getFiles(f, includePat, excludePat);
					Collections.sort(fs, new Comparator<File>() {
							@Override
							public int compare(final File file1, final File file2) {
								final Matcher matcher1 = numPat.matcher(file1.getName());
								final Matcher matcher2 = numPat.matcher(file2.getName());
								if (matcher1.matches() && matcher2.matches()) {
									final Integer valueOf1 = Integer.valueOf(matcher1.group(1));
									final Integer valueOf2 = Integer.valueOf(matcher2.group(1));
									if (valueOf1 != valueOf2)
										return valueOf1 - valueOf2;
								}
								return file1.getAbsolutePath().compareToIgnoreCase(file2.getAbsolutePath());
							}
						});
					String name;
					String doc;
					String htmlTitle;
					String filePath;
					for (File ff : fs) {
						filePath = ff.getAbsolutePath();
						ExceptionLogger.d(TAG, "filePath " + filePath);
						if (!startPage.equalsIgnoreCase(filePath)
							&& (!coverImage.equalsIgnoreCase(filePath) || !IMAGE_PATTERN.matcher(coverImage).matches())) {
							name = ff.getName();
							if (HTML_PATTERN.matcher(name).matches()) {
								doc = FileUtil.readFileByMetaTag(ff);//Jsoup.parse(ff, null);
								htmlTitle = HtmlUtil.getTagValue("title", doc).trim();//doc.title();
								ExceptionLogger.d("htmlTitle ", htmlTitle);

								if (htmlTitle != null && htmlTitle.length() > 0) {
									book.addSection(htmlTitle,// + "_" + name,
													getResource(filePath, lengthDir));
								} else {
									book.addSection(name,
													getResource(filePath, lengthDir));
								}
							} else {
								book.getResources().add(
									getResource(filePath, lengthDir));
							}
						}
					}
					final String saveTo = saveToET.getText().toString();
					if (saveTo.length() > 0) {
						final File file = new File(saveTo);
						if (file.exists()) {
							if (file.isDirectory()) {
								if (epubName.length() > 0) {
									name = file.getAbsolutePath() + "/" + epubName + ".epub";
								} else {
									name = file.getAbsolutePath() + "/" + f.getName() + ".epub";
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
						if (f.isDirectory()) {
							name = f.getAbsolutePath() + "/" + f.getName() + ".epub";
						} else {
							name = path;
							if (!name.toLowerCase().endsWith(".epub")) {
								name = name + ".epub";
							}
						}
					}
					ExceptionLogger.d(TAG, name);
					final EpubWriter epubWriter = new EpubWriter();
					epubWriter.write(book, new BufferedOutputStream(new FileOutputStream(name)));
					publishProgress(name + " created");
				} else {
					publishProgress("No file to create epub");
				}
			} catch (Throwable e) {
				publishProgress(e.getMessage());
				ExceptionLogger.e(TAG, e);
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String[] values) {
			super.onProgressUpdate(values);
			AndroidUtils.toast(EPUBActivity.this, values[0] + "");
		}
	}
	
	public void createBook(View p1) {
		new BookCreateTask().execute();
	}

	private Resource getResource(final String path, final int lengthDir) throws FileNotFoundException, IOException {
		//ExceptionLogger.d(TAG, "getResource " + path + ", " + path.substring(lengthDir));
		final FileInputStream fis = new FileInputStream(path);
		final BufferedInputStream bis = new BufferedInputStream(fis);
		return new Resource(bis, path.substring(lengthDir));
	}

	public static String getExtension(final String fName) {
		int lastIndexOf = fName.lastIndexOf(".");
		if (lastIndexOf >= 0)
			return fName.substring(++lastIndexOf).toLowerCase();
		return "";
	}
	
	protected void onActivityResult (int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
			case PICK_REQUEST_CODE:
				if (resultCode == RESULT_OK) {
					fileET.setText(getFile(intent));
				}
				break;
			case PICK_SAVE_REQUEST_CODE: 
				if (resultCode == RESULT_OK) {
					saveToET.setText(getFile(intent));
				}
				break;
			case PICK_COVER_REQUEST_CODE: 
				if (resultCode == RESULT_OK) {
					coverImageET.setText(getFile(intent));
				}
				break;
			case PICK_START_REQUEST_CODE: 
				if (resultCode == RESULT_OK) {
					startPageET.setText(getFile(intent));
				}
				break;
		}
	}

	private String getFile(Intent intent) {
		Uri uri = intent.getData();
		String type = intent.getType();
		ExceptionLogger.d(TAG, "Pick completed: " + uri + " " + type);
		String path = "";
		if (uri != null) {
			path = uri.toString();
			if (path.toLowerCase().startsWith("file://")) {
// Selected file/directory path is below
				path = (new File(URI.create(path))).getAbsolutePath();
			} else {
				path = AndroidUtils.getPath(EPUBActivity.this, uri);
			}
		} else {
		}
		return path;
	}
	
	public String getRealPathFromURI(Uri contentUri) {
		// can post image
		String [] proj ={MediaStore.Files.FileColumns.DATA};
		Cursor cursor = getContentResolver().query(contentUri,
									  proj, // Which columns to return
									  null, // WHERE clause; which rows to return (all rows)
									  null, // WHERE clause selection arguments (none)
									  null);// Order-by clause (ascending by name)
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
}
