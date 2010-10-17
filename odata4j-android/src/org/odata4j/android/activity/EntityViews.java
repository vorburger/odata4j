package org.odata4j.android.activity;

import java.net.URL;
import java.util.List;

import org.odata4j.android.AndroidLogger;
import org.odata4j.android.MultipleLinkSpannableFactory;
import org.odata4j.android.R;
import org.odata4j.android.model.SerializableORelatedEntitiesLink;
import org.odata4j.android.model.SerializableORelatedEntityLink;
import org.odata4j.android.model.ServiceVM;
import org.odata4j.core.AtomInfo;
import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperty;
import org.odata4j.core.ORelatedEntitiesLink;
import org.odata4j.core.ORelatedEntityLink;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class EntityViews {
    
    private static final AndroidLogger log = AndroidLogger.get(EntityViews.class);
    
    public static TableLayout newEntityTable(final Context context, OEntity entity, final ServiceVM service){
        TableLayout layout = new TableLayout(context);
        layout.setBackgroundResource(R.layout.roundedcorner);
        
        layout.setColumnShrinkable(1, true);
        layout.setPadding(3,3,3,3);
        
        // header
        TextView header = new TextView(context);
        header.setText(computeName(entity));
        header.setTextSize(16);
        layout.addView(header);
        
        // separator
        View sep = new View(context);
        sep.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,1));
        sep.setBackgroundResource(android.R.color.darker_gray);
        layout.addView(sep);
        
        // properties
        addProperties(context,layout,entity.getProperties(),0);
        
        
        // links
        TextView linksView = new TextView(context);
        linksView.setPadding(10, 5, 10, 5);
        linksView.setGravity(Gravity.CENTER_HORIZONTAL);
        MultipleLinkSpannableFactory spannables = new MultipleLinkSpannableFactory();
        for(final OLink link : entity.getLinks()){
            if (link instanceof ORelatedEntitiesLink){
                spannables.append(link.getTitle(), new OnClickListener() {  
                    public void onClick(View v) {  
                        log.info("link " + link.getHref());
                        context.startActivity(new Intent(context,EntitiesActivity.class)
                            .putExtra("service",service)
                            .putExtra("link", new SerializableORelatedEntitiesLink((ORelatedEntitiesLink)link)));
                    }  
                });
            }
            if (link instanceof ORelatedEntityLink){
                spannables.append(link.getTitle(), new OnClickListener() {  
                    public void onClick(View v) {  
                        context.startActivity(new Intent(context,EntityActivity.class)
                            .putExtra("service",service)
                            .putExtra("link", new SerializableORelatedEntityLink((ORelatedEntityLink)link)));
                    }  
                });
            }
        }
        spannables.apply(linksView);
        layout.addView(linksView);
        return layout;
    }
    
    private static String computeName(OEntity entity){
        if (entity instanceof AtomInfo)
            return ((AtomInfo)entity).getTitle();
        
        throw new UnsupportedOperationException("unable to compute name for " + entity);
    }
    
    
    @SuppressWarnings("unchecked")
    private static void addProperties(Context context, TableLayout layout, List<OProperty<?>> properties,int indentLevel){
       
        for(OProperty<?> prop : properties){
            
            if (prop.getValue()==null)
                continue;
            
            TableRow row = new TableRow(context);
            TextView label = new TextView(context);
            label.setText(prop.getName());
            label.setWidth(100);
            label.setEllipsize(TruncateAt.END);
            if (indentLevel>0){
                label.setPadding(indentLevel*10, 0, 0, 0);
            }
            row.addView(label);
            
            if (prop.getValue() instanceof List<?>){
                // render complex type
                layout.addView(row);
                addProperties(context, layout, (List<OProperty<?>>)prop.getValue(),indentLevel+1);
            
            }  else {
                if (prop.getValue()!=null){
                    String sv = prop.getValue().toString();
                    
                    // render embedded images (disabled for now)
                    if (false && sv.startsWith("http://") && sv.endsWith(".jpg")){
                        ImageView value = new ImageView(context);
                        try {
                            value.setImageDrawable(android.graphics.drawable.Drawable.createFromStream(new URL(sv).openStream(),"test"));
                        } catch (Exception e) {
                           throw new RuntimeException(e);
                        }
                        //value.setScaleType(ScaleType.CENTER);
                       // value.setImageURI(Uri.parse(sv));
                        row.addView(value);
                        
                    } else {
                        
                        // render content, detect html
                        TextView value = new TextView(context);
                        if (sv.contains("<a ")||sv.contains("<p>")||sv.contains("<P>")||sv.contains("<br>")||sv.contains("<br/>")||sv.contains("<img ")) {
                            value.setText(Html.fromHtml(sv));
                            value.setMovementMethod(LinkMovementMethod.getInstance());
                        } else if (sv.startsWith("http://")){
                            value.setText(sv);
                            Linkify.addLinks(value, Linkify.WEB_URLS);
                        }
                        else
                            value.setText(sv);
                      
                        row.addView(value);
                    }
                }
                layout.addView(row);
            } 
        }
    }
}
