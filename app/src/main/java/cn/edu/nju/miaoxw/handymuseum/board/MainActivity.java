package cn.edu.nju.miaoxw.handymuseum.board;

import android.app.Activity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity
{
	private TextView textViewDescription;
	private TextView textViewProblemDescription;
	private LinearLayout linearLayoutProblem;

	String descriptionString;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textViewDescription=(TextView)findViewById(R.id.textViewDescription);
		//textViewDescription.setHighlightColor(getResources().getColor(android.R.color.transparent));
		descriptionString="\u3000\u3000"+getString(R.string.description);
		SpannableString spannableString=new SpannableString(descriptionString);
		int start1902=descriptionString.indexOf("1902");
		int start1950=descriptionString.indexOf("1950");
		int start1952=descriptionString.indexOf("1952");
		int start1888=descriptionString.indexOf("1888");
		spannableString.setSpan(new Clickable(start1902,4),start1902,start1902+4,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableString.setSpan(new Clickable(start1950,4),start1950,start1950+4,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableString.setSpan(new Clickable(start1952,4),start1952,start1952+4,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableString.setSpan(new Clickable(start1888,4),start1888,start1888+4,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
		textViewDescription.setText(spannableString);
		textViewDescription.setMovementMethod(LinkMovementMethod.getInstance());

		linearLayoutProblem=(LinearLayout)findViewById(R.id.linearLayoutProblem);

		textViewProblemDescription=(TextView)findViewById(R.id.textViewQuestionDescription);
		String toFill=getString(R.string.problemDescription).replace("###","_______________");
		textViewProblemDescription.setText(toFill);
			}

	class Clickable extends ClickableSpan
	{
		private int start;
		private int length;

		public Clickable(int start,int length)
		{
			this.start=start;
			this.length=length;
		}

		@Override
		public void updateDrawState(TextPaint ds)
		{
			ds.setColor(getResources().getColor(android.R.color.black));
			ds.setUnderlineText(false);
		}

		@Override
		public void onClick(View widget)
		{
			String userAnswer=descriptionString.substring(start,start+length);
			String toFill=getString(R.string.problemDescription).replace("###",userAnswer);
			textViewProblemDescription.setText(toFill);
		}
	}
}
