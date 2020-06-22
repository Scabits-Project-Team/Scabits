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

/**
 * Class that contains the list of DataHabits and make the correspondance with the view and the list.
 * @see DataHabits
 */
public class DataHabitsAdapter extends RecyclerView.Adapter<DataHabitsAdapter.DataHabitsViewHolder> {


        //_______________________________________fields_____________________________________________
        /**
         * The context on this class.
         */
        private Context context;
        /**
         * The list of DataHabits.
         */
        private List<DataHabits> listHabits;


        //_______________________________________constructors_______________________________________
        /**
         * Instanciate a DataHabitsAdapter object with all the fields equal to params.
         */
        DataHabitsAdapter(Context context, List<DataHabits> listHabits) {
            this.context = context;
            this.listHabits = listHabits;
        }


        //_______________________________________methods____________________________________________
        /**
         * Setup the LayoutInflater and the view.
         * @param parent the parent that contains this object.
         * @param viewType the type of view.
         * @return the ViewHolder created.
         */
        @NonNull
        @Override
        public DataHabitsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //Create the LayoutInflater and the view of each item
            LayoutInflater inflater = LayoutInflater.from(context);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.list_layout,
                    null);
            return new DataHabitsViewHolder(view);
        }

        /**
         * Get the item on the list and set params of the ViewHolder with the one selected.
         * @param holder the ViewHolder that we have to setup.
         * @param position the postion of the item in the list.
         */
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

        /**
         * Get the number of item in the list instanciated before.
         * @return the number of item in the list.
         */
        @Override
        public int getItemCount() {
            return listHabits.size();
        }

        //_________________________________________class____________________________________________
        /**
         * Class that contains informations on DataHabits, it makes the correspondance with the
         * view and the list.
         */
        static class DataHabitsViewHolder extends RecyclerView.ViewHolder {


            //______________________________________fields__________________________________________
            /**
             * The activity that the user chose with the survey.
             */
            private TextView realActivity;
            /**
             * The activity we expected it the real one.
             */
            private TextView expectedActivity;
            /**
             * The image icon of the real activity.
             */
            private ImageView tagIcon;
            /**
             * The infos on with sensors have been used.
             */
            private TextView sensorsUsed;
            /**
             * The time of the collect.
             */
            private TextView time;
            /**
             * The image that show if we the expected activity is the same as the real one.
             */
            private ImageView validation;


            //______________________________________constructors____________________________________
            /**
             * Instanciate a DataHabitsViewHolder object with all the fields equal to params.
             */
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
