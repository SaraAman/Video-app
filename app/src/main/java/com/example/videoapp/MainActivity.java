package com.example.videoapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    private static int VIDEO_REQUEST = 101;
    private Uri videoUri = null;
    private VideoView mVideoView;
    MediaRecorder recorder;
    File file;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = findViewById(R.id.videoview);
        recorder = new MediaRecorder();

        MediaController mediaController = new MediaController(this);
        mVideoView.stopPlayback();
        mVideoView.setMediaController(mediaController);
        mediaController.setAnchorView(mVideoView);

    }


    public void capturerVideo(View view) {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            startActivityForResult(videoIntent, VIDEO_REQUEST);
            mVideoView.setVisibility(View.VISIBLE);


        }
    }

    public void playVideo() {
        mVideoView.setVideoURI(videoUri);
        mVideoView.start();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == VIDEO_REQUEST && resultCode == RESULT_OK) {
            videoUri = data.getData();
            playVideo();
            Log.d("VideoUri", videoUri + "");
            file = new File(getRealPathFromURI(this, videoUri));
            long length = file.length();
            length = length / 1024;
            long mb = length / 1024;
            Toast.makeText(this, "Video size:" + length + "KB,  " + mb + "MB", Toast.LENGTH_LONG).show();

            try {
                path = data.getData().toString();
                mVideoView.setVideoPath(path);
                mVideoView.requestFocus();
                mVideoView.start();


            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void shareVideo(View view) {

        Uri uri = Uri.parse(videoUri.toString());
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("video/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        this.startActivity(Intent.createChooser(share, "Share Video File"));
    }

    public void upload(View view) {
        Toast.makeText(this, videoUri.toString(), Toast.LENGTH_SHORT).show();
    }

    public void deleteVideo(View view) {

       mVideoView.setVideoURI(null);
        File videoFiles = new File(videoUri.getPath());
        videoFiles.delete();
        this.getContentResolver().delete(videoUri, null, null);
        mVideoView.clearAnimation();
        mVideoView.setVisibility(View.GONE);
    }
}

//        try {
//            Uri uri = Uri.parse(videoUri.toString());
//            File videoFile = new File(path);
//            videoFile.getAbsoluteFile(uri.getPath(path));
//            // if (videoFile.exists()) {
//            boolean ret = videoFile.delete();
//            int rett = this.getContentResolver().delete(videoUri, null, null);
//            Log.d("Entry Delted", String.valueOf(ret + "----" + rett));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
      // }
//            try {
//
//                File videofiles = new File(videoUri.getPath());
//                    videofiles.delete();
//                mVideoView.release();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
////
//   }
//}