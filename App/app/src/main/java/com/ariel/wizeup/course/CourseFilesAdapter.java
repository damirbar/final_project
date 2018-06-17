package com.ariel.wizeup.course;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.CourseFile;
import com.ariel.wizeup.file.DownloadFile;
import com.ariel.wizeup.model.CourseMessage;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizeup.file.OpenFile.dirPath;
import static com.ariel.wizeup.file.OpenFile.openFile;

public class CourseFilesAdapter extends ArrayAdapter<CourseFile> {
    private Context mContext;
    private List<CourseFile> filesList;
    private boolean[] progressState;

    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;
    private int positionDel;
    private String cid;


    public CourseFilesAdapter(Activity context, ArrayList<CourseFile> list, View v, String _cid) {
        super(context, 0, list);
        mContext = context;
        filesList = list;
        progressState = new boolean[list.size()];
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(context);
        mServerResponse = new ServerResponse(v);
        cid = _cid;

    }

    public boolean[] getProgressState() {
        return progressState;
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
                                case R.id.delete:
                                    positionDel = position;
                                    deleteFile(filesList.get(position).getPublicid(),filesList.get(position).get_id());
                                    break;
                                case R.id.report:
                                    AlertDialogTheme();
                                    break;

                                case R.id.action_share:
                                    String smsBody = "I believe this will be of interest to you. Please let me know what you think.\n" +
                                            filesList.get(position).getUrl();
                                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, smsBody);
                                    mContext.startActivity(Intent.createChooser(sharingIntent, "Share using?"));
                                    break;
                                case R.id.action_download:
                                    String url = filesList.get(position).getUrl();
                                    String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());

                                    File file = new File(dirPath(mContext) + "/" + fileName);
                                    if (file.exists()) {
                                        openFile(file, mContext);
                                        break;
                                    }

                                    holder.progressBar.setVisibility(View.VISIBLE);
                                    holder.progressBar.getIndeterminateDrawable().setColorFilter(
                                            Color.BLUE, PorterDuff.Mode.SRC_IN);
                                    holder.progressBar.setIndeterminate(true);

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

    private void deleteFile(String publicid,String id) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().removeFile(publicid, id, cid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, i -> mServerResponse.handleError(i)));
    }

    private void handleResponse(Response response) {
        filesList.remove(filesList.get(positionDel));
        notifyDataSetChanged();
    }


    @Override
    public void finalize() {
        mSubscriptions.unsubscribe();
    }



}

