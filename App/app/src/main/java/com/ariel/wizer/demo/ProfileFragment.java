//
//package com.ariel.wizer.demo;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//import com.ariel.wizer.R;
//import com.ariel.wizer.fragments.ChangePasswordDialog;
//
//public class ProfileFragment extends Fragment  {
//
//    private Button mBtChangePassword;
//
//    public static ProfileFragment newInstance() {
//        ProfileFragment fragment = new ProfileFragment();
//        return fragment;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_pofile, container, false);
//        initViews(view);
//        return view;
//    }
//    private void initViews(View v) {
//
//        mBtChangePassword = (Button) v.findViewById(R.id.btn_change_password);
//        mBtChangePassword.setOnClickListener(view -> showDialog());
//    }
//
//    private void showDialog(){
//
//        ChangePasswordDialog fragment = new ChangePasswordDialog();
//        fragment.show(getActivity().getFragmentManager(), ChangePasswordDialog.TAG);
//    }
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//}
//
//
