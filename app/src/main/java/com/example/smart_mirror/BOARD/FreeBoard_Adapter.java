package com.example.smart_mirror.BOARD;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smart_mirror.R;

import java.util.ArrayList;

public class FreeBoard_Adapter extends RecyclerView.Adapter<FreeBoard_Adapter.CustomViewHolder> {

    // listView의 item들을 담을 ArrayList
    // Board를 가져옴
    private ArrayList<Board> arrayList;

    public FreeBoard_Adapter(ArrayList<Board> arrayList) {
        this.arrayList = arrayList;
    }


    // 처음으로 생성될 때의 생성주기!! -> onCreate와 유사
    @NonNull
    @Override
    public FreeBoard_Adapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    // 실제 추가될 때의 생명주기!!
    @Override
    public void onBindViewHolder(@NonNull final FreeBoard_Adapter.CustomViewHolder holder, int position) {

        Board item = arrayList.get(position);

        holder.recycler_Title   .setText(item.getTitle());
        holder.recycler_Title   .setTypeface(Typeface.DEFAULT_BOLD);
        holder.recycler_Content .setText(item.getContent());


        // list Click Listener
        holder.itemView.setTag(position);       // position 값 가져오기.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RecyclerView에서 클릭한 항목의 이름을 가져옴.
                String curName = holder.recycler_Title.getText().toString();
//                Toast.makeText(v.getContext(), curName, Toast.LENGTH_SHORT).show();     //activity가 아니기 때문에, v.getContext()

                Intent intent = new Intent(v.getContext(), BoardRead_Activity.class);

                intent.putExtra("TITLE", holder.recycler_Title.getText().toString());
                intent.putExtra("CONTENT", holder.recycler_Content.getText().toString());

                v.getContext().startActivity(intent);
            }
        });

//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                remove(holder.getAdapterPosition());
//
//                return false;
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

//    public void remove(int position) {
//        try {
//            arrayList.remove(position);
//            notifyItemRemoved(position);
//
//        } catch (IndexOutOfBoundsException e) {
//            e.printStackTrace();
//        }
//    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {


        //        protected ImageView recycler_IMG;
        protected TextView recycler_Title;
        protected TextView recycler_Content;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            recycler_Title = (TextView) itemView.findViewById(R.id.recycler_Title);
            recycler_Content = (TextView) itemView.findViewById(R.id.recycler_Content);

        }

        public TextView getRecycler_Title() {
            return recycler_Title;
        }
        public TextView getRecycler_Content() {
            return recycler_Content;
        }
//        public ImageView getRecycler_IMG() {
//            return recycler_IMG;
//        }
    }
}
