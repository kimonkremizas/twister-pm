package com.example.twisterpm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.twisterpm.model.Message;

public class SecondFragment extends Fragment {

    TextView messageUserTextView, messageContentTextView, messageCommentsNoTextView;
    ImageView messageIconImage;
    Message message;

    public SecondFragment(){

    }


//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//    }
//
//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_main, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_allMessages:  {
//                return true;
//            }
//            case R.id.action_myMessages: {
//                NavHostFragment.findNavController(SecondFragment.this)
//                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
//                return true;
//            }
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Bundle bundle = this.getArguments();
//        if(this.getArguments()!=null) {
//            Bundle parameters = this.getArguments();
//            message = (Message) parameters.getSerializable("SINGLEMESSAGE");
//            Log.d("KIMON2", "messageContent = "+ message.getContent());
//
//
//        }
//        else{
//            Log.d("KIMON", "No data from 1st fragment :(");
//        }




    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.single_message_layout, container, false);



    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //messageIconImage.setImageResource(message.get);

//        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                NavHostFragment.findNavController(SecondFragment.this)
//                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
//            }
//        });
    }
}