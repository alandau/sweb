package landau.sweb;

import java.util.*;

public class MenuAction {

	final String title;
	final int icon;
	final Runnable action;
	final MyBooleanSupplier getState;

	final static HashMap<String, MenuAction> actions = new HashMap<>();

	MenuAction(final String title, final int icon, final Runnable action) {
		this(title, icon, action, null);
	}

	MenuAction(final String title, final int icon, final Runnable action, MyBooleanSupplier getState) {
		this.title = title;
		this.icon = icon;
		this.action = action;
		this.getState = getState;
		actions.put(title, this);
	}

	@Override
	public String toString() {
		return title;
	}
}
