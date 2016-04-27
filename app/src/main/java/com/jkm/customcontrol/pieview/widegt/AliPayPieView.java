package com.jkm.customcontrol.pieview.widegt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

/**
 * Created by cmc on 2015/12/3.
 */
public class AliPayPieView extends View {

    //数据
    private List<PieChartInfo> data;
    //屏幕中心位置
    private Point position;
    //圆的半径
    private int radiusLength;
    //屏幕宽
    private int rect;

    private Context context;
    private int i = 0;
    //是否是启动动画
    private boolean isStartAnim = true;
    //移动动画是否还在进行
    private boolean isAnim = false;
    //当前选中的扇形
    private int selectPosition;
    private Paint mPaintCircleBorder;//中心圆画笔
    private Paint paint;//文字画笔
    private Paint mPaintFill;//扇形画笔

    public void setChartViewListener(ChartViewListener chartViewListener) {
        this.chartViewListener = chartViewListener;
    }

    private ChartViewListener chartViewListener;

    public AliPayPieView(Context context) {
        super(context);
    }

    public AliPayPieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initPaint(context);
    }

    public AliPayPieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public interface ChartViewListener {
        public void getCurrentItem(PieChartInfo entity, int selectIndex);
    }

    /**
     * 启动动画
     */
    private void start() {
        //扇形正在移动中
        this.i=0;
        isAnim = true;
        isStartAnim=true;
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 360; i++) {
                    AliPayPieView.this.i++;
                    SystemClock.sleep(2);
                    postInvalidate();
                }
                isStartAnim = false;
                //移动第一个扇形到最下方
                moveToPosition(0);
            }
        }.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (canvas) {
            // get safe rect 获取屏幕宽度
            rect = super.getWidth() > super.getHeight() ? super.getHeight()
                    : super.getWidth();
            // calculate radius length 半径长度
            radiusLength = (int) ((rect / 2f)) - 20;
            // calculate position 屏幕中心点
            position = new Point((int) (getWidth() / 2f), (int) (getHeight() / 2f));
            // 画扇形
            drawData(canvas);
            //画空心圆
            drawCircle(canvas);
            //画空心圆内的字
            drawText(canvas);
        }
        //画年份
