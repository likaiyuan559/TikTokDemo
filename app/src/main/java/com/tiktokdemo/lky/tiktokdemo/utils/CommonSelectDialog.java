package com.tiktokdemo.lky.tiktokdemo.utils;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tiktokdemo.lky.tiktokdemo.R;

/**
 * Created by 1 on 2016/10/13.
 */
public class CommonSelectDialog {

    public static void showList(Context context, int which, final OnItemSelectedListener listener, List<String> items) {
        show(context, which, "", listener, (String[]) items.toArray());
    }

    public static Dialog show(Context context, int which, final OnItemSelectedListener listener, final String... items) {
        return show(context, which, "", listener,null, items);
    }

    public static Dialog show(Context context, int which, String title, final OnItemSelectedListener listener, final String... items){
        return show(context, which, "", listener, null,items);
    }

    public static Dialog show(Context context, int which, final OnItemSelectedListener listener, final OnCancelListener cancelListener, final String... items){
        return show(context, which, "", listener, cancelListener,items);
    }

    public static Dialog show(Context context, int which, String title, final OnItemSelectedListener listener, final OnCancelListener cancelListener, final String... items) {
    final AlertDialog dialog = new AlertDialog.Builder(context, R.style.dialog_bond).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_common_select, null);
        View line = view.findViewById(R.id.view_line);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        if (TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }
        final LinearLayout linItems = (LinearLayout) view.findViewById(R.id.lin_items);
//        linItems.removeAllViews();
        for (int i = 0; i < items.length; i++) {
            final int j = i;
            String str = items[i];
            View itemView = inflater.inflate(R.layout.item_common_select, null);
            TextView tvName = (TextView) itemView.findViewById(R.id.tv_item_name);
            ImageView imgSelected = (ImageView) itemView.findViewById(R.id.img_selected);
            View lineView = itemView.findViewById(R.id.view_line);
            if (i == items.length - 1) {
                lineView.setVisibility(View.INVISIBLE);
            } else {
                lineView.setVisibility(View.VISIBLE);
            }
            tvName.setText(str);
            if (j == which) {
                imgSelected.setVisibility(View.VISIBLE);
            } else {
                imgSelected.setVisibility(View.INVISIBLE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int k = 0; k < items.length; k++) {
                        View view = linItems.getChildAt(k);
                        ImageView imgSelected = (ImageView) view.findViewById(R.id.img_selected);
                        if (k == j) {
                            imgSelected.setVisibility(View.VISIBLE);
                        } else {
                            imgSelected.setVisibility(View.INVISIBLE);
                        }
                    }
                    listener.onItemSelected(j);
                    dialog.dismiss();
//                    dialog = null;
                }
            });
            linItems.addView(itemView);
        }

        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(cancelListener != null){
                    cancelListener.onCancel();
                }
//                dialog = null;
            }
        });
        dialog.show();
        dialog.setContentView(view);
        Window win = dialog.getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);
        win.setGravity(Gravity.BOTTOM);
//        win.setWindowAnimations(R.style.mystyle);  //

        return dialog;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int which);
    }

    public interface OnCancelListener{
        void onCancel();
    }

}
