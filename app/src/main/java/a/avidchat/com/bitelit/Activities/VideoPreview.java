package a.avidchat.com.bitelit.Activities;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;

import a.avidchat.com.bitelit.R;

public class VideoPreview extends AppCompatActivity {
    public static final String INTENT_NAME_VIDEO_PATH = "INTENT_NAME_VIDEO_PATH";
    private VideoView mVvPlayback;
    String path;
    private int mVideoCurPos;
    File mVideo,audio;
    FFmpeg ffmpeg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);


        mVvPlayback =  findViewById(R.id.vv_playback);
         path = getIntent().getStringExtra(INTENT_NAME_VIDEO_PATH);
        if (path == null) {
            finish();
        }
        mVvPlayback.setVideoPath(path);
        mVvPlayback.setKeepScreenOn(true);
        mVvPlayback.setMediaController(new MediaController(this));
        mVvPlayback.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });
        mVvPlayback.start();
    }



    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode == 1){

            if(resultCode == RESULT_OK){

                String paths = _getRealPathFromURI(getApplicationContext(), data.getData());
                 audio = new File(paths);
//                    String cmd = "ffmpeg -i " + mVideo.getPath() + " -i " + filePath + " -shortest -threads 0 -preset ultrafast -strict -2 " + editedVideo.getPath();
//                    String[] command = cmd.split(" ");
                loadFFmpeg(mVideo,audio,mVideo);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private String _getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Audio.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private void loadFFmpeg(final File videoPath, final File audioPath, final File outputPath) {
        ffmpeg = FFmpeg.getInstance(getApplicationContext());
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onFailure() {}

                @Override
                public void onSuccess() {

                }

                @Override
                public void onFinish() {
                   String cmd= "-y -i " + videoPath + " -i " + audioPath + " -c:v copy -map 0:v:0 -map 1:a:0 -c:a aac -shortest " + outputPath;
                    String[] command = cmd.split(" ");
                    executeFFmpeg(command,outputPath);
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }
    }

    private void executeFFmpeg(String[] cmd, final File outputpath)
    {
        try {
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.e("Progress","Start");
                }

                @Override
                public void onProgress(String message) {
                    Log.e("Progress",message);
                }

                @Override
                public void onFailure(String message) {
                    Log.e("Fail",message);
                }

                @Override
                public void onSuccess(String message) {
                    Log.e("Success",message);
                }

                @Override
                public void onFinish() {
                    Log.e("Ha",outputpath.getPath());

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVvPlayback.stopPlayback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVvPlayback.pause();
        mVideoCurPos = mVvPlayback.getCurrentPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVvPlayback.seekTo(mVideoCurPos);
        mVvPlayback.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =new Intent(VideoPreview.this,VideoRecording.class);
        startActivity(intent);
        overridePendingTransition(0,0);
    }
}