//        drawYear(canvas);
    }


    private void initPaint(Context context){
        mPaintCircleBorder = new Paint();
        mPaintCircleBorder.setColor(Color.WHITE);
        mPaintCircleBorder.setStyle(Paint.Style.FILL);
        mPaintCircleBorder.setAntiAlias(true);

        paint = new Paint();
        paint.setAntiAlias(true);

        mPaintFill = new Paint();
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setAntiAlias(true);
    }

    /**
     * 画出外部的圆
     *
     * @param canvas
     */
    protected void drawCircle(Canvas canvas) {

        // draw a circle
        canvas.drawCircle(position.x, position.y, radiusLength * 0.5f,
                mPaintCircleBorder);
    }

    /**
     * 画出圆中央的文字
     *
     * @param canvas
     */
    protected void drawText(Canvas canvas) {
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    protected void drawData(final Canvas canvas) {
        if (null != data && data.size() != 0) {

            // draw arcs of every piece
            for (int j = 0; j < data.size(); j++) {
                PieChartInfo e = data.get(j);
                // get color
                mPaintFill.setColor(e.color);
                RectF oval = new RectF(position.x - radiusLength, position.y
                        - radiusLength, position.x + radiusLength, position.y
                        + radiusLength);
                //是否是启动动画
                if (isStartAnim) {
                    float tarGetAngle = e.startAngle + e.angle - 360 + this.i;
                    if (tarGetAngle >= 0) {
                        if (tarGetAngle < e.angle) {
                            canvas.drawArc(oval, 0, tarGetAngle, true, mPaintFill);
                        } else {
                            if(e.angle!=0)//华为emui4.0时会画出一个圆
                            canvas.drawArc(oval, tarGetAngle - e.angle, e.angle, true, mPaintFill);
                        }
                    }
                } else {
                    if (!isAnim && j == selectPosition) {
                        if(data.size()>1) {
                            oval = new RectF(position.x - radiusLength, position.y
                                    - radiusLength+20, position.x + radiusLength, position.y
                                    + radiusLength + 20);
                        }
                    } else {
                        oval = new RectF(position.x - radiusLength, position.y
                                - radiusLength, position.x + radiusLength, position.y
                                + radiusLength);
                    }
                    if(e.angle!=0)
                    canvas.drawArc(oval, e.startAngle, e.angle, true, mPaintFill);
                }
            }
        }
    }

    /**
     * @param data the data to set
     */
    public void setData(List<PieChartInfo> data) {
        this.data = data;
        if (data!=null) {
            start();
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((!isAnim) && (data != null) && !isStartAnim) {
            float x = 0.0F;
            float y = 0.0F;
            int r = radiusLength;//外圆半径
            int r1 = (int) (radiusLength*0.65f);//内圆半径
            int cx = position.x;//圆心x轴
            int cy = position.y;//圆心y轴
            switch (event.getAction()) {
                // 按下
                case MotionEvent.ACTION_DOWN:
                    x = event.getX();

                    y =(int) event.getY();
                    //利用勾股定理判断是否在圆内
                    if ((x - cx) * (x - cx) + (y - cy) * (y - cy) - r * r <= 0.0F && (x - cx) * (x - cx) + (y - cy) * (y - cy) - r1 * r1 >= 0.0F) {
                        if (selectPosition != getShowItem(getTouchedPointAngle(position.x, position.y, x, y))) {
                            moveToPosition(getShowItem(getTouchedPointAngle(position.x, position.y, x, y)));
                        }
                    }
                    break;
            }
        }
        return true;
    }


    /**
     * 旋转到指定位置
     *
     * @param i 第几个扇形
     */
    public void moveToPosition(int i) {
        if(data!=null) {
            selectPosition = i;
            isAnim = true;
            final float speedAngle = getSpeedAngle(i);
            new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < 300; i++) {
                        for (int j = 0; j < data.size(); j++) {
                            PieChartInfo data = AliPayPieView.this.data.get(j);
                            data.startAngle += speedAngle;
                            if (data.startAngle < 0) {
                                data.startAngle += 360;
                            }
                        }
                        SystemClock.sleep(2);
                        postInvalidate();
                    }
                    isAnim = false;
                }
            }.start();
        }
    }

    private float getSpeedAngle(int i) {
        if(data.size()>0) {
            PieChartInfo data = this.data.get(i);
            float v = data.startAngle + data.angle / 2;
            float v1 = v % 360;
            if (v1 < 0) {
                v1 = v1 + 360;
            }
            float f = 0;
            if (v1 > 90 && v1 < 270) {//如果是90-270逆时针转动
                f = -(v1 - 90) / 300;
            } else {
                if (v1 <= 90) {
                    f = (90 - v1) / 300;
                } else {
                    f = (450 - v1) / 300;
                }
            }
        return f;
        }
        return 0;
    }

    /**
     * @param touchAngle 触摸位置角度
     * @return
     * @throws
     * @Title: getShowItem
     * @Description: 拿到触摸位置
     */
    private int getShowItem(float touchAngle) {
        int position = 0;
        for (int i = 0; i < data.size(); i++) {
            PieChartInfo data = this.data.get(i);
            float v = data.startAngle % 360;
            if (v < 0) {
                v = v + 360;
            }
            if (v + data.angle > 360) {
                if (touchAngle + 360 > v && touchAngle + 360 < v + data.angle) {
                    position = i;
                    break;
                }
            }
            if (touchAngle > v && touchAngle < v + data.angle) {
                position = i;
                break;
            }
        }
        return position;
    }

    /**
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

    public static class PieChartInfo {
        public int id;
        public float angle;
        public int color;
        public float startAngle;
        public float radio;
        public String name;
    }
}
