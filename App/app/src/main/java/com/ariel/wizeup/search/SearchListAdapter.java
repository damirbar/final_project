package com.ariel.wizeup.search;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Course;
import com.ariel.wizeup.model.CourseFile;
import com.ariel.wizeup.model.Session;
import com.ariel.wizeup.model.User;
import com.ariel.wizeup.file.DownloadFile;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ariel.wizeup.file.OpenFile.dirPath;
import static com.ariel.wizeup.file.OpenFile.openFile;

public class SearchListAdapter extends ArrayAdapter<Object> {
    private Context mContext;
    private List<Object> searchList;
    private boolean[] progressState;


    public SearchListAdapter(Activity context, ArrayList<Object> list) {
        super(context, 0, list);
        mContext = context;
        searchList = list;
        progressState = new boolean[list.size()];

    }

    public List<Object> getSearchList() {
        return searchList;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        Object a = (Object) getItem(position);

        if (a instanceof User) {
            User user = (User) a;

            listItem = LayoutInflater.from(mContext).inflate(R.layout.search_item_user, parent, false);
            TextView mName = (TextView) listItem.findViewById(R.id.user_name);
            ImageView profileImage = (ImageView) listItem.findViewById(R.id.user_image);
            ;


            String disName = user.getDisplay_name();
            if (disName != null && !(disName.isEmpty())) {
                mName.setText(user.getDisplay_name());
            } else {
                mName.setText(user.getFname() + " " + user.getLname());
            }

            String pic = user.getProfile_img();
            if (pic != null && !(pic.isEmpty()))
                Picasso.with(mContext)
                        .load(pic)
                        .error(R.drawable.default_user_image)
                        .into(profileImage);

        }

        if (a instanceof Session) {
            Session session = (Session) a;

            listItem = LayoutInflater.from(mContext).inflate(R.layout.search_item_session, parent, false);
            TextView mName = (TextView) listItem.findViewById(R.id.sid_name);
            TextView mDate = (TextView) listItem.findViewById(R.id.creation_date);
            mName.setText(session.getName());


            //Date
            Date date = session.getCreation_date();
            if (date != null) {
                Format formatter = new SimpleDateFormat("dd/MM/yyyy");
                String s = formatter.format(date);
                mDate.setText(s);
            }

        }

        if (a instanceof CourseFile) {
            CourseFile file = (CourseFile) a;
            PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                    .setDatabaseEnabled(true)
                    .build();
            PRDownloader.initialize(mContext, config);

            listItem = LayoutInflater.from(mContext).inflate(R.layout.search_item_file, parent, false);
            TextView mName = (TextView) listItem.findViewById(R.id.file_name);
            TextView mDate = (TextView) listItem.findViewById(R.id.creation_date);
            TextView progressInfo = (TextView) listItem.findViewById(R.id.textViewProgress);
            Button btStart = (Button) listItem.findViewById(R.id.buttonStart);
            ProgressBar progressBar = (ProgressBar) listItem.findViewById(R.id.progressBar);
            ImageButton menu = listItem.findViewById(R.id.feed_item_menu);

            mName.setText(file.getName());

            //Date
            Date date = file.getCreation_date();
            if (date != null) {
                Format formatter = new SimpleDateFormat("dd/MM/yyyy");
                String s = formatter.format(date);
                mDate.setText(s);
            }

            if (progressState[position]) {
                progressBar.setVisibility(View.VISIBLE);

            } else {
                progressBar.setVisibility(View.GONE);
                progressInfo.setText("");
            }


            btStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = file.getUrl();
                    String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());

                    File file = new File(dirPath(mContext) + "/" + fileName);
                    if (file.exists()) {
                        openFile(file, mContext);
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    progressBar.getIndeterminateDrawable().setColorFilter(
                            Color.BLUE, PorterDuff.Mode.SRC_IN);

                    PRDownloader.download(url, dirPath(mContext), fileName)
                            .build()
                            .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                @Override
                                public void onStartOrResume() {
//                                    progressState[position] = true;
                                    progressBar.setIndeterminate(false);
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
                                    progressBar.setProgress((int) progressPercent);
                                    progressInfo.setText(DownloadFile.getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                                }
                            })
                            .start(new OnDownloadListener() {
                                @Override
                                public void onDownloadComplete() {
                                    progressBar.setVisibility(View.GONE);
                                    progressInfo.setText("");
                                    progressState[position] = false;
                                    File file = new File(dirPath(mContext) + "/" + fileName);
                                    openFile(file, mContext);
                                }

                                @Override
                                public void onError(Error error) {
                                    Toast.makeText(mContext, "     Invalid File     ", Toast.LENGTH_LONG).show();
                                    progressInfo.setText("");
                                    progressBar.setProgress(0);
                                    progressBar.setIndeterminate(false);
                                    progressBar.setVisibility(View.GONE);
                                    progressState[position] = false;
                                }
                            });
                }
            });

            try {
                menu.setOnClickListener(v -> {
                    switch (v.getId()) {
                        case R.id.feed_item_menu:
                            PopupMenu popup = new PopupMenu(mContext, v);
                            popup.getMenuInflater().inflate(R.menu.file_clipboard_popup,
                                    popup.getMenu());
                            popup.show();
                            popup.setOnMenuItemClickListener(item -> {
                                switch (item.getItemId()) {
                                    case R.id.report:
                                        AlertDialogTheme();
                                        break;
                                    case R.id.action_share:
                                        String smsBody = "I believe this will be of interest to you. Please let me know what you think.\n" +
                                                file.getUrl();
                                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                        sharingIntent.setType("text/plain");
                                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, smsBody);
                                        mContext.startActivity(Intent.createChooser(sharingIntent, "Share using?"));
                                        break;
                                    case R.id.action_download:
//                                        String url = file.getUrl();
//                                        downloadFile(position, url);
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

        }

        if (a instanceof Course) {
            Course course = (Course) a;

            listItem = LayoutInflater.from(mContext).inflate(R.layout.feed_course_item, parent, false);
            TextView mCourseName = (TextView) listItem.findViewById(R.id.course_name);
            TextView mTecherName = (TextView) listItem.findViewById(R.id.techer_name);

            mCourseName.setText(course.getName());
            mTecherName.setText(course.getLocation());///change to teacher name

        }

        return listItem;
    }

    public void AlertDialogTheme() {
        AlertDialog.Builder AlertBuilder = new AlertDialog.Builder(
                mContext, R.style.Theme_Report_Dialog_Alert);
        AlertBuilder.setTitle("Report");
        AlertBuilder.setMessage("Would you like to report this file?");
        AlertBuilder.setCancelable(false);
        AlertBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertBuilder.setNegativeButton("N0", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = AlertBuilder.create();
        dialog.show();
    }

}

