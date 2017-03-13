package com.practice.mybanner;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.practice.mybanner.R.id.ll_dot_group;
import static com.practice.mybanner.R.id.tv_img_desc;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    @Bind(R.id.vp)
    ViewPager vp;
    @Bind(tv_img_desc)
    TextView tvImgDesc;
    @Bind(ll_dot_group)
    LinearLayout llDotGroup;

    private List<ImageView> vpLists;
    private String[] imageDescArrs;

    private boolean isSwitchPager = false; //默认不切换
    private int previousPosition = 0; //默认为0


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //更新当前viewpager的 要显示的当前条目
            vp.setCurrentItem(vp.getCurrentItem() + 1);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        initViewPagerData();
        vp.setAdapter(new ViewpagerAdapter());

        //设置当前viewpager要显示第几个条目
        int item = Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % vpLists.size());
        vp.setCurrentItem(item);

        //把第一个小圆点设置为白色，显示第一个textview内容
        llDotGroup.getChildAt(previousPosition).setEnabled(true);
        tvImgDesc.setText(imageDescArrs[previousPosition]);
        //设置viewpager滑动的监听事件
        vp.addOnPageChangeListener(this);

        //实现自动切换的功能
        new Thread() {
            public void run() {
                while (!isSwitchPager) {
                    SystemClock.sleep(3000);
                    //拿着我们创建的handler 发消息
                    handler.sendEmptyMessage(0);
                }
            }
        }.start();
    }

    /**
     * 初始化ViewPager的数据
     */
    private void initViewPagerData() {
        imageDescArrs = new String[]{"标题1", "标题2", "标题3", "标题4", "标题5"};
        vpLists = new ArrayList<ImageView>();
        int imgIds[] = {R.mipmap.a, R.mipmap.b, R.mipmap.c, R.mipmap.d, R.mipmap.e};
        ImageView iv;
        View dotView;

        for (int i = 0; i < imgIds.length; i++) {
            iv = new ImageView(this);
            iv.setBackgroundResource(imgIds[i]);
            vpLists.add(iv);
            //准备小圆点的数据
            dotView = new View(getApplicationContext());
            dotView.setBackgroundResource(R.drawable.selector_dot);
            //设置小圆点的宽和高
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(15, 15);
            //设置每个小圆点之间距离
            if (i != 0) {
                params.leftMargin = 15;
            }
            dotView.setLayoutParams(params);
            //设置小圆点默认状态
            dotView.setEnabled(false);
            //把dotview加入到线性布局中
            llDotGroup.addView(dotView);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    //当新的页面被选中的时候调用
    @Override
    public void onPageSelected(int position) {
        //拿着position位置 % 集合.size
        int newposition = position % vpLists.size();
        //取出postion位置的小圆点 设置为true
        llDotGroup.getChildAt(newposition).setEnabled(true);
        //把一个小圆点设置为false
        llDotGroup.getChildAt(previousPosition).setEnabled(false);
        tvImgDesc.setText(imageDescArrs[newposition]);
        previousPosition = newposition;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 定义数据适配器
     */
    private class ViewpagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        //是否复用当前view对象
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        //初始化每个条目要显示的内容
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //拿着position位置 % 集合.size
            int newposition = position % vpLists.size();
            //获取到条目要显示的内容imageview
            ImageView iv = vpLists.get(newposition);
            //要把 iv加入到 container 中
            container.addView(iv);
            return iv;
        }

        //销毁条目
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //移除条目
            container.removeView((View) object);
        }
    }
}
