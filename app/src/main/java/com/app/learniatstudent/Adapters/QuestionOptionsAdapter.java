package com.app.learniatstudent.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.app.learniatstudent.R;
import com.app.studentlearnientapi.DataModels.FetchQuestionDataModels.QuestionDataModel;

import java.util.ArrayList;
import java.util.List;


public class QuestionOptionsAdapter extends RecyclerView.Adapter<QuestionOptionsAdapter.QOptionsViewHolder> {

    private static final String TAG = QuestionOptionsAdapter.class.getSimpleName();
    List<QuestionDataModel> categoriesModelList;
    Context context;
    LayoutInflater inflater;
    OptionsSelected optionsSelected;
    int selectedPosition;
    private SparseBooleanArray selectedItems;

    public QuestionOptionsAdapter(Context context, List<QuestionDataModel> optionsModels, List<Integer> items) {
        this.categoriesModelList = optionsModels;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setSelectedItems(items);
    }

    public void setOptionsSelected(OptionsSelected optionsSelected) {
        this.optionsSelected = optionsSelected;
    }


    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> setSelectedItems(List<Integer> items) {
        selectedItems = new SparseBooleanArray();
        for (int i = 0; i < items.size(); i++) {
            selectedItems.put(items.get(i), true);
        }
        return items;
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        notifyDataSetChanged();
        return items;
    }

    @Override
    public QOptionsViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new QOptionsViewHolder(inflater.inflate(R.layout.question_options_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(QOptionsViewHolder holder, int position) {
        QuestionDataModel info = categoriesModelList.get(position);
        QOptionsViewHolder typeViewHolder = (QOptionsViewHolder) holder;
//        typeViewHolder.tvQOption.setText(info.getCategoryName());
        if (selectedItems.get(position, false)){

        }

        else{

        }
    }


    @Override
    public int getItemCount() {
        return categoriesModelList.size();
    }


    class QOptionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        LinearLayout optionLayout;
        TextView tvQOption;

        public QOptionsViewHolder(View itemView) {
            super(itemView);
            optionLayout = (LinearLayout) itemView.findViewById(R.id.llQuestions_listitem_layout);
            tvQOption = (TextView) itemView.findViewById(R.id.tv_question_option);
            optionLayout.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int index = getAdapterPosition();
            toggleSelection(index);
            if (optionsSelected != null){
                optionsSelected.onOptionsSelected(categoriesModelList.get(index), index);
            }

        }
    }

    public interface OptionsSelected {
        void onOptionsSelected(QuestionDataModel model, int pos);
    }

}
