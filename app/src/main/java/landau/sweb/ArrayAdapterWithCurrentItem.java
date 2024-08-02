package landau.sweb;

import android.graphics.drawable.*;
import android.util.*;
import android.view.*;
import android.widget.*;

public class ArrayAdapterWithCurrentItem<T> extends ArrayAdapter<T> {
	
	int currentIndex;
	
	ArrayAdapterWithCurrentItem(MainActivity context, int resource, T[] objects, int currentIndex) {
		super(context, resource, objects);
		this.currentIndex = currentIndex;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		TextView textView = (TextView) view.findViewById(android.R.id.text1);
		int icon = position == currentIndex ? android.R.drawable.ic_menu_mylocation : R.drawable.empty;
		Drawable d = getContext().getResources().getDrawable(icon, null);
		int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getContext().getResources().getDisplayMetrics());
		d.setBounds(0, 0, size, size);
		textView.setCompoundDrawablesRelative(d, null, null, null);
		textView.setCompoundDrawablePadding(
			(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getContext().getResources().getDisplayMetrics()));
		return view;
	}
}
