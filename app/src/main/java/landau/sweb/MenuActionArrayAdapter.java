package landau.sweb;

import android.content.*;
import android.graphics.drawable.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import landau.sweb.*;

public class MenuActionArrayAdapter extends ArrayAdapter<MenuAction> implements View.OnClickListener {
	DisplayMetrics m;
	int size;
	class Holder {
		final TextView textView;
		MenuAction item;
		Holder(View convertView) {
			textView = (TextView) convertView.findViewById(android.R.id.text1);
			textView.setOnClickListener(MenuActionArrayAdapter.this);
			convertView.setOnClickListener(MenuActionArrayAdapter.this);
			textView.setTag(this);
			convertView.setTag(this);
		}
	}
	@Override
	public void onClick(View p1) {
		Holder tag = (Holder) p1.getTag();
		final MenuAction item = (tag).item;
		item.action.run();
		Drawable right = null;
		if (item.getState != null) {
			int icon = item.getState.getAsBoolean() ? android.R.drawable.checkbox_on_background :
				android.R.drawable.checkbox_off_background;
			right = getContext().getResources().getDrawable(icon, null);
			right.setBounds(0, 0, size, size);
		}

		tag.textView.setCompoundDrawables(tag.textView.getCompoundDrawables()[0], null, right, null);
		tag.textView.setCompoundDrawablePadding(
			(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, m));
		MenuActionArrayAdapter.this.notifyDataSetChanged();
	}

	MenuActionArrayAdapter(Context context, int resource, ArrayList<MenuAction> objects) {
		super(context, resource, objects);
		m = getContext().getResources().getDisplayMetrics();
		size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, m);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			convertView = super.getView(position, convertView, parent);//getLayoutInflater().inflate(R.layout.list_item, parent, false);
			holder = new Holder(convertView);
		} else {
			holder = (Holder) convertView.getTag();
		}

		final TextView textView = holder.textView;
		final MenuAction item = getItem(position);
		assert item != null;
		holder.item = item;
		textView.setText(item.title);
//			View view = super.getView(position, convertView, parent);
//			TextView textView = (TextView) view.findViewById(android.R.id.text1);

		Drawable left = getContext().getResources().getDrawable(item.icon != 0 ? item.icon : R.drawable.empty, null);
		left.setBounds(0, 0, size, size);
		left.setTint(0xffeeeeee);

		Drawable right = null;
		if (item.getState != null) {
			int icon = item.getState.getAsBoolean() ? android.R.drawable.checkbox_on_background :
				android.R.drawable.checkbox_off_background;
			right = getContext().getResources().getDrawable(icon, null);
			right.setBounds(0, 0, size, size);
		}

		textView.setCompoundDrawables(left, null, right, null);
		textView.setCompoundDrawablePadding(
			(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, m));

		return convertView;
	}
}
