package com.aisling.digitalcollections;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ais on 14/03/2017.
 *
 * as in SearchResultsAdapter
 *
 * Used as guide: https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
 */

public class FolderViewAdapter extends ArrayAdapter<Folder> {

    private int mLayout;
    private int mImageSize;
    private int mBackground;
    private QueryManager mQueryManager;


    public FolderViewAdapter(Context context, List<Folder> results, int layout, int imageSize, int background)
    {
        super(context, 0, results);
        mQueryManager = new QueryManager();
        mLayout = layout;
        mImageSize = imageSize;
        mBackground = background;

    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        Folder folder = getItem(position);
        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(mLayout,parent,false);
        }
        ImageView mImageView = (ImageView) convertView.findViewById(R.id.imageView);
        mImageView.setImageResource(folder.getFolderResource());
        /*if(mBackground==AppConstants.backGroundLight)
        {
            mImageView.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.background_place_holder_image_light));
        }
        else if (mBackground == AppConstants.backGroundLight){
            mImageView.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.background_place_holder_image_dark));
        }*/
        mImageView.setTag(String.valueOf(position));
        TextView mFolderName = (TextView) convertView.findViewById(R.id.titleTextView);
        mFolderName.setTextColor(Color.WHITE);
        mFolderName.setText(capitalize(folder.getFolderName()));

        //Getting the image, using first item in the folder to be the thumbnail image
        /*if(!folder.isEmpty())
        {
            GetThumbnailImage image = new GetThumbnailImage();
            image.updateInfoSyncTask(folder.getFirstElement(),mImageView,mQueryManager,mImageSize,(String) mImageView.getTag());
            image.execute();
        }*/


        return convertView;
    }

    private String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
}
