package fr.jaimys.scabits;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DataHabitsAdapter extends RecyclerView.Adapter<DataHabitsAdapter.DataHabitsViewHolder> {


        //_______________________________________fields_____________________________________________
        private Context context;
        private List<DataHabits> listHabits;


        //_______________________________________constructors_______________________________________
        DataHabitsAdapter(Context context, List<DataHabits> listHabits) {
            this.context = context;
            this.listHabits = listHabits;
        }


        //_______________________________________methods____________________________________________
        @NonNull
        @Override
        public DataHabitsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //Create the LayoutInflater and the view of each item
            LayoutInflater inflater = LayoutInflater.from(context);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.list_layout, null);
            return new DataHabitsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DataHabitsViewHolder holder, int position) {
            //Creation of the DataHabits and recuperation of each component
            DataHabits habits = this.listHabits.get(position);
            holder.realActivity.setText(habits.getRealActivity());
            holder.expectedActivity.setText(habits.getExpectedActivity());
            holder.sensorsUsed.setText(habits.getSensorsUsed());
            holder.time.setText(habits.getTime());

            //In the ressources, each activity has an icon (recuperation with the tag)
            String resourceName = "item_" + habits.getTagIcon() + "_icon";
            int resID = context.getResources().getIdentifier(resourceName, "drawable",
                        context.getPackageName());
            holder.tagIcon.setImageDrawable(context.getResources().getDrawable(resID));

            //Show a validate or unvalidate symbol, if we guessed right or not
            if (habits.getRealActivity().equals(habits.getExpectedActivity())) {
                holder.validation.setImageDrawable(context.getResources().
                        getDrawable(R.drawable.validate));
            }
            else {
                holder.validation.setImageDrawable(context.getResources().
                        getDrawable(R.drawable.unvalidate));
            }
        }

        @Override
        public int getItemCount() {
            return listHabits.size();
        }



        //_________________________________________class____________________________________________
        static class DataHabitsViewHolder extends RecyclerView.ViewHolder {


            //______________________________________fields__________________________________________
            TextView realActivity;
            TextView expectedActivity;
            ImageView tagIcon;
            TextView sensorsUsed;
            ImageView validation;
            TextView time;


            //______________________________________constructors____________________________________
            DataHabitsViewHolder(@NonNull View itemView) {
                super(itemView);

                //Recuperation of each component and association with fields
                this.realActivity = itemView.findViewById(R.id.real_activity);
                this.expectedActivity = itemView.findViewById(R.id.expected_activity);
                this.tagIcon = itemView.findViewById(R.id.icon_activity);
                this.sensorsUsed = itemView.findViewById(R.id.sensors);
                this.validation = itemView.findViewById(R.id.icon_found);
                this.time = itemView.findViewById(R.id.time_item);
            }
        }

    }
