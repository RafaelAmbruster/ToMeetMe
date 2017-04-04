
package com.app.tomeetme.helper.background;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

import com.app.tomeetme.R;
import com.app.tomeetme.helper.log.LogManager;


public class BackgroundProcess {
    private boolean cancelable;
    private Context context;
    private int currentProgress;
    private ProgressDialog dialog;
    private boolean inProgress;
    private int maxProgress;
    private String message;
    private int style;
    private BackgroundProcessEvent event;

    public BackgroundProcess(Context paramContext) {
        this.context = paramContext;
        this.style = 0;
        setCancelable(false);
    }

    public BackgroundProcess(Context paramContext, String paramString) {
        this.context = paramContext;
        this.style = 0;
        setCancelable(false);
        setMessage(paramString);
    }

    public BackgroundProcess(Context paramContext, boolean paramBoolean, int paramInt1, String paramString, int paramInt2) {
        this.context = paramContext;
        this.cancelable = paramBoolean;
        this.maxProgress = paramInt1;
        this.message = paramString;
        this.style = paramInt2;
    }

    public int getCurrentProgress() {
        return this.currentProgress;
    }

    public void setCurrentProgress(int paramInt) {
        this.currentProgress = paramInt;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public void setMaxProgress(int paramInt) {
        this.maxProgress = paramInt;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String paramString) {
        this.message = paramString;
    }

    public int getStyle() {
        return this.style;
    }

    public void setStyle(int paramInt) {
        this.style = paramInt;
    }

    public boolean isCancelable() {
        return this.cancelable;
    }

    public void setCancelable(boolean paramBoolean) {
        this.cancelable = paramBoolean;
    }

    public boolean isInProgress() {
        return this.inProgress;
    }

    public void start(BackgroundProcessEvent paramBackgroundProcessEvent) {
        this.dialog = new ProgressDialog(this.context);
        this.dialog.setProgressStyle(this.style);
        this.dialog.setProgress(0);
        this.dialog.setMax(this.maxProgress);
        this.dialog.setCancelable(false);
        this.dialog.setMessage(this.message);
        this.inProgress = true;
        this.event = paramBackgroundProcessEvent;

        if (this.cancelable)
            this.dialog.setButton(-2, this.context.getResources().getString(R.string.dialog_cancel), new OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    BackgroundProcess.this.inProgress = false;
                    paramDialogInterface.dismiss();
                }
            });

        this.dialog.show();
        Button localButton = this.dialog.getButton(-2);

        ProgressBar localProgressBar = (ProgressBar) this.dialog.findViewById(android.R.id.progress);

        new Thread(new Runnable() {
            public void run() {
                try {

                    event.process();
                    ((Activity) BackgroundProcess.this.context).runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                BackgroundProcess.this.inProgress = false;
                                BackgroundProcess.this.dialog.dismiss();
                                event.postProcess();
                                return;
                            } catch (Exception localException) {
                                localException.printStackTrace();
                                LogManager.getInstance().error("BackgroundProcess", localException.getMessage());
                            }
                        }
                    });
                    return;
                } catch (Exception localException) {
                    while (true) {
                        localException.printStackTrace();
                        LogManager.getInstance().error("BackgroundProcess", localException.getMessage());
                    }
                }
            }
        }).start();
    }

    public void startWithoutDialog(BackgroundProcessEvent paramBackgroundProcessEvent) {
        this.event = paramBackgroundProcessEvent;
        new Thread(new Runnable() {
            public void run() {
                try {
                    event.process();
                    ((Activity) BackgroundProcess.this.context).runOnUiThread(new Runnable() {
                        public void run() {
                            event.postProcess();
                        }
                    });
                    return;
                } catch (Exception localException) {
                    while (true) {
                        localException.printStackTrace();
                        LogManager.getInstance().error("BackgroundProcess", localException.getMessage());
                    }
                }
            }
        }).start();
    }

    public void stepProgress() {
        stepProgress(1);
    }

    public void stepProgress(int paramInt) {
        this.currentProgress = (paramInt + this.currentProgress);
        if (this.dialog != null)
            this.dialog.setProgress(this.currentProgress);
    }

    public void setDialogMessage(String Message)
    {
        if (this.dialog != null)
            this.dialog.setMessage(Message);
    }
}

