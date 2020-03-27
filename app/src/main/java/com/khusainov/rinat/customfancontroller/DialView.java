package com.khusainov.rinat.customfancontroller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author Rinat Khusainov on 2020-03-27
 * <p>
 * Custom view представляет собой мультипозиоционную шкалу.
 * Каждый клик переключает на следующую позицию.
 * По умолчанию 4 позиции (0-3): 0 = Off, 1 = Low, 2 = Medium, 3 = High.
 */
public class DialView extends View {

    private static int SELECTION_COUNT = 4; // Общее количество вариантов выбора
    private float mWidth;                   // Ширина Custom View
    private float mHeight;                  // Высота Custom View
    private Paint mTextPaint;               // Кисть для текста
    private Paint mDialPaint;               // Кисть для шкалы
    private float mRadius;                  // Радиус круга
    private int mActiveSelection;           // Активный выбор
    // Цвета устанавливаемые из атрибутов
    private int mFanOnColor;
    private int mFanOffColor;
    // Буфер для элементов шкалы и результатов
    private final StringBuffer mTempLabel = new StringBuffer(8);
    private final float[] mTempResult = new float[2];

    /**
     * Стандартный конструктор, вызывается когда custom view создается в java коде.
     *
     * @param context Контекст в котором запущено view, через которое можно получить
     *                доступ к текущей теме, ресурсам итд.
     */
    public DialView(Context context) {
        super(context, null);
    }

    /**
     * Данный конструктор вызывается, когда view создается из XML файла,
     * предоставляя атрибуты, которые указаны в XML файле.
     *
     * @param context Контекст в котором запущено view, через которое можно получить
     *                доступ к текущей теме, ресурсам итд.
     * @param attrs   Атрибуты XML, заполняющие view.
     */
    public DialView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * @param context      Контекст в котором запущено view, через которое можно получить
     *                     доступ к текущей теме, ресурсам итд.
     * @param attrs        Атрибуты XML, заполняющие view.
     * @param defStyleAttr Атрибуты стиля по умолчанию.
     */
    public DialView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * Вспомогательный метод для инициализации экземпляров переменных.
     * Вызывается конструкторами.
     */
    private void init(Context context, AttributeSet attrs) {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(40f);

        // Устанааливить цвета по умолчанию для включения выключения
        mFanOnColor = Color.CYAN;
        mFanOffColor = Color.GRAY;

        mDialPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDialPaint.setColor(mFanOffColor);
        mActiveSelection = 0;

        // Получить кастомные атрибуты (fanOnColor и fanOffColor) если доступны
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DialView, 0, 0);

            // Установить цвета из значений аттрибутов
            mFanOnColor = typedArray.getColor(R.styleable.DialView_fanOnColor, mFanOnColor);
            mFanOffColor = typedArray.getColor(R.styleable.DialView_fanOffColor, mFanOffColor);
            typedArray.recycle();
        }

        // Установить onClickListener для DialView
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Прокрутить к следующему значению
                mActiveSelection = (mActiveSelection + 1) % SELECTION_COUNT;
                // Установить фон шкалы на зеленый, если selection >= 1
                if (mActiveSelection >= 1) {
                    mDialPaint.setColor(mFanOnColor);
                } else {
                    mDialPaint.setColor(mFanOffColor);
                }
                // Перерисовать view
                invalidate();
            }
        });
    }

    /**
     * Вызывается когда размер данной view изменился.
     * Если view было просто добавлено в иерархию view, оно вызовется с начальным значением 0.
     * Код определяет границы рисования для custom view.
     *
     * @param w    Текущая ширина данной view
     * @param h    Текущая высота данной view
     * @param oldw Начальная ширина данной view
     * @param oldh Начальная высота данной view
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Вычислить радиус в зависимости от значений ширины и высоты
        mWidth = w;
        mHeight = h;
        mRadius = (float) (Math.min(mWidth, mHeight) / 2 * 0.8);
    }

    /**
     * Вызывается для отрисовки элементов view.
     * Класс Canvas определяет методы для рисования текста, линий, растровых изображений
     * и многих других графических примитивов.
     *
     * @param canvas Холст, на котором будет нарисован фон.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Нарисовать шкалу
        canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mDialPaint);

        // Нарисовать текстовые значения
        final float labelRadius = mRadius + 20;
        StringBuffer label = mTempLabel;

        for (int i = 0; i < SELECTION_COUNT; i++) {
            float[] xyData = computeXYForPosition(i, labelRadius);
            float x = xyData[0];
            float y = xyData[1];
            label.setLength(0);
            label.append(i);
            canvas.drawText(label, 0, label.length(), x, y, mTextPaint);
        }

        // Нарисовать индикатор
        final float markerRadius = mRadius - 35;
        float[] xyData = computeXYForPosition(mActiveSelection, markerRadius);
        float x = xyData[0];
        float y = xyData[1];
        canvas.drawCircle(x, y, 20, mTextPaint);
    }

    /**
     * Вычисляет X/Y-координаты для текстового значения и индикатора,
     * предоставляет позицию номера и радиус
     *
     * @param pos    Нулевой индекс позиции
     * @param radius Радиус где будет отрисовано текстовое значение или индикатор.
     * @return Массив из 2-х элементов. 0 - координата X, 1 - координата Y.
     */
    private float[] computeXYForPosition(final int pos, final float radius) {
        float[] result = mTempResult;
        Double startAngle = Math.PI * (9 / 8d);
        Double angle = startAngle + (pos * (Math.PI / 4));
        result[0] = (float) (radius * Math.cos(angle)) + (mWidth / 2);
        result[1] = (float) (radius * Math.sin(angle)) + (mHeight / 2);
        return result;
    }
}
