package landau.sweb;

import android.content.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import java.util.*;
import landau.sweb.*;

public class ArrayAdapterWithCurrentItemClose<T> extends ArrayAdapter<T> implements View.OnClickListener {
	
	MainActivity mainActivity;
	
	class Holder {
		final ImageView currentView;
		final TextView titleView;
		final TextView addressView;
		final ImageView closeView;
		final LinearLayout ll;
		int pos;
		Holder(View convertView) {
			currentView = (ImageView) convertView.findViewById(R.id.current);
			titleView = (TextView) convertView.findViewById(R.id.title);
			addressView = (TextView) convertView.findViewById(R.id.address);
			closeView = (ImageView) convertView.findViewById(R.id.close);
			ll = (LinearLayout) convertView.findViewById(R.id.top);
			closeView.setOnClickListener(ArrayAdapterWithCurrentItemClose.this);
			convertView.setOnClickListener(ArrayAdapterWithCurrentItemClose.this);
			closeView.setTag(this);
			convertView.setTag(this);
		}
	}
	ArrayAdapterWithCurrentItemClose(final MainActivity mainActivity, final int resource, final List<T> objects) {//}, final int currentIndex) {
		super(mainActivity, resource, objects);
		this.mainActivity = mainActivity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			convertView = mainActivity.getLayoutInflater().inflate(R.layout.tab_item, parent, false);
			holder = new Holder(convertView);
		} else {
			holder = (Holder) convertView.getTag();
		}

		final TextView titleView = holder.titleView;
		final TextView addressView = holder.addressView;
		final ImageView currentView = holder.currentView;
		final WebView wv = mainActivity.tabs.get(position).webview;
		final LinearLayout ll = holder.ll;
		holder.pos = position;
		titleView.setText(wv.getTitle());
		addressView.setText(wv.getUrl());
		if (position == mainActivity.currentTabIndex) {
			ll.setBackgroundColor(0xffffffe8);
			titleView.setTextColor(0xff000000);
			addressView.setTextColor(0xff000000);
			//holder.closeView.setColorFilter(0xff000000, PorterDuff.Mode.SRC_IN);
		} else {
			ll.setBackgroundColor(0xff363636);
			titleView.setTextColor(0xfffffff0);
			addressView.setTextColor(0xfffffff0);
			//holder.closeView.setColorFilter(0xfffffff0, PorterDuff.Mode.SRC_IN);
		}
		if (mainActivity.tabs.get(position).previewImage != null) {
			currentView.setImageBitmap(mainActivity.tabs.get(position).previewImage);
		} else {
			currentView.setImageBitmap(mainActivity.tabs.get(position).favicon);
		}
		return convertView;
	}

	@Override
	public void onClick(View p1) {
		final Holder holder = (Holder) p1.getTag();
		final int pos = holder.pos;
		if (p1 == holder.closeView) {
			mainActivity.closeTab(mainActivity.tabs.get(pos).webview, pos, false);
		} else {
			mainActivity.switchToTab(pos);
			mainActivity.tabDialog.setVisibility(View.GONE);
		}
	}
}
