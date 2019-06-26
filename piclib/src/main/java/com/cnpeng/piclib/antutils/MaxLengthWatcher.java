package com.cnpeng.piclib.antutils;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;

public class MaxLengthWatcher implements TextWatcher {
	private int maxLen;
	private EditText editText;
	private ImageView delImage;

	public MaxLengthWatcher(int maxLen, EditText editText, ImageView delImage) {
		this.maxLen = maxLen;
		this.editText = editText;
		this.delImage = delImage;
		delImage.setOnClickListener(new ImageClickListener());
		editText.setOnFocusChangeListener(new edittextFocusListener());
	}

	class ImageClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			editText.setText("");
		}
	}
	
	class edittextFocusListener implements OnFocusChangeListener{
	@Override
	public void onFocusChange(View arg0, boolean hasFocus) {
		if(hasFocus){//有焦点时
			if((editText.getText().toString().length())>0){
				delImage.setVisibility(View.VISIBLE);
			}
		} else {
			delImage.setVisibility(View.GONE);
		}
	}
}

	public void afterTextChanged(Editable arg0) {
	}

	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}

	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		delImage.setVisibility(View.VISIBLE);
		Editable editable = editText.getText();
		int len = editable.length();
		if (len == 0) {
			delImage.setVisibility(View.GONE); 
		}
		if (len > maxLen) {
			int selEndIndex = Selection.getSelectionEnd(editable);
			String str = editable.toString();
			// 截取新字符串
			String newStr = str.substring(0, maxLen);
			editText.setText(newStr);
			editable = editText.getText();
			// 新字符串的长度
			int newLen = editable.length();
			// 旧光标位置超过字符串长度
			if (selEndIndex > newLen) {
				selEndIndex = editable.length();
			}
			// 设置新光标所在的位置
			Selection.setSelection(editable, selEndIndex);
		}
	}
}