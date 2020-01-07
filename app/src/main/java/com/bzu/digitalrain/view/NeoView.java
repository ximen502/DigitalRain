package com.bzu.digitalrain.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.bzu.digitalrain.R;

import java.util.Random;

public class NeoView extends View {

    class Symbol{
        int x;
        int y;
        String value;
        int speed;
        boolean first;
        int opacity;
        int switchInternal = interval[random.nextInt(interval.length)];//random.nextInt(25-2)+2;

        public Symbol(int x, int y, int speed, boolean first, int opacity) {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.first = first;
            this.opacity = opacity;
        }

        private void setToRandomSymbol(){
            if (frameCount % switchInternal == 0){
                this.value = getChar();
            }
        }

        private String getChar(){
            return katakana[random.nextInt(96)];
            /*
            char charItem = (char) (0x30a0 + (random.nextInt(96)));
            return String.valueOf(charItem);
            */
            /*
            char[] array = new char[62];
            for (int i=0x30;i<=0x39;i++){
                array[i&0x0f]=(char) i;
            }
            int data = 0x41;
            for (int i=10;i<=35;i++){
                array[i] = (char)data;
                data++;
            }
            data = 0x61;
            for (int i=36;i<=61;i++){
                array[i] = (char)data;
                data++;
            }
            //System.out.println(Arrays.toString(array));
            //[0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            // A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,
            // a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z]
            return String.valueOf(array[random.nextInt(array.length)]);
            */
        }

        @Override
        public String toString() {
            return "Symbol{" +
                    "x=" + x +
                    ", y=" + y +
                    ", value='" + value + '\'' +
                    ", speed=" + speed +
                    '}';
        }
    }

    Random random = new Random();
    Paint mPaint;
    Paint mPaintLight;
    final int TEXT_SIZE = 24;
    int x = 0, y = 0;
    int mWidth = 0;
    int mHeight = 0;
    int frameCount =0;
    int[] interval = {9,11,17,23,29};
    float fadeInterval = 1.6f;
    final int DEFAULT_TEXT_COLOR = Color.argb(255, 0, 255, 70);
    int textColor;
    int a;
    int r;
    int g;
    int b;

    Object[] streams;
    String[] katakana;

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setARGB(255, 0, 255, 70);
        mPaint.setTextSize(TEXT_SIZE);
        a = textColor >> 24 & 0xff;
        r = textColor >> 16 & 0xff;
        g = textColor >> 8 & 0xff;
        b = textColor & 0xff;
        mPaint.setARGB(a, r, g, b);

        mPaintLight = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLight.setARGB(255, 140, 255, 170);
        mPaintLight.setTextSize(TEXT_SIZE);

        //Log.i("ndkapp", getChar());

    }

    public NeoView(Context context) {
        this(context, null);
    }

    public NeoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NeoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NeoView,defStyleAttr, 0);
        textColor = a.getColor(R.styleable.NeoView_textColor, DEFAULT_TEXT_COLOR);
        a.recycle();
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(widthMode == MeasureSpec.EXACTLY){
            mWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mHeight = heightSize;
        }
        setMeasuredDimension((int)mWidth, (int)mHeight);

        setUp();
    }

    private void setUp() {
        if (streams == null && mWidth > 0) {
            streams = new Object[mWidth / TEXT_SIZE];
        } else {
            // 防止重复初始化数组，避免框架层对onMeasure多次调用，造成重复创建对象。
            return;
        }

        // 初始化katakana字符数组
        if (katakana == null) {
            katakana = new String[96];
        }
        for (int i = 0; i < 96; i++){
            katakana[i] = String.valueOf((char) (0x30a0 + i));
        }

        for (int j = 0; j < streams.length; j++) {
            int opacity = 255;
            int length = random.nextInt(35 - 5) + 5;
            int speed = random.nextInt(22 - 5) + 5;
            Symbol[] symbols = new Symbol[length];
            int sx = this.x + j * TEXT_SIZE;
            boolean first = random.nextInt(100) < 45;
            this.y = - random.nextInt(500);
            for (int i = 0; i < length; i++) {
                Symbol symbol = new Symbol(sx, y - (i * TEXT_SIZE), speed, first, opacity);
                //symbol.value = getChar();
                symbol.setToRandomSymbol();
                symbols[i] = symbol;
                opacity -= (255 / symbols.length) / fadeInterval;
                first = false;
            }

            streams[j] = symbols;
        }
    }

    private void render(Canvas canvas){
        canvas.drawColor(Color.BLACK);
        for (int j = 0; j < streams.length; j++) {
            Symbol[] symbols = (Symbol[]) streams[j];
            for (int i = 0; i < symbols.length; i++) {
                Symbol symbol = symbols[i];
                //Log.i("xsc", symbol.toString());
                if (symbol.first){
                    mPaintLight.setARGB(symbol.opacity, 140, 255, 170);
                    canvas.drawText(symbol.value, symbol.x, symbol.y, mPaintLight);
                } else {
                    mPaint.setARGB(symbol.opacity, r, g, b);
                    canvas.drawText(symbol.value, symbol.x, symbol.y, mPaint);
                }
            }
            setSymbols(symbols);
        }
        invalidate();
        frameCount++;
    }

    private void rain(){
        for (int j = 0; j < streams.length; j++) {
            Symbol[] symbols = (Symbol[]) streams[j];
            for (int i = 0; i < symbols.length; i++) {
                Symbol symbol = symbols[i];
                symbol.y = symbol.y >= mHeight ? 0 : symbol.y+symbol.speed;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        render(canvas);
        rain();
    }


    private void setSymbols(Symbol[] symbols){
        for (int i = 0; i < symbols.length; i++) {
            Symbol symbol = symbols[i];
            symbol.setToRandomSymbol();
        }
        /*if (Math.abs(frameCount) % REFRESH_RATE == 0){
        }*/
    }
}
