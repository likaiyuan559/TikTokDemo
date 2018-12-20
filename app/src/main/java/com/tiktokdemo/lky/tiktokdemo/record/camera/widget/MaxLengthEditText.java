package com.tiktokdemo.lky.tiktokdemo.record.camera.widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.tiktokdemo.lky.tiktokdemo.R;


/**
 * Created by ydc on 2017/6/1.
 */
@SuppressLint("AppCompatCustomView") public class MaxLengthEditText extends EditText {
    protected int maxEdit;
    protected boolean isShowToast;
    protected String lastText;
    protected int beforeStart;
    protected int beforeAfter;
    protected boolean isCanInputLineFeed;

    public MaxLengthEditText(Context context) {
        super(context);
        init(context, null, 0);
    }

    public MaxLengthEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }


    public MaxLengthEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) {
            return;
        }
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MaxLengthEditText, defStyleAttr, 0);
        maxEdit = array.getInt(R.styleable.MaxLengthEditText_MaxLengthEditTextMaxLength, 15);
        isShowToast = array.getBoolean(R.styleable.MaxLengthEditText_MaxLengthEditTextShowToast, true);
        isCanInputLineFeed = array.getBoolean(R.styleable.MaxLengthEditText_MaxLengthIsCanInputLineFeed, true);
        initMaxListener();
    }

    public void setMaxEdit(int maxEdit) {
        this.maxEdit = maxEdit;
    }

    public void setIsShowToast(boolean isShowToast) {
        this.isShowToast = isShowToast;
    }

    public int getMaxEdit() {
        return maxEdit;
    }

    public boolean isShowToast() {
        return isShowToast;
    }

    protected void initMaxListener() {
        addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeStart = start;
                beforeAfter = after;
                lastText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();

                if(!isCanInputLineFeed && content.contains("\n")){
                    Pattern p = Pattern.compile("\r|\n");
                    Matcher m = p.matcher(content);
                    content = m.replaceAll("");
                    s.replace(0,s.length(),content,0,content.length());
                    content = s.toString();
                    if (isShowToast) {
//                        ToastTool.showShort(
//                                AppUtil.getApplicationContext(), AppUtil.getApplicationContext().getString(R.string.edit_text_max_length_hint_not_line_feed));
                    }
                }
                if (content.length() > maxEdit) {
                    String afterStr = content.substring(beforeStart,beforeStart+beforeAfter);
                    int residue = maxEdit - lastText.length();
                    if(containsEmoji(afterStr)){
                        residue = 0;
                    }
                    s.delete(beforeStart + residue,beforeStart + beforeAfter);
                    if (isShowToast) {
//                        ToastTool.showShort(AppUtil.getApplicationContext(), AppUtil.getApplicationContext().getString(R.string.edit_text_max_length_hint, maxEdit));
                    }
                }
            }
        });
    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { // 如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }


    /**
     * 判断是否是Emoji
     *
     * @param codePoint
     *            比较的单个字符
     * @return
     */
    private boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

}
