package com.ego.adapters;




import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ego.chatit.R;
import com.ego.interfaces.IAdapterViewInflater;
import com.ego.model.Message;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * Created with IntelliJ IDEA. User: Justin Date: 10/6/13 Time: 12:47 AM
 */
public class MessageInflater implements IAdapterViewInflater<Message> {

	Context c;
	
	public MessageInflater(Context c){
		this.c = c;
	}

	@Override
	public View inflate(final BaseInflaterAdapter<Message> adapter,
			final int pos, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.layout_message, parent,false);
			holder = new ViewHolder(convertView);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Message message = adapter.getTItem(pos);
		holder.updateDisplay(message);

		return convertView;
	}

	private class ViewHolder {
		private View m_rootView;
		private TextView tvUsername;
		private TextView tvMessage;
		private TextView tvTimestamp;

		public ViewHolder(View rootView) {
			m_rootView = rootView;
			tvMessage = (TextView) m_rootView.findViewById(R.id.tvMessage);
			tvTimestamp = (TextView) m_rootView.findViewById(R.id.tvTimestamp);
			tvUsername = (TextView) m_rootView.findViewById(R.id.tvUsername);
			rootView.setTag(this);
		}

		public void updateDisplay(Message message) {
			tvMessage.setText(message.getMessage());
			tvUsername.setText(message.getName());
			tvTimestamp.setText(message.getDateSent().toString());
		}
	}
}
