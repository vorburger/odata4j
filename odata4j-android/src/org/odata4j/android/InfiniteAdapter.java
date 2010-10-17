package org.odata4j.android;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class InfiniteAdapter<T> extends BaseAdapter implements OnScrollListener{
    
    @SuppressWarnings("unused")
    private final AndroidLogger log = AndroidLogger.get(getClass());
    
    private final Context context;
    private final Iterator<T> iterator;
    private final int pageSize;
    
    private final List<T> items = new ArrayList<T>();
    
    public InfiniteAdapter(Context context, Iterator<T> iterator, int pageSize){
        this.context = context;
        this.iterator = iterator;
        this.pageSize = pageSize;
        getMore();
    }
    
    protected Context getContext(){
        return context;
    }

    // BaseAdapter
    @Override
    public int getCount() { return items.size(); }
    @Override
    public Object getItem(int pos) { return items.get(pos); }
    @Override
    public long getItemId(int pos) { return pos; }

    @Override
    public View getView(int pos, View v, ViewGroup p) {
            TextView view = new TextView(context);
            view.setText(getItem(pos).toString());
            return view;
    }
    

    
    @SuppressWarnings("unused")
    private int previousFirstVisibleItem;
    private boolean pendGetMore;
    
    // OnScrollListener
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //log.info("onScroll(%s,%s,%s)", firstVisibleItem, visibleItemCount, totalItemCount);
        pendGetMore = shouldGetMore(firstVisibleItem, visibleItemCount, totalItemCount);
        previousFirstVisibleItem = firstVisibleItem;
    }

    
    private boolean shouldGetMore(int firstVisibleItem, int visibleItemCount, int totalItemCount){
        //if (firstVisibleItem <=previousFirstVisibleItem)
        //   return false;
        
        int lastVisibleItem = firstVisibleItem + visibleItemCount;
        return lastVisibleItem > (totalItemCount - pageSize);
        //return lastVisibleItem >= totalItemCount;

    }
    
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(pendGetMore && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            getMore();
            this.notifyDataSetChanged();
        }
    }
    
        
     
    
    private void getMore(){
        
        //Toast toast = Toast.makeText(context, "Getting more " + System.currentTimeMillis(), Toast.LENGTH_SHORT);
        //toast.show();
        for(int i=0;i<pageSize;i++){
            if (!iterator.hasNext())
                break;
            items.add(iterator.next());
        }
        
    }
}