package android.mxdlzg.com.windmill.adapter;

import android.content.Context;
import android.mxdlzg.com.windmill.R;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 廷江 on 2017/5/1.
 */

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ViewHolder>{
    private List<String> exams;
    private Context context;
    private int thisWeek = 1;

    public ExamAdapter(List<String> exams, Context context) {
        this.exams = exams;
        this.context = context;
    }

    public void setExams(List<String> exams) {
        this.exams = exams;
    }

    public void setThisWeek(int thisWeek) {
        this.thisWeek = thisWeek;
    }

    @Override
    public ExamAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exam_rcy_item,parent,false);
        return new ExamAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExamAdapter.ViewHolder holder, int position) {
        String exam = exams.get(position);
        String[] info = exam.split(";");
        String[] smash_time = info[1].split(" ");
        holder.name.setText(info[0]);
        holder.time.setText(info[1]);
        holder.place.setText(info[2]);
        holder.remain_time.setText("20天");
    }

    @Override
    public int getItemCount() {
        return exams.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name,time,place,remain_time;
        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.exam_rcy_name);
            time = (TextView) itemView.findViewById(R.id.exam_rcy_time);
            place = (TextView) itemView.findViewById(R.id.exam_rcy_place);
            remain_time = (TextView) itemView.findViewById(R.id.exam_rcy_remainTime);
        }
    }

}
