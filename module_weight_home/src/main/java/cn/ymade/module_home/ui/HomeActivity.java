package cn.ymade.module_home.ui;


import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zcxie.zc.model_comm.base.BaseActivity;
import com.zcxie.zc.model_comm.callbacks.CallBack;
import com.zcxie.zc.model_comm.util.AppConfig;
import com.zcxie.zc.model_comm.util.CommUtil;
import com.zcxie.zc.model_comm.util.LiveDataBus;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.ymade.module_home.R;
import cn.ymade.module_home.adapter.HomeMenuAdapterKt;
import cn.ymade.module_home.common.Constant;
import cn.ymade.module_home.databinding.ActivityHomeBinding;
import cn.ymade.module_home.db.beans.LotDataBean;
import cn.ymade.module_home.db.database.DataBaseManager;
import cn.ymade.module_home.model.HomeMenuBean;
import cn.ymade.module_home.vm.VMHome;


/**
 * @author zc.xie
 * @date 2021/4/19 0019.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
@Route(path = "/home/homeActivity1")
public class HomeActivity extends BaseActivity<VMHome, ActivityHomeBinding> {

    private List<HomeMenuBean> homeMenuBeanList = new ArrayList<>();
//    HomeMenuAdapter homeMenuAdapter = null;
    HomeMenuAdapterKt homeMenuAdapter = null;


    public void initData() {

        getMBinding().setVm(getMViewModel());
        initMenuData();
        loadData();
        getMBinding().rvAssetm.setLayoutManager(new GridLayoutManager(this, 3));
        homeMenuAdapter = new HomeMenuAdapterKt(homeMenuBeanList, new CallBack<HomeMenuBean>() {
            @Override
            public void callBack(HomeMenuBean obj) {
                switch (obj.type) {
                    case 1:
                        startActivity(new Intent(HomeActivity.this, SearchGoodsActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(HomeActivity.this,LotActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(HomeActivity.this, LotCreateActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(HomeActivity.this,GoodsListActivity .class));
                        break;
                    case 5:
                        startActivity(new Intent(HomeActivity.this,ClientActivity.class));
                        break;
//                    case 6:
//                        startActivity(new Intent(HomeActivity.this,SyncActvity .class));
//                        break;
                    case 7:
                        startActivity(new Intent(HomeActivity.this,SummaryActivity .class));
                        break;
                    case 8:
                        startActivity(new Intent(HomeActivity.this,PropertiesActivity.class));
                        break;

                }

            }
        });

        getMBinding().rvAssetm.setAdapter(homeMenuAdapter);
    }
    private void initMenuData() {
        for (int i = 0; i < Constant.menuName.length; i++) {
            homeMenuBeanList.add(new HomeMenuBean(Constant.menuName[i], Constant.menuIconResId[i], i + 1));
        }
    }

    int goodsCount=0;
    float weight=0f;
    //加载数据
    private void loadData() {

      new Thread(new Runnable() {
          @Override
          public void run() {
             long start= CommUtil.getStartTime();
             long end=CommUtil.getEndTime();
            List<LotDataBean> lotDataBeans=  DataBaseManager.INSTANCE.getDb().lotDao().loadAllInTime(start,end);

               goodsCount=0;
               weight=0f;
            for ( int i=0;i<lotDataBeans.size();i++){
                LotDataBean lotDataBean=lotDataBeans.get(i);
                goodsCount+=lotDataBean.getItems();
                weight+=lotDataBean.getWeight();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getMBinding().tvTitle1.setText(lotDataBeans.size()+"");
                getMBinding().tvTitle2.setText(goodsCount+"");
                    getMBinding().tvTitle3.setText(weight+"");
                }
            });
          }
      }).start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getMBinding().unCode.setText(AppConfig.staff.get());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void processLogic() {
        initData();
        initEvent();
    }

    private void initEvent() {
        getMBinding().imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
            }
        });
        LiveDataBus.get().with(Constant.LD_UP_HOME_TITLE,Integer.class).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                loadData();
            }
        });
    }

    @NotNull
    @Override
    public Class findViewModelClass() {
        return VMHome.class;
    }


}
