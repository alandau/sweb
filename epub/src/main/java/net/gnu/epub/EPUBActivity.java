package net.gnu.epub;

import net.gnu.common.ParentActivity;
import net.gnu.common.ExceptionLogger;
import net.gnu.common.AndroidUtils;
import java.io.File;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Bundle;
import android.os.Build;
import android.content.Intent;
import android.view.View;

public class EPUBActivity extends ParentActivity {
	
	private static final String TAG = "EPUBActivity";
	
	private EditText coverImageET, titleET, authorET, fileET, saveToET, startPageET, includeET, excludeET,
	replaceEt, byEt;
	
	private TextView statusTv;
	
	private static final int PICK_REQUEST_CODE = 1;
	private static final int PICK_SAVE_REQUEST_CODE = 2;
	private static final int PICK_COVER_REQUEST_CODE = 3;
	private static final int PICK_START_REQUEST_CODE = 4;
	
	@Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
		setContentView(R.layout.epub_activity_main);
		
		fileET = (EditText) findViewById(R.id.files);
		coverImageET = (EditText) findViewById(R.id.coverFiles);
		titleET = (EditText) findViewById(R.id.title);
        authorET = (EditText) findViewById(R.id.author);
		saveToET = (EditText) findViewById(R.id.saveTo);
		startPageET = (EditText) findViewById(R.id.startPage);
		includeET = (EditText) findViewById(R.id.include);
		excludeET = (EditText) findViewById(R.id.exclude);
		replaceEt = (EditText) findViewById(R.id.replaceEt);
		byEt = (EditText) findViewById(R.id.byEt);
		statusTv = (TextView) findViewById(R.id.statusTv);
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
	
	public void createBook(View p1) {
		AndroidUtils.toast(EPUBActivity.this, "Started");
		new BookCreateTask(this,
						   fileET.getText().toString(),
						   saveToET.getText().toString(),
						   titleET.getText().toString(),
						   authorET.getText().toString(),
						   startPageET.getText().toString(),
						   coverImageET.getText().toString(),
						   includeET.getText().toString(),
						   excludeET.getText().toString(),
						   replaceEt.getText().toString(),
						   byEt.getText().toString(),
						   statusTv, false).execute();
	}
	
	protected void onActivityResult (int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
			case PICK_REQUEST_CODE:
				if (resultCode == RESULT_OK) {
					fileET.setText(AndroidUtils.getFile(EPUBActivity.this, intent));
				}
				break;
			case PICK_SAVE_REQUEST_CODE: 
				if (resultCode == RESULT_OK) {
					saveToET.setText(AndroidUtils.getFile(EPUBActivity.this, intent));
				}
				break;
			case PICK_COVER_REQUEST_CODE: 
				if (resultCode == RESULT_OK) {
					coverImageET.setText(AndroidUtils.getFile(EPUBActivity.this, intent));
				}
				break;
			case PICK_START_REQUEST_CODE: 
				if (resultCode == RESULT_OK) {
					startPageET.setText(AndroidUtils.getFile(EPUBActivity.this, intent));
				}
				break;
		}
	}
}


