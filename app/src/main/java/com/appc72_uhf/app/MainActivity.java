package com.appc72_uhf.app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.appc72_uhf.app.activities.inventoryList;
import com.appc72_uhf.app.fragment.UHFReadTagFragment;
import com.appc72_uhf.app.fragment.UHFSetFragment;
import com.rscja.utility.StringUtility;

import java.util.HashMap;


public class MainActivity extends BaseTabFragmentActivity {
    private final static String TAG ="MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initSound();
        initUHF();
        initViewPageData();
        initViewPager();
        initTabs();
    }

    @Override
    protected void initViewPageData() {
        lstFrg.add(new inventoryList());
        lstFrg.add(new UHFReadTagFragment());
        lstFrg.add(new UHFSetFragment());

        //lstFrg.add(new UHFReadFragment());
        //lstFrg.add(new UHFWriteFragment());
        //lstFrg.add(new UHFKillFragment());
        //lstFrg.add(new UHFLockFragment());


        lstTitles.add(getString(R.string.uhf_msg_tab_inventory));
        lstTitles.add(getString(R.string.uhf_msg_tab_scan));
        lstTitles.add(getString(R.string.uhf_msg_tab_set));

        //lstTitles.add(getString(R.string.uhf_msg_tab_read));
        //lstTitles.add(getString(R.string.uhf_msg_tab_kill));
        //lstTitles.add(getString(R.string.uhf_msg_tab_write));
        //lstTitles.add(getString(R.string.uhf_msg_tab_lock));

    }
    @Override
    protected void onDestroy() {
        if (mReader != null) {
            mReader.free();
        }
        super.onDestroy();
    }
    @SuppressLint("StaticFieldLeak")
    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            return mReader.init();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            mypDialog.cancel();

            if (!result) {
                Toast.makeText(MainActivity.this, "fallo al iniciar",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(MainActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("iniciando...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }

    }

    public boolean vailHexInput(String str) {

        if (str == null || str.length() == 0) {
            return false;
        }

        if (str.length() % 2 == 0) {
            return StringUtility.isHexNumberRex(str);
        }

        return false;
    }
    HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    private SoundPool soundPool;
    private float volumnRatio;
    private AudioManager am;
    private void initSound(){
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundMap.put(1, soundPool.load(this, R.raw.barcodebeep, 1));
        soundMap.put(2, soundPool.load(this, R.raw.serror, 1));
        am = (AudioManager) this.getSystemService(AUDIO_SERVICE);
    }
    public void playSound(int id) {

        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumnRatio = audioCurrentVolumn / audioMaxVolumn;
        try {
            soundPool.play(soundMap.get(id), volumnRatio,
                    volumnRatio,
                    1,
                    0,
                    1
            );
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
