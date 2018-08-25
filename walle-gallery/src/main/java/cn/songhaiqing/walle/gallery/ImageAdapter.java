package cn.songhaiqing.walle.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<GalleryImage> galleryImages;
    private ImageOptions imageOptions;
    public ImageAdapter(Context context, List<GalleryImage> galleryImages){
        this.context = context;
        this.galleryImages = galleryImages;
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(100), DensityUtil.dip2px(100))
                // 图片缩放模式
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setAutoRotate(true)
                .build();
    }

    @Override
    public int getCount() {
        return galleryImages != null ? galleryImages.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return galleryImages != null ? galleryImages.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.walle_gallery_item_gallery,parent,false);
            holder = new ViewHolder();
            holder.imgvContent = convertView.findViewById(R.id.imgv_image);
            holder.cbChoose = convertView.findViewById(R.id.cb_choose);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        GalleryImage galleryImage = galleryImages.get(position);
        x.image().bind(holder.imgvContent, galleryImage.getOriginalPath(),imageOptions );
        return convertView;
    }
    class ViewHolder{
        ImageView imgvContent;
        CheckBox cbChoose;
    }
}
