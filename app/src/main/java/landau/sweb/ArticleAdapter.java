package landau.sweb;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.*;
import com.bumptech.glide.load.engine.*;
import java.util.*;

public class ArticleAdapter extends ArrayAdapter<Article> {
    private Context activity;
    private ArrayList<Article> listAticle;

    public ArticleAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
		super(context, resource, objects);
		this.activity = context;
        this.listAticle = (ArrayList<Article>) objects;
	}
	
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
		final ArticleHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item, parent,false);
			holder = new ArticleHolder(convertView);
		} else {
			holder = (ArticleHolder) convertView.getTag();
		}
		final Article article = listAticle.get(position);
        //holder.tvTitle.setText(article.getTitle());
        Glide.with(activity)
			.load(article.url)
			.asBitmap()
			.atMost()
			.diskCacheStrategy(DiskCacheStrategy.SOURCE)
			.animate(android.R.anim.fade_in)
			.approximate()
			.into(holder.imgThumnal);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					//activity.startActivity(new Intent(activity,DetailArticleActivity.class).putExtra("Article",article));
				}
			});
		return convertView;
    }

    @Override
    public int getCount() {
        return listAticle.size();
    }

    class ArticleHolder {
        private ImageView imgThumnal;
        private TextView tvTitle;
		private View itemView;
        public ArticleHolder(View itemView) {
            imgThumnal = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
			tvTitle.setTag(this);
			itemView.setTag(this);
			this.itemView = itemView;
        }
    }
}
