package cn.ymade.module_home.ui;

import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.zcxie.zc.model_comm.base.BaseActivity;
import com.zcxie.zc.model_comm.callbacks.CallBack;
import com.zcxie.zc.model_comm.util.CommUtil;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.ymade.module_home.R;
import cn.ymade.module_home.databinding.ActivitySummaryBinding;
import cn.ymade.module_home.model.SummaryData;
import cn.ymade.module_home.vm.VMSummary;

import static com.zcxie.zc.model_comm.util.CommUtil.getTime;


/**
 * @author zc.xie
 * @date 2021/4/19 0019.
 * GitHub：
 * email：3104873490@qq.com
 * description：
 */
@Route(path = "/home/summaryActivity")
public class SummaryActivity extends BaseActivity<VMSummary, ActivitySummaryBinding> {

    public void initData() {
        setTopTitle("汇总");
        updateTime();
        loading();
    }

    //更新时间
    private void updateTime() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -30);
        String startDate = new SimpleDateFormat("yyyy-MM-dd").format(now.getTime());

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String nowdayTime = dateFormat.format(new Date());

        getMBinding().tvStartTime.setText(startDate);
        getMBinding().tvStopTime.setText(nowdayTime);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_summary;
    }

    @Override
    public void processLogic() {
        initTopSearchBar();
        initData();
        initEvent();
    }

    private void initEvent() {
        getMBinding().tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //时间选择器
                TimePickerView pvTime = new TimePickerBuilder(SummaryActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        getMBinding().tvStartTime.setText(getTime(date));
                    }
                }).build();
                Calendar calendar=Calendar.getInstance();
                String ymd=getMBinding().tvStartTime.getText().toString();
                calendar.setTime(CommUtil.ConverToDateDay(ymd));
                pvTime.setDate(calendar);//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
                pvTime.show();
            }
        });
        getMBinding().tvStopTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //时间选择器
                TimePickerView pvTime = new TimePickerBuilder(SummaryActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        getMBinding().tvStopTime.setText(getTime(date));
                    }
                }).build();
                pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
                pvTime.show();
            }
        });
        getMBinding().btnSummarySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading();
            }
        });
    }

    private void loading(){
        showProgress("查询中..");
        String start = getMBinding().tvStartTime.getText().toString();
        String stop = getMBinding().tvStopTime.getText().toString();

        Log.i("TAG", "loading:getNum start start "+start+" stop "+stop+ getTopSearchText());

        getMViewModel().getNum(getTopSearchText(),start,stop,new CallBack<SummaryData>() {
            @Override
            public void callBack(SummaryData data) {
                hideProgress();
                if (data==null)
                    return;
                getMBinding().tvGoodsSum.setText(""+data.getGoodsSum());
                getMBinding().tvLotCount.setText(""+data.getLotCount());
            }
        });
    }

    @NotNull
    @Override
    public Class findViewModelClass() {
        return VMSummary.class;
    }

    @Override
    public void onSearchAction(@NotNull String s) {
        super.onSearchAction(s);
        Log.i("TAG", "onSearchAction: s key "+s);
        loading();
    }
}
