package landau.sweb;

import android.app.*;
import android.widget.*;
import android.os.*;
import android.view.*;
import java.net.*;

public class OfflineDialogBookmark extends Dialog implements View.OnClickListener {

	Activity mainActivity;
	Button add;
	Button close;
	ListView listView;
	ArrayAdapter adapter;
	final Tab tab;
	
	public OfflineDialogBookmark(Activity activity, final Tab tab) {
		super(activity);
		this.mainActivity = activity;
		this.tab = tab;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("List bookmarks");
		setContentView(R.layout.dialog_bookmark);
		setCanceledOnTouchOutside(true);
		setCancelable(true);
		add = (Button) findViewById(R.id.btn_addbookmark);
		add.setOnClickListener(this);
		close = (Button) findViewById(R.id.btn_close);
		close.setOnClickListener(this);
		listView = (ListView) findViewById(R.id.listView);
		adapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_list_item_1, tab.listBookmark);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					tab.webview.loadUrl("file://" + tab.extractPath + "/" + tab.listBookmark.get(i));
					dismiss();
				}
			});
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4) {
					tab.listBookmark.remove(tab.listBookmark.get(p3));
					adapter.notifyDataSetChanged();
					return false;
				}
			});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_addbookmark:
				String url = URLDecoder.decode(tab.webview.getUrl()).substring(("file://" + tab.extractPath).length() + 1);
				if (tab.listBookmark.indexOf(url) == -1) {
					tab.listBookmark.add(url);
					adapter.notifyDataSetChanged();
				} else {
					Toast.makeText(mainActivity, "Bookmark already exist", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btn_close:
				tab.offlineBookmarkDialog.dismiss();
				break;
		}
	}
}
