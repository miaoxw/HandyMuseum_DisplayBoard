package cn.edu.nju.miaoxw.handymuseum.board;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends Activity
{
	private TextView textViewCountdown;
	private TextView textViewDescription;
	private TextView textViewProblemDescription;
	private LinearLayout linearLayoutProblem;
	private ImageView imageViewAnswerResult;

	private Handler handler;

	private String descriptionString;
	private Thread currentCountdownThread=null;

	private boolean rightAnswer=false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textViewCountdown=(TextView)findViewById(R.id.textViewCountdown);
		imageViewAnswerResult=(ImageView)findViewById(R.id.imageViewAnswerResult);

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

		handler=new Handler();
		new Thread(new NetworkMonitor()).start();
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

			if(currentCountdownThread!=null)
			{
				rightAnswer=userAnswer.equals("1902");
				currentCountdownThread.interrupt();
			}
		}
	}

	public class NetworkMonitor implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				ServerSocket serverSocket=new ServerSocket(22222);

				while(true)
				{
					Socket socket=serverSocket.accept();
					ObjectOutputStream objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
					rightAnswer=false;
					handler.post(new Runnable()
					{
						@Override
						public void run()
						{
							String toFill=getString(R.string.problemDescription).replace("###","_______________");
							textViewProblemDescription.setText(toFill);
							linearLayoutProblem.setVisibility(View.VISIBLE);
						}
					});

					currentCountdownThread=new Thread(new Countdown());
					currentCountdownThread.start();
					try
					{
						currentCountdownThread.join();
					}
					catch(InterruptedException e)
					{
					}

					handler.post(new Runnable()
					{
						@Override
						public void run()
						{
							if(rightAnswer)
								imageViewAnswerResult.setImageResource(android.R.drawable.star_on);
							else
								imageViewAnswerResult.setImageResource(android.R.drawable.ic_delete);
							imageViewAnswerResult.setVisibility(View.VISIBLE);
						}
					});
					objectOutputStream.writeObject(new Boolean(rightAnswer));
					objectOutputStream.flush();
					objectOutputStream.close();
					socket.close();

					try
					{
						Thread.sleep(1000);
					}catch(InterruptedException e)
					{
					}

					handler.post(new Runnable()
					{
						@Override
						public void run()
						{
							imageViewAnswerResult.setVisibility(View.INVISIBLE);
							linearLayoutProblem.setVisibility(View.INVISIBLE);
						}
					});

					currentCountdownThread=null;
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public class Countdown implements Runnable
	{
		private int countdown;

		public Countdown()
		{
			countdown=30;
		}

		@Override
		public void run()
		{
			try
			{
				for(;countdown>=0;countdown--)
				{
					handler.post(new Runnable()
					{
						@Override
						public void run()
						{
							textViewCountdown.setText(Integer.toString(countdown));
						}
					});
					Thread.sleep(1000);
				}
			}
			catch(InterruptedException e)
			{
				//中断意味着提前完成了回答，什么都不用管
			}
		}
	}
}
