package landau.sweb;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import java.util.regex.*;
import landau.sweb.*;
import net.gnu.common.*;

import landau.sweb.R;

public class LogArrayAdapter extends ArrayAdapter implements View.OnClickListener, View.OnLongClickListener {
	
	boolean showImages = false;
	MainActivity mainActivity;
	
	class Holder {
		TextView textView;
		ImageView imageView;
		Object item;
		Holder(View convertView) {
			imageView = (ImageView) convertView.findViewById(R.id.icon);
			imageView.setOnClickListener(LogArrayAdapter.this);
			convertView.setOnClickListener(LogArrayAdapter.this);

			imageView.setOnLongClickListener(LogArrayAdapter.this);
			convertView.setOnLongClickListener(LogArrayAdapter.this);

			imageView.setTag(this);
			convertView.setTag(this);

			textView = (TextView) convertView.findViewById(R.id.name);
			textView.setOnClickListener(LogArrayAdapter.this);
			textView.setOnLongClickListener(LogArrayAdapter.this);
			textView.setTag(this);
			textView.setTextColor(mainActivity.textColor);
			textView.setBackgroundColor(mainActivity.backgroundColor);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13.f);
		}
	}
	public LogArrayAdapter(MainActivity mainActivity, int resource, LinkedList objects) {
		super(mainActivity, resource, objects);
		this.mainActivity = mainActivity;
	}

	@Override
	public void onClick(View p1) {
		final Holder tag = (Holder) p1.getTag();
		final Object item = (tag).item;
		if (showImages)
			mainActivity.newForegroundTab(((DownloadInfo)item).savedPath, mainActivity.getCurrentTab().isIncognito, mainActivity.getCurrentTab());
		else
			mainActivity.newForegroundTab((String)item, mainActivity.getCurrentTab().isIncognito, mainActivity.getCurrentTab());
	}

	@Override
	public boolean onLongClick(View p1) {
		final Holder tag = (Holder) p1.getTag();
		final Object item = (tag).item;
		if (showImages) {
			AndroidUtils.copyClipboard(mainActivity, "Text", ((DownloadInfo)item).savedPath);
		} else {
			AndroidUtils.copyClipboard(mainActivity, "Text", (String)item);
		}
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			convertView = mainActivity.getLayoutInflater().inflate(R.layout.image, parent, false);
			holder = new Holder(convertView);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final ImageView imageView = holder.imageView;
		final TextView textView = holder.textView;
		holder.item = getItem(position);
		final String path;
		if (showImages) {
			path = ((DownloadInfo)holder.item).savedPath;
			final BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = false;

			final Bitmap bMap = BitmapFactory.decodeFile(path, opts);
			//ExceptionLogger.d(TAG, "holder.item " + item + ", " + show + ", bitmap " + bMap + ", barr.length " + barr.length);
			imageView.setImageBitmap(bMap);

			textView.setVisibility(View.GONE);
			imageView.setVisibility(View.VISIBLE);
		} else {
			path = (String)holder.item;
			textView.setText(path);
			textView.setVisibility(View.VISIBLE);
			imageView.setVisibility(View.GONE);
		}
		return convertView;
	}
	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				LogArrayAdapter.this.clear();
				if (results != null && results.values != null) {
					LogArrayAdapter.this.addAll((ArrayList<String>) results.values);
					if (results.count == 0 ) {
						LogArrayAdapter.this.notifyDataSetInvalidated();
					} else {
						LogArrayAdapter.this.notifyDataSetChanged();
					}
				}
			}
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				if (constraint != null && constraint.length() == 0) {
					constraint = mainActivity.getCurrentTab().recentConstraint;
				}
				mainActivity.getCurrentTab().recentConstraint = constraint;
				final FilterResults results = new FilterResults();
				final ArrayList<String> filteredArrayNames = new ArrayList<String>();
				if (constraint == null) {
					filteredArrayNames.addAll(mainActivity.getCurrentTab().requestsLog);
				} else {
					// perform your search here using the searchConstraint String.
					//constraint = constraint.toString().toLowerCase();
					final Pattern pattern = Pattern.compile(constraint.toString(), Pattern.CASE_INSENSITIVE);
					final Tab currentTab = mainActivity.getCurrentTab();
					for (String s : currentTab.requestsLog) {
						if (s != null && pattern.matcher(s).matches())  {
							filteredArrayNames.add(s);
						}
					}
				}
				results.count = filteredArrayNames.size();
				results.values = filteredArrayNames;
				//Log.e("VALUES", results.values.toString());
				return results;
			}
		};
		return filter;
	}
}
