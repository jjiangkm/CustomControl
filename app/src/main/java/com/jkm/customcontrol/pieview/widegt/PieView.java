package com.jkm.customcontrol.pieview.widegt;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.jkm.customcontrol.R;

import java.util.List;

/**
 * Created by cmc on 2015/12/3.
 */
public class PieView extends View {
    private int normalColor = Color.rgb(240,240,240);
    private int selectColor = Color.GREEN;
    private int normalTextColor = Color.GRAY;
    private int selectTextColor = Color.WHITE;
    private int circleColor = Color.WHITE;
    private Paint arcPaint;
    private Paint textPaint;
    private int selectPosition = 1;
    private PointF circle;
    private List<Data> list;
    private OnItemSelectListener onItemSelectListener;

    public PieView(Context context) {
        super(context);
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public List<Data> getList() {
        return list;
    }

    /**
     * 设置饼图数据、显示文字，图片
     * @param list
     */
    public void setList(List<Data> list) {
        this.list = list;
    }

    public void setSelectTextColor(int selectTextColor) {
        this.selectTextColor = selectTextColor;
    }

    public void setNormalTextColor(int normalTextColor) {
        this.normalTextColor = normalTextColor;
    }

    public void setSelectColor(int selectColor) {
        this.selectColor = selectColor;
    }

    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
    }

    public PieView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);

    }

    public PieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.pieView);
        int count = ta.getIndexCount();
        for(int i = 0;i<count;i++){
            int index = ta.getIndex(i);
            switch (index){
                case R.styleable.pieView_normal_color:
                    normalColor = ta.getColor(i, Color.rgb(240,240,240));
                    break;

                case R.styleable.pieView_select_color:
                    selectColor = ta.getColor(i, Color.GREEN);
                    break;
                case R.styleable.pieView_normal_text_color:
                    normalTextColor = ta.getColor(i, Color.GRAY);
                    break;

                case R.styleable.pieView_select_text_color:
                    selectTextColor = ta.getColor(i, Color.WHITE);
                    break;
                case R.styleable.pieView_circle_color:
                    circleColor = ta.getColor(i, Color.WHITE);
                    break;
            }
        }
        init();
    }

    private void init() {
        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(28);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PieView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //确定中心点
        circle = new PointF(getWidth()/2f,getHeight()/2f);
        //确定圆半径 取高和宽小的一部分作为直径
        float min = getHeight()>getWidth()?getWidth():getHeight();
        //减20作为拉开间距
        float arcRadio = min/2f-20;
        //圆的外接矩形
        RectF rect = new RectF(circle.x-arcRadio,circle.y-arcRadio,circle.x+arcRadio,circle.y+arcRadio);
        if(list!=null&&list.size()>0) {
            for (int i = 0; i < list.size(); i++) {
                String title = "";
                int bitmapReId = R.mipmap.ic_launcher;
                Data date = list.get(i);
                if (date.name != null)
                    title = date.name;
                //设置选中改变图标和字体颜色
                if (i == selectPosition) {
                    textPaint.setColor(selectTextColor);
                    arcPaint.setColor(selectColor);
                    if (date.selectDrawbleId != 0)
                        bitmapReId = date.selectDrawbleId;
                } else {
                    arcPaint.setColor(normalColor);
                    textPaint.setColor(normalTextColor);
                    if (date.drawableID != 0)
                        bitmapReId = date.drawableID;
                }
                //按角度移动画布，确定间隙
                canvas.save();
                //按角度移动画布，
                canvas.translate((float) (Math.sin((180/list.size() + 360/list.size() * i) * Math.PI / 180) * 10), -(float) (10 * Math.cos((180/list.size() + 360/list.size() * i) * Math.PI / 180)));
                canvas.drawArc(rect, 270 + 360/list.size() * i, 360/list.size(), true, arcPaint);
                canvas.restore();
                canvas.save();
                canvas.translate((float) (Math.sin((180/list.size() + 360/list.size() * i) * Math.PI / 180) * arcRadio / 1.5), -(float) (arcRadio / 1.5 * Math.cos((180/list.size() + 360/list.size() * i) * Math.PI / 180)));
                Bitmap bit = BitmapFactory.decodeResource(getResources(), bitmapReId);
                canvas.drawBitmap(bit, circle.x - bit.getWidth() / 2, circle.y - bit.getHeight(), arcPaint);
                canvas.drawText(title, circle.x, circle.y + 28, textPaint);
                canvas.restore();
            }
        }
        //画中心圆
        arcPaint.setColor(circleColor);
        canvas.drawCircle(circle.x, circle.y, arcRadio / 2.5f, arcPaint);
    }

    /**
     * 百度查到别人写的一个方法，在我多个饼图中都有使用这个方法，确认无误
     * @param radiusX 圆心
     * @param radiusY 圆心
     * @param x1      触摸点
     * @param y1      触摸点
     * @return
     * @throws
     * @Title: getTouchedPointAngle
     * @Description: 计算触摸角度
     */
    private float getTouchedPointAngle(float radiusX, float radiusY, float x1,
                                       float y1) {
        float differentX = x1 - radiusX;
        float differentY = y1 - radiusY;
        double a = 0.0D;
        double t = differentY
                / Math.sqrt(differentX * differentX + differentY * differentY);

        if (differentX > 0.0F) {
            // 0~90
            if (differentY > 0.0F)
                a = 6.283185307179586D - Math.asin(t);
            else
                // 270~360
                a = -Math.asin(t);
        } else if (differentY > 0.0F)
            // 90~180
            a = 3.141592653589793D + Math.asin(t);
        else {
            // 180~270
            a = 3.141592653589793D + Math.asin(t);
        }
        return (float) (360.0D - a * 180.0D / 3.141592653589793D % 360.0D);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float cx = circle.x;
        float cy = circle.y;
        float r = getHeight();
        float r1 = (float) ((getHeight()/2f-20)/2.5);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if ((x - cx) * (x - cx) + (y - cy) * (y - cy) - r * r <= 0.0F && (x - cx) * (x - cx) + (y - cy) * (y - cy) - r1 * r1 >= 0.0F) {
                    selectPosition = getSelectPosition(getTouchedPointAngle(cx,cy,x,y));
                    if(onItemSelectListener!=null)
                        onItemSelectListener.selectPosition(selectPosition,this);
                    invalidate();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private int getSelectPosition(float touchedPointAngle) {
        Log.i("TAG",touchedPointAngle+"");
        //当为4块及以下时，第一块的范围为270-360+所以加上一下代码
        if(list.size()<=4){
            if(touchedPointAngle>270||touchedPointAngle<=(270+360/list.size())%360){
                return 0;
            }
        }
        for(int i = 0;i<list.size();i++){
            if(touchedPointAngle>(270+i*360/list.size())%360&&touchedPointAngle<=(270+(i+1)*360/list.size())%360){
                return i;
            }
        }
        return 1;
    }

    public static class Data {
        public Data() {
        }

        public Data(String name, int drawableID, int selectDrawbleId) {
            this.name = name;
            this.drawableID = drawableID;
            this.selectDrawbleId = selectDrawbleId;
        }

        public String name;
        public int drawableID;
        public int selectDrawbleId;
    }
    public interface OnItemSelectListener{
        void selectPosition(int position, PieView pieView);
    }
}
