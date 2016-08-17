package com.arenas.droidfan.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arenas.droidfan.R;
import com.arenas.droidfan.data.db.FanFouDB;
import com.arenas.droidfan.data.model.Draft;

import java.util.List;

/**
 * Created by Arenas on 2016/8/14.
 */
public class DraftAdapter extends RecyclerView.Adapter<DraftAdapter.DraftViewHolder> {

    private Context context;
    private List<Draft> drafts;
    private OnDraftClickListener listener;

    public DraftAdapter(Context context, List<Draft> drafts , OnDraftClickListener listener) {
        this.context = context;
        this.drafts = drafts;
        this.listener = listener;
    }

    @Override
    public DraftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DraftViewHolder(LayoutInflater.from(context).inflate(R.layout.draft_item , parent , false));
    }

    @Override
    public void onBindViewHolder(DraftViewHolder holder, final int position) {
        final Draft draft = drafts.get(position);
        holder.text.setText(draft.text);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDraftItemClick(draft);
                FanFouDB.getInstance(context).deleteDraft(draft.id);
                removeItem(position);
            }
        });

        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                AlertDialog alertDialog = builder.setMessage(context.getString(R.string.delete_dialog_message))
                        .setNegativeButton(context.getString(R.string.cancel), null)
                        .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FanFouDB.getInstance(context).deleteDraft(draft.id);
                                removeItem(position);
                            }
                        }).create();
                alertDialog.show();
                return true;
            }
        });
    }

    public void removeItem(int position){
        drafts.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return drafts.size();
    }

    public void setData(List<Draft> data){
        drafts = data;
    }

    public void replaceData(List<Draft> data){
        setData(data);
        notifyDataSetChanged();
    }

    class DraftViewHolder extends RecyclerView.ViewHolder{
        LinearLayout container;
        TextView text;
        public DraftViewHolder(View itemView) {
            super(itemView);
            text = (TextView)itemView.findViewById(R.id.draft_text);
            container = (LinearLayout)itemView.findViewById(R.id.draft_container);
        }
    }
}
