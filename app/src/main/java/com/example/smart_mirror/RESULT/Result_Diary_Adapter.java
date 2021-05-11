package com.example.smart_mirror.RESULT;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smart_mirror.R;

import java.util.ArrayList;

public class Result_Diary_Adapter extends RecyclerView.Adapter<Result_Diary_Adapter.CustomViewHolder> {

    private ArrayList<Result> arrayList;

    public Result_Diary_Adapter(ArrayList<Result> arrayList) {
        this.arrayList = arrayList;
    }

    // 처음으로 생성될 때의 생성주기!! -> onCreate와 유사
    @NonNull
    @Override
    public Result_Diary_Adapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_diary_recyclerview, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    // 실제 추가될 때의 생명주기!!
    @Override
    public void onBindViewHolder(@NonNull final Result_Diary_Adapter.CustomViewHolder holder, int position) {

        Result item = arrayList.get(position);

        int High_Grade_score    = Integer.parseInt(item.getHigh_Grade());
        int Middle_Grade_Score  = Integer.parseInt(item.getMiddle_Grade());
        int Low_Grade_Score     = Integer.parseInt(item.getLow_Grade());
        int Good_Grade_Score    = Integer.parseInt(item.getGood_Grade());

        int Total_ProgressRate = 0;

        // 날짜 입력
        holder.recycler_Date.setText(item.getDate());

        /*
        * 고위험으로 진단 되었을 경우
        * */
        if (High_Grade_score > Middle_Grade_Score
                && High_Grade_score > Low_Grade_Score
                && High_Grade_score > Good_Grade_Score) {

            Total_ProgressRate = 91;

            // 고위험 군으로 선정되고 진행률이 0 ~ 20% 사이일 때
            if (High_Grade_score >= 0 && High_Grade_score <= 20) {
                // 총 진행률 92%
                Total_ProgressRate += 1;
            }
            else if (High_Grade_score >= 21 && High_Grade_score <= 40) {
                // 총 진행률 94%
                Total_ProgressRate += 3;
            }
            else if (High_Grade_score >= 41 && High_Grade_score <= 60) {
                // 총 진행률 96%
                Total_ProgressRate += 5;
            }
            else if (High_Grade_score >= 61 && High_Grade_score <= 80) {
                // 총 진행률 98%
                Total_ProgressRate += 7;
            }
            else if (High_Grade_score >= 81 && High_Grade_score <= 100) {
                // 총 진행률 100%
                Total_ProgressRate += 9;
            }

            holder.recycler_Grade               .setText("고위험");
            holder.recycler_ProgressRate_Score  .setText(Total_ProgressRate + " %");
        }


        /*
         * 위험으로 진단 되었을 경우
         * */
        if (Middle_Grade_Score > High_Grade_score
                && Middle_Grade_Score > Low_Grade_Score
                && Middle_Grade_Score > Good_Grade_Score) {

            Total_ProgressRate = 71;

            // 위험 군으로 선정되고 진행률이 0 ~ 20% 사이일 때
            if (Middle_Grade_Score >= 0 && Middle_Grade_Score <= 20) {
                // 총 진행률 75%
                Total_ProgressRate += 4;
            }
            else if (Middle_Grade_Score >= 21 && Middle_Grade_Score <= 40) {
                // 총 진행률 79%
                Total_ProgressRate += 8;
            }
            else if (Middle_Grade_Score >= 41 && Middle_Grade_Score <= 60) {
                // 총 진행률 83%
                Total_ProgressRate += 12;
            }
            else if (Middle_Grade_Score >= 61 && Middle_Grade_Score <= 80) {
                // 총 진행률 87%
                Total_ProgressRate += 16;
            }
            else if (Middle_Grade_Score >= 81 && Middle_Grade_Score <= 100) {
                // 총 진행률 90%
                Total_ProgressRate += 19;
            }

            holder.recycler_Grade               .setText("위험");
            holder.recycler_ProgressRate_Score  .setText(Total_ProgressRate + " %");

        }


        /*
         * 경고로 진단 되었을 경우
         * */
        if (Low_Grade_Score > High_Grade_score
                && Low_Grade_Score > Middle_Grade_Score
                && Low_Grade_Score > Good_Grade_Score) {

            Total_ProgressRate = 41;

            // 위험 군으로 선정되고 진행률이 0 ~ 20% 사이일 때
            if (Low_Grade_Score >= 0 && Low_Grade_Score <= 20) {
                // 총 진행률 47%
                Total_ProgressRate += 6;
            }
            else if (Low_Grade_Score >= 21 && Low_Grade_Score <= 40) {
                // 총 진행률 53%
                Total_ProgressRate += 12;
            }
            else if (Low_Grade_Score >= 41 && Low_Grade_Score <= 60) {
                // 총 진행률 59%
                Total_ProgressRate += 18;
            }
            else if (Low_Grade_Score >= 61 && Low_Grade_Score <= 80) {
                // 총 진행률 65%
                Total_ProgressRate += 24;
            }
            else if (Low_Grade_Score >= 81 && Low_Grade_Score <= 100) {
                // 총 진행률 70%
                Total_ProgressRate += 29;
            }

            holder.recycler_Grade               .setText("경고");
            holder.recycler_ProgressRate_Score  .setText(Total_ProgressRate + " %");

        }

        /*
         * 좋음으로 진단 되었을 경우
         * */
        if (Good_Grade_Score > High_Grade_score
                && Good_Grade_Score > Low_Grade_Score
                && Good_Grade_Score > Middle_Grade_Score) {

            Total_ProgressRate = 0;

            // 위험 군으로 선정되고 진행률이 0 ~ 20% 사이일 때
            if (Good_Grade_Score >= 0 && Good_Grade_Score <= 20) {
                // 총 진행률 0%
                Total_ProgressRate += 0;
            }
            else if (Good_Grade_Score >= 21 && Good_Grade_Score <= 40) {
                // 총 진행률 10%
                Total_ProgressRate += 10;
            }
            else if (Good_Grade_Score >= 41 && Good_Grade_Score <= 60) {
                // 총 진행률 20%
                Total_ProgressRate += 20;
            }
            else if (Good_Grade_Score >= 61 && Good_Grade_Score <= 80) {
                // 총 진행률 30%
                Total_ProgressRate += 30;
            }
            else if (Good_Grade_Score >= 81 && Good_Grade_Score <= 100) {
                // 총 진행률 40%
                Total_ProgressRate += 40;
            }

            holder.recycler_Grade               .setText("좋음");
            holder.recycler_ProgressRate_Score  .setText(Total_ProgressRate + " %");

        }

        // list Click Listener
//        holder.itemView.setTag(position);       // position 값 가져오기.
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // RecyclerView에서 클릭한 항목의 이름을 가져옴.
//                String curName = holder.recycler_Date.getText().toString();
//
//                Intent intent = new Intent(v.getContext(), BoardRead_Activity.class);
//
//                intent.putExtra("HairLossRate_Score", holder.recycler_HairLossRate_Score.getText().toString());
//                intent.putExtra("NoneHairLossRate_Score", holder.recycler_NoneHairLossRate_Score.getText().toString());
//
//                v.getContext().startActivity(intent);
//            }
//        });

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

    public void remove(int position) {
        try {
            arrayList.remove(position);
            notifyItemRemoved(position);

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {


         protected ImageView recycler_IMG;
//        protected TextView recycler_HairLossRate_Score;
//        protected TextView recycler_NoneHairLossRate_Score;
        protected TextView recycler_Date;
        protected TextView recycler_Grade;
        protected TextView recycler_ProgressRate_Score;
        protected String recycler_High_Grade;
        protected String recycler_Middle_Grade;
        protected String recycler_Low_Grade;
        protected String recycler_Good_Grade;

        public String getRecycler_High_Grade() {
            return recycler_High_Grade;
        }

        public String getRecycler_Middle_Grade() {
            return recycler_Middle_Grade;
        }

        public String getRecycler_Low_Grade() {
            return recycler_Low_Grade;
        }

        public String getRecycler_Good_Grade() {
            return recycler_Good_Grade;
        }

        public CustomViewHolder(@NonNull View itemView) {

            super(itemView);

            recycler_Grade                  = (TextView) itemView.findViewById(R.id.recycler_Grade);
            recycler_ProgressRate_Score     = (TextView) itemView.findViewById(R.id.recycler_ProgressRate_Score);
            recycler_Date                   = (TextView) itemView.findViewById(R.id.recycler_Date);

        }

//        public TextView getRecycler_HairLossRate_Score() {
//            return recycler_HairLossRate_Score;
//        }
//        public TextView getRecycler_NoneHairLossRate_Score() {
//            return recycler_NoneHairLossRate_Score;
//        }
//        public ImageView getRecycler_IMG() {
//            return recycler_IMG;
//        }
    }
}
