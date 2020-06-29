package com.zwyl.myhomework.main;

import android.widget.MediaController;
import android.widget.VideoView;


import com.zwyl.myhomework.R;
import com.zwyl.myhomework.base.BaseActivity;

import butterknife.BindView;

public class PlayActivity extends BaseActivity {
    @BindView(R.id.videoView)
    VideoView videoView;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_play;
    }

    @Override
    protected void initView() {
        super.initView();
        setHeadView();
        String resourceUri = getIntent().getStringExtra("resourceUri");
        if (resourceUri != null) {
            videoView.setMediaController(new MediaController(PlayActivity.this));
            videoView.setVideoPath(resourceUri);
            videoView.start();
        }
    }

    /**
     * 设置顶部点击事件
     */
    private void setHeadView() {
        setTitleCenter("视频");
        setShowLeftHead(false);//左边顶部按钮
        setShowRightHead(false);//右边顶部按钮
        setShowFilter(false);//日历筛选
        setShowLogo(true);//logo
        setShowRefresh(false);//刷新
        setLogoClick(v -> {
            finish();
        });
    }

}
