package com.cangjiedata.lib_widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cangjiedata.lib_widget.R;


public class PermissionDialog extends Dialog {

    private Context context = null;
    // 布局组件
    private View view = null;
    private Button btnOK, btnCancel;
    private TextView tvPermissionUser, tvPermission;
    private Display display;
    // 当前状态
    private String privacy, agreement;

    public PermissionDialog(Context context) {
        super(context, R.style.CommonDialogStyle);
        this.context = context;
        view = getLayoutInflater().inflate(R.layout.layout_dialog_permission, null);
        setContentView(view);
        // 初始化view
        intiView();
    }

    // 初始化view
    private void intiView() {
        btnOK = (Button) view.findViewById(R.id.btn_pos);
        btnCancel = view.findViewById(R.id.btnCancel);
        tvPermission = view.findViewById(R.id.tvPermission);
        tvPermissionUser = view.findViewById(R.id.tvPermissionUser);
    }

    public void setPrivacyUrlClickListener(View.OnClickListener listener) {
        this.tvPermission.setOnClickListener(listener);
    }

    public void setUserUrlClickListener(View.OnClickListener listener) {
        this.tvPermissionUser.setOnClickListener(listener);
    }

    public void setCancelButton(View.OnClickListener listener) {
        this.btnCancel.setOnClickListener(listener);
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getAgreement() {
        return agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }

    /**
     * 设置取消button
     *
     * @param listener
     */
    public void setOkButton(View.OnClickListener listener) {
        this.btnOK.setOnClickListener(listener);
    }

    @Override
    public void show() {
        // 设置dialog
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        // 设置dialog
    }
}