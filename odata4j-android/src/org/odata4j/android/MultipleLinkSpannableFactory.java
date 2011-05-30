package org.odata4j.android;

import java.util.ArrayList;
import java.util.List;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MultipleLinkSpannableFactory extends Spannable.Factory {

  private List<InternalLink> links = new ArrayList<InternalLink>();

  @Override
  public Spannable newSpannable(CharSequence source) {
    SpannableString ss = new SpannableString(source);
    int start = 0;
    for (InternalLink info : links) {
      ss.setSpan(new InternalURLSpan(info.listener), start, start + info.caption.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      start = start + info.caption.length() + 1;
    }
    return ss;
  }

  public void apply(TextView view) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (InternalLink link : links) {
      if (first)
        first = false;
      else
        sb.append(" ");
      sb.append(link.caption);
    }
    view.setSpannableFactory(this);
    view.setText(sb.toString());
    view.setMovementMethod(LinkMovementMethod.getInstance());
  }

  public void append(String caption, OnClickListener listener) {
    InternalLink link = new InternalLink();
    link.caption = caption;
    link.listener = listener;
    links.add(link);
  }

  private static class InternalLink {
    public String caption;
    public OnClickListener listener;
  }

  private static class InternalURLSpan extends ClickableSpan {
    private final OnClickListener listener;

    public InternalURLSpan(OnClickListener listener) {
      this.listener = listener;
    }

    @Override
    public void onClick(View widget) {
      listener.onClick(widget);
    }
  }

}