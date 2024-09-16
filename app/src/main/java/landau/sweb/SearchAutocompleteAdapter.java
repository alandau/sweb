package landau.sweb;

import android.annotation.*;
import android.content.*;
import android.graphics.drawable.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.webkit.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import org.json.*;

public class SearchAutocompleteAdapter extends BaseAdapter implements Filterable {

	interface OnSearchCommitListener {
		void onSearchCommit(String text);
	}

	private final Context mContext;
	private final OnSearchCommitListener commitListener;
	private List<String> completions = new ArrayList<>();

	SearchAutocompleteAdapter(Context context, OnSearchCommitListener commitListener) {
		mContext = context;
		this.commitListener = commitListener;
	}

	@Override
	public int getCount() {
		return completions.size();
	}

	@Override
	public Object getItem(int position) {
		return completions.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	@SuppressWarnings("ConstantConditions")
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
		}
		TextView v = (TextView) convertView.findViewById(android.R.id.text1);
		v.setText(completions.get(position));
		Drawable right = mContext.getResources().getDrawable(R.drawable.commit_search, null);
		final int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, mContext.getResources().getDisplayMetrics());
		right.setBounds(0, 0, size, size);
		v.setCompoundDrawables(null, null, right, null);
		//noinspection AndroidLintClickableViewAccessibility
		v.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v1, MotionEvent event) {
					if (event.getAction() != MotionEvent.ACTION_DOWN) {
						return false;
					}
					TextView t = (TextView) v1;
					if (event.getX() > t.getWidth() - t.getCompoundPaddingRight()) {
						commitListener.onSearchCommit(getItem(position).toString());
						return true;
					}
					return false;
				}});
		//noinspection AndroidLintClickableViewAccessibility
		parent.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View dropdown, MotionEvent event) {
					if (event.getX() > dropdown.getWidth() - size * 2) {
						return true;
					}
					return false;
				}});
		return convertView;
	}

	@Override
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				// Invoked on a worker thread
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					List<String> results = getCompletions(constraint.toString());
					filterResults.values = results;
					filterResults.count = results.size();
				}
				return filterResults;
			}

			@Override
			@SuppressWarnings("unchecked")
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results != null && results.count > 0) {
					completions = (List<String>) results.values;
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}
		};
	}

	// Runs on a worker thread
	private List<String> getCompletions(String text) {
		int total = 0;
		byte[] data = new byte[16384];
		try {
			URL url = new URL(URLUtil.composeSearchUrl(text, MainActivity.searchCompleteUrl, "%s"));
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				while (total <= data.length) {
					int count = in.read(data, total, data.length - total);
					if (count == -1) {
						break;
					}
					total += count;
				}
				if (total >= data.length) {
					// overflow
					return new ArrayList<String>();
				}
			} finally {
				urlConnection.disconnect();
			}
		} catch (IOException e) {
			// Swallow exception and return empty list
			return new ArrayList<String>();
		}

		// Result looks like:
		// [ "original query", ["completion1", "completion2", ...], ...]

		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(new String(data, StandardCharsets.ISO_8859_1));
		} catch (JSONException e) {
			return new ArrayList<String>();
		}
		jsonArray = jsonArray.optJSONArray(1);
		if (jsonArray == null) {
			return new ArrayList<String>();
		}
		final int MAX_RESULTS = 10;
		List<String> result = new ArrayList<>(Math.min(jsonArray.length(), MAX_RESULTS));
		for (int i = 0; i < jsonArray.length() && result.size() < MAX_RESULTS; i++) {
			String s = jsonArray.optString(i);
			if (s != null && !s.isEmpty()) {
				result.add(s);
			}
		}
		return result;
	}
}
