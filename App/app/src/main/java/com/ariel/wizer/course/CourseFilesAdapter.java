package com.ariel.wizer.course;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizer.R;
import com.ariel.wizer.model.CourseFile;
import com.ariel.wizer.utils.DownloadFile;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ariel.wizer.utils.FileUtils.dirPath;
import static com.ariel.wizer.utils.FileUtils.openFile;

public class CourseFilesAdapter extends ArrayAdapter<CourseFile> {
    private Context mContext;
    private List<CourseFile> filesList;
    private boolean[] progressState;


    public CourseFilesAdapter(Activity context, ArrayList<CourseFile> list) {
        super(context, 0, list);
        mContext = context;
        filesList = list;
        progressState = new boolean[list.size()];
    }

    public List<CourseFile> getCoursesList() {
        return filesList;
    }

    public class MyHolder {
        TextView mFileName;
        TextView mFileDate;
        TextView progressInfo;
        Button btStart;
        ProgressBar progressBar;
        ImageButton menu;

        public MyHolder(View v) {
            mFileName = v.findViewById(R.id.file_name);
            mFileDate = v.findViewById(R.id.creation_date);
            progressInfo = v.findViewById(R.id.textViewProgress);
            btStart = v.findViewById(R.id.buttonStart);
            progressBar = v.findViewById(R.id.progressBar);
            menu = v.findViewById(R.id.feed_item_menu);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        MyHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.search_item_file, parent, false);
            holder = new MyHolder(view);
            view.setTag(holder);
            holder.progressBar.setTag(holder);
            holder.progressInfo.setTag(holder);

        } else {
            holder = (MyHolder) view.getTag();

        }

        if (progressState[position]) {
            holder.progressBar.setVisibility(View.VISIBLE);

        } else {
            holder.progressBar.setVisibility(View.GONE);
            holder.progressInfo.setText("");
        }

        holder.mFileName.setText(filesList.get(position).getName());
        //Date
        Date date = filesList.get(position).getCreation_date();
        if (date != null) {
            Format formatter = new SimpleDateFormat("dd/MM/yyyy");
            String s = formatter.format(date);
            holder.mFileDate.setText(s);
        }

        holder.btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = filesList.get(position).getUrl();
                String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());

                File file = new File(dirPath(mContext) + "/" + fileName);
                if (file.exists()) {
                    openFile(file, mContext);
                    return;
                }

                holder.progressBar.setVisibility(View.VISIBLE);
                holder.progressBar.setIndeterminate(true);
                holder.progressBar.getIndeterminateDrawable().setColorFilter(
                        Color.BLUE, PorterDuff.Mode.SRC_IN);

                PRDownloader.download(url, dirPath(mContext), fileName)
                        .build()
                        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                            @Override
                            public void onStartOrResume() {
                                progressState[position] = true;
                                holder.progressBar.setIndeterminate(false);
                            }
                        })
                        .setOnPauseListener(new OnPauseListener() {
                            @Override
                            public void onPause() {
                            }
                        })
                        .setOnCancelListener(new OnCancelListener() {
                            @Override
                            public void onCancel() {
                            }
                        })
                        .setOnProgressListener(new OnProgressListener() {
                            @Override
                            public void onProgress(Progress progress) {
                                long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                                holder.progressBar.setProgress((int) progressPercent);
                                holder.progressInfo.setText(DownloadFile.getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                            }
                        })
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {
                                holder.progressBar.setVisibility(View.GONE);
                                holder.progressInfo.setText("");
                                progressState[position] = false;
                                File file = new File(dirPath(mContext) + "/" + fileName);
                                openFile(file, mContext);
                            }

                            @Override
                            public void onError(Error error) {
                                Toast.makeText(mContext, "     Invalid File     ", Toast.LENGTH_LONG).show();
                                holder.progressInfo.setText("");
                                holder.progressBar.setProgress(0);
                                holder.progressBar.setIndeterminate(false);
                                holder.progressBar.setVisibility(View.GONE);
                                progressState[position] = false;
                            }
                        });
            }
        });
        try {
            holder.menu.setOnClickListener(v -> {
                switch (v.getId()) {
                    case R.id.feed_item_menu:
                        PopupMenu popup = new PopupMenu(mContext, v);
                        popup.getMenuInflater().inflate(R.menu.file_clipboard_popup,
                                popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(item -> {
                            switch (item.getItemId()) {
                                case R.id.report:
                                    Toast.makeText(mContext, " Report Clicked at position " + " : " + position, Toast.LENGTH_LONG).show();
                                    break;
                                case R.id.addtowishlist:
                                    Toast.makeText(mContext, "Delete File Clicked at position " + " : " + position, Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    break;
                            }
                            return true;
                        });
                        break;
                    default:
                        break;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}

