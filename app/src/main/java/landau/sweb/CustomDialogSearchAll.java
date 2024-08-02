package landau.sweb;

import android.app.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import landau.sweb.*;
import net.gnu.common.*;
import net.gnu.util.*;

import landau.sweb.R;
import net.gnu.common.ExceptionLogger;

public class CustomDialogSearchAll extends Dialog implements View.OnClickListener {

	private static final String TAG = "CustomDialogSearchAll";

	Activity mainActivity;
	Button search;
	ListView listView;
	EditText editText;
	ArrayAdapter adapter;
	ProgressBar searchProgress;
	ArrayList<String> searchResult;
	final Tab tab;

	public CustomDialogSearchAll(Activity activity, final Tab tab) {
		super(activity);
		// TODO Auto-generated constructor stub
		this.mainActivity = activity;
		this.tab = tab;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Search all");
		setContentView(R.layout.dialog_search_all);
		setCanceledOnTouchOutside(true);
		setCancelable(true);
		search = (Button) findViewById(R.id.btn_search);
		search.setOnClickListener(this);
		listView = (ListView) findViewById(R.id.list_result);
		editText = (EditText) findViewById(R.id.edit_search);
		searchProgress = (ProgressBar) findViewById(R.id.progressBar);
		searchProgress.setMax(tab.listSite.size() - 1);
		searchResult = new ArrayList<>();
		adapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_list_item_1, searchResult);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					tab.webview.loadUrl("file://" + tab.extractPath + "/" + searchResult.get(i));
					dismiss();
				}
			});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_search:
				if (editText.getText().toString().length() > 0)
					new AsyncTask<Void, Integer, Void>() {
						String textSearch;

						@Override
						protected void onPreExecute() {
							super.onPreExecute();
							searchProgress.setProgress(0);
							textSearch = editText.getText().toString();
						}

						@Override
						protected Void doInBackground(Void... voids) {
							for (int i = 1; i < tab.listSite.size(); i++) {
								if (searchDoc(tab, tab.listSite.get(i), textSearch)) {
									publishProgress(i, 1);
								} else {
									publishProgress(i, 0);
								}
							}
							return null;
						}

						@Override
						protected void onProgressUpdate(Integer... values) {
							super.onProgressUpdate(values);
							searchProgress.setProgress(values[0].intValue());
							if (values[1] == 1) {
								searchResult.add(tab.listSite.get(values[0]));
								adapter.notifyDataSetChanged();
								CustomDialogSearchAll.this.setTitle(searchResult.size() + " Results");
							}
						}

						@Override
						protected void onPostExecute(Void aVoid) {
							super.onPostExecute(aVoid);
							CustomDialogSearchAll.this.setTitle(searchResult.size() + " Results");
						}
					}.execute();
		}
	}

	private boolean searchDoc(final Tab tab, final String siteName, final String textSearch) {
		final StringBuilder reval = new StringBuilder();
		InputStream in = null;
		try {
			final InputStream resourceAsStream = tab.utils.chm.getResourceAsStream("/" + siteName);
			if (resourceAsStream == null) {
				return false;
			}
			in = new BufferedInputStream(resourceAsStream);
			final byte[] buf = new byte[1024 * 1024];
			int c;
			while ((c = in.read(buf)) >= 0) {
				reval.append(new String(buf, 0, c));
			}
		} catch (Throwable e) {
			ExceptionLogger.e(TAG, e.getMessage(), e);
		} finally {
			FileUtil.close(in);
		}

		//Document doc = Jsoup.parse(reval.toString());
//            if (doc.text().indexOf(textSearch) > 0)
//				return true;
//            else
//				return false;
		return Html.escapeHtml(reval.toString()).indexOf(textSearch) > 0;
	}
}
