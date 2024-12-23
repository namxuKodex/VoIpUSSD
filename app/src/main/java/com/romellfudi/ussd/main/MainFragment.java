/*
 * Copyright (c) 2020. BoostTag E.I.R.L. Romell D.Z.
 * All rights reserved
 * porfile.romellfudi.com
 */

package com.romellfudi.ussd.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.romellfudi.permission.PermissionService;
import com.romellfudi.ussd.App;
import com.romellfudi.ussd.R;
import com.romellfudi.ussd.databinding.ContentOp1Binding;
import com.romellfudi.ussdlibrary.OverlayShowingService;
import com.romellfudi.ussdlibrary.SplashLoadingService;
import com.romellfudi.ussdlibrary.USSDApi;
import com.romellfudi.ussdlibrary.USSDController;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Use Case for Test Windows
 *
 * @author Romell Domínguez
 * @version 1.1.b 27/09/2018
 * @since 1.0.a
 */
public class MainFragment extends Fragment {

    @Inject
    USSDApi ussdApi;

    @Inject
    HashMap<String, HashSet<String>> map;

    DaoViewModel mViewModel;
    ContentOp1Binding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((App) requireActivity().getApplicationContext()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        PermissionService.INSTANCE.request(requireActivity(),callback);
//        new PermissionService(requireActivity()).request(callback);
        mViewModel = ViewModelProviders.of(requireActivity()).get(DaoViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.content_op1, container, false);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(requireActivity());
        setHasOptionsMenu(false);

        binding.btn1.setOnClickListener(view -> {
            binding.result.setText("");
            ussdApi.callUSSDInvoke(getPhoneNumber(), map, new USSDController.CallbackInvoke() {
                @Override
                public void responseInvoke(String message) {
                    Timber.d(message);
                    binding.result.append("\n-\n" + message);
                    // first option list - select option 1
                    ussdApi.send("1", message12 -> {
                        Timber.i(message12);
                        binding.result.append("\n-\n" + message12);
                        // second option list - select option 2
                        ussdApi.send("2", message121 -> {
                            Timber.i(message121);
                            binding.result.append("\n-\n" + message121);
                            // second option list - select option 1
                            ussdApi.send("1", message1211 -> {
                                Timber.i(message1211);
                                binding.result.append("\n-\n" + message1211);
                                Timber.i("successful");
                            });
                        });
                    });
//                        ussdApi.cancel();
                }

                @Override
                public void over(String message) {
                    Timber.i(message);
                    binding.result.append("\n-\n" + message);
//                        mViewModel.setResult(dao);
//                        mViewModel.update();
                }
            });
        });

        binding.btn2.setOnClickListener(view -> {
            if (USSDController.verifyOverLay(requireActivity())) {
                final Intent svc = new Intent(requireActivity(), OverlayShowingService.class);
                svc.putExtra(OverlayShowingService.EXTRA, getString(R.string.process));
                pendingServiceIntent(svc);
                callOverlay(svc);
            }
        });

        binding.btn4.setOnClickListener(view -> {
            if (USSDController.verifyOverLay(requireActivity())) {
                final Intent svc = new Intent(requireActivity(), SplashLoadingService.class);
                pendingServiceIntent(svc);
                callOverlay(svc);
            }
        });

        binding.btn3.setOnClickListener(view ->
                USSDController.verifyAccesibilityAccess(requireActivity()));

        return binding.getRoot();
    }

    private void callOverlay(Intent overlayDialogService) {
        ussdApi.callUSSDOverlayInvoke(getPhoneNumber(), map, new USSDController.CallbackInvoke() {
            @Override
            public void responseInvoke(String message) {
                Timber.i(message);
                binding.result.append("\n-\n" + message);
                // first option list - select option 1
                ussdApi.send("1", message1 -> {
                    Timber.i(message1);
                    binding.result.append("\n-\n" + message1);
                    // second option list - select option 2
                    ussdApi.send("2", message2 -> {
                        Timber.i(message2);
                        binding.result.append("\n-\n" + message2);
                        // second option list - select option 1
                        ussdApi.send("1", message3 -> {
                            Timber.i(message3);
                            binding.result.append("\n-\n" + message3);
                            requireActivity().stopService(overlayDialogService);
                            Timber.i("successful");
                        });
                    });
                });
//                            ussdApi.cancel();
            }

            @Override
            public void over(String message) {
                Timber.i(message);
                binding.result.append("\n-\n" + message);
                requireActivity().stopService(overlayDialogService);
                Timber.i("STOP OVERLAY DIALOG");
            }
        });
    }

    private void pendingServiceIntent(Intent overlayService) {
        requireActivity().startService(overlayService);
        Timber.i(getString(R.string.overlayDialog));
        new Handler().postDelayed(() -> requireActivity().stopService(overlayService), 12000);
        binding.result.setText("");
    }

    private String getPhoneNumber() {
        return binding.phone.getText().toString().trim();
    }

    private final PermissionService.Callback callback = new PermissionService.Callback() {
//        @Override
//        public void onResponse(@Nullable List<String> list) {
//
//        }

        @Override
        public void onResponse(@Nullable List<String> list) {
            if (list != null) {
                Toast.makeText(getContext(),
                        getString(R.string.refuse_permissions), Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Timber.i(MessageFormat.format(getString(R.string.primissionsLogFormat), permissions, grantResults));
//        callback.handler(permissions, grantResults);
//        callback.onResponse(grantResults.);
    }
}

