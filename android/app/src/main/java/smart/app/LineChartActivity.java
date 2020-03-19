package smart.app;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


public class LineChartActivity extends Activity implements View.OnClickListener {
    private TextView txt_topbar;
    private ImageButton back;
    private LineChart mChart;
    private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
    private List<String> timeList = new ArrayList<>(); //存储x轴的时间
    String sensorsid = null;
    String message = null;
    private Timer timer = new Timer();
    Sensorsparsebean sensorsparsebean;
    HashMap<String, Object> hashMap;

    private static class handler extends Handler {
        private final WeakReference<LineChartActivity> mActivity;

        private handler(LineChartActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LineChartActivity activity = mActivity.get();
            if (activity != null) {
                super.handleMessage(msg);
            }
        }
    }

    private final LineChartActivity.handler handler = new handler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor_menu_linechart);
        sensorsparsebean = new Sensorsparsebean();
        df.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        back = findViewById(R.id.back);
        txt_topbar = findViewById(R.id.txt_topbar);
        back.setOnClickListener(this);
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private void init() {
        mChart = findViewById(R.id.line_chart);
        mChart.setTouchEnabled(true);
        setDescription("");
        // 可拖曳
        mChart.setDragEnabled(true);
        // 可缩放
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        //显示边界
        mChart.setDrawBorders(true);
        mChart.setPinchZoom(true);
        mChart.animateX(1000);//绘制动画 从左到右

        // 设置图表的背景颜色
        mChart.setBackgroundColor(Color.LTGRAY);
        LineData data = new LineData();
        // 数据显示的颜色
        data.setValueTextColor(Color.BLACK);
        // 先增加一个空的数据，随后往里面动态添加
        mChart.setData(data);
        // 图表的注解(只有当数据集存在时候才生效)
        Legend l = mChart.getLegend();
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTextSize(11f);
        // 线性，也可是圆
        l.setForm(Legend.LegendForm.LINE);
        // 颜色
        l.setTextColor(Color.BLUE);
        // x坐标轴
        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(true);
        //是否绘制轴线
        xl.setDrawAxisLine(true);
        xl.setAvoidFirstLastClipping(false);
        // 如果false，那么x坐标轴将不可见
        xl.setEnabled(true);
        // 将X坐标轴放置在底部，默认是在顶部。
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
//        //设置最小间隔，防止当放大时出现重复标签
        xl.setGranularity(1f);
        xl.setLabelCount(5);
        xl.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return timeList.get((int) value % timeList.size());
            }
        });
        // 图表左边的y坐标轴线
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);
        YAxis rightAxis = mChart.getAxisRight();
        // 不显示图表的右边y坐标轴线
        rightAxis.setEnabled(false);
        //保证Y轴从0开始，不然会上移一点
        leftAxis.setAxisMinimum(0f);
        rightAxis.setAxisMinimum(0f);
        sensorsid = getIntent().getExtras().getString("sensorid");
        new Thread(new Runnable() {
            @Override
            public void run() {
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            hashMap = HttpService.devicesdata(sensorsid, "sensor");
                            message = (String) hashMap.get("str");
                            sensorsparsebean = HttpService.JsonToObject(message, Sensorsparsebean.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        handler.post(udpUIRunnable);
                    }
                }, 0, 1000);
            }
        }).start();
    }

    Runnable udpUIRunnable = new Runnable() {
        @Override
        public void run() {
            txt_topbar.setText(sensorsparsebean.data.Name);
            Float f = Float.parseFloat(sensorsparsebean.data.Value);
            addEntry(f, sensorsparsebean.data.Name, sensorsparsebean.data.Unit);
        }
    };

    // 添加进去一个坐标点
    private void addEntry(float number, String titlename, String unit) {
        LineData data = mChart.getData();
        ILineDataSet set = data.getDataSetByIndex(0);
        if (set == null) {
            set = createLineDataSet(titlename, unit);
            data.addDataSet(set);
        }
        mChart.setData(data);
        if (timeList.size() > 6) {
            timeList.clear();
        }
        timeList.add(df.format(System.currentTimeMillis()));
        Entry entry = new Entry(set.getEntryCount(), number);
        data.addEntry(entry, 0);
        data.notifyDataChanged();
        mChart.notifyDataSetChanged();
        mChart.setVisibleXRangeMaximum(5);
        mChart.moveViewToX(data.getEntryCount() - 5);
    }

    private LineDataSet createLineDataSet(String titlename, String unit) {
        LineDataSet set;
        if (unit != null) {
            set = new LineDataSet(null, titlename + " " + unit);
        } else {
            set = new LineDataSet(null, titlename);
        }
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(3f);
        set.setFillAlpha(128);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.GREEN);
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(10f);
        set.setDrawValues(true);
        return set;
    }

    public void setDescription(String str) {
        Description description = new Description();
        description.setText(str);
        mChart.setDescription(description);
        mChart.invalidate();
    }

    @Override
    protected void onDestroy() {
        //当结束程序时关掉Timer
        timer.cancel();
        super.onDestroy();
    }
}
