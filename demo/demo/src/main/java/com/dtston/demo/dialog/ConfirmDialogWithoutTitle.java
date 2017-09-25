package com.dtston.demo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.dtston.demo.R;

public class ConfirmDialogWithoutTitle extends Dialog implements View.OnClickListener,
		DialogInterface {

	private Button mBtCancel;
	private OnClickListener mBtCancelListener;
	private Button mBtOk;
	private OnClickListener mBtOkListener;
	private TextView mContent;
	private View mGap;

	public ConfirmDialogWithoutTitle(Context context, int okStrResid,
									 OnClickListener onClickListener) {
		super(context, R.style.Theme_CustomDialog);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		View contentView = LayoutInflater.from(context).inflate(
				R.layout.dialog_confirm_without_title, null);

		this.mContent = (TextView) contentView.findViewById(R.id.content);
		this.mBtOk = ((Button) contentView.findViewById(R.id.ok));
		this.mBtCancel = ((Button) contentView.findViewById(R.id.cancel));
		this.mGap = contentView.findViewById(R.id.gap);

		this.mBtOk.setOnClickListener(this);
		this.mBtCancel.setOnClickListener(this);

		if (okStrResid <= 0) {
			okStrResid = R.string.dialog_button_ok;
		}

		setButton(DialogInterface.BUTTON_POSITIVE,
				context.getString(okStrResid), onClickListener);

		setContentView(contentView);
	}

	public void dismiss() {
		super.dismiss();
	}

	public Button getButton(int paramInt) {
		if (paramInt == DialogInterface.BUTTON_POSITIVE)
			return this.mBtOk;
		if (paramInt == DialogInterface.BUTTON_NEGATIVE)
			return this.mBtCancel;
		return null;
	}

	public TextView getContent() {
		return this.mContent;
	}

	public void setContent(String content) {
		mContent.setText(content);
	}

	public void hideCancleButton() {
		this.mBtCancel.setVisibility(View.GONE);
		this.mGap.setVisibility(View.GONE);
		this.mBtOk.setBackgroundResource(R.drawable.selector_operate_btn_bottom_bg);
	}

	public void onClick(View paramView) {
		if ((paramView.getId() == R.id.ok)
				&& (this.mBtOkListener != null)) {
			this.mBtOkListener.onClick(this,
					DialogInterface.BUTTON_POSITIVE);
		} else if ((paramView.getId() == R.id.cancel)
				&& (this.mBtCancelListener != null)) {
			this.mBtCancelListener.onClick(this,
					DialogInterface.BUTTON_NEGATIVE);
		}
		cancel();
		dismiss();
	}

	@Override
	public void cancel() {
		if (this.mBtCancelListener != null) {
			this.mBtCancelListener.onClick(this,
					DialogInterface.BUTTON_NEGATIVE);
		}
		super.cancel();
	}

	public void setButton(int paramInt1, int paramInt2,
			OnClickListener paramOnClickListener) {
		setButton(paramInt1, getContext().getResources().getString(paramInt2),
				paramOnClickListener);
	}

	public void setButton(int paramInt, CharSequence paramCharSequence,
			OnClickListener paramOnClickListener) {
		if (paramInt == DialogInterface.BUTTON_POSITIVE) {
			this.mBtOk.setText(paramCharSequence);
			this.mBtOkListener = paramOnClickListener;
		}

		if (paramInt == DialogInterface.BUTTON_NEGATIVE) {
			this.mBtCancel.setText(paramCharSequence);
			this.mBtCancelListener = paramOnClickListener;
		}
	}

}
