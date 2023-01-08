package android.redcall.service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class CallReceiver extends BroadcastReceiver {

    //private TelephonyManager telephonyManager;
    private Context mContext;
    private static int mState;
    public static boolean isActive;
    private StateListener stateListener;
    private String nameNum;
    private AudioManager audioManager;

    @Override
    public void onReceive(Context context, Intent intent) {

       /* telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (stateListener == null) {
            stateListener = new StateListener();
            telephonyManager.listen(stateListener, PhoneStateListener.LISTEN_CALL_STATE);
            mContext = context;
        } */

        //Так всё работает (если не заработает - то закоменть код с вызовом сервиса):
        String whatACall = intent.getAction();
        //telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (isActive) {
            if (whatACall != null) {
                //nameNum1 = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
                if (intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) {
                    nameNum = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    //nameNum = checkToContact(intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));
                    if (stateListener == null) {
                        stateListener = new StateListener();
                        //telephonyManager.listen(stateListener, PhoneStateListener.LISTEN_CALL_STATE);
                        // String state = intent.getStringExtra("state");
                        stateListener.callState(context, intent, nameNum);
                        mContext = context;
                    }
                }

            }
        }

       /* String whatACall = intent.getAction();
        if ("android.intent.action.PHONE_STATE".equals(whatACall)){

        }

        if ("android.intent.action.NEW_OUTGOING_CALL".equals(whatACall)){

        } */

    }

     /*public class StateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);

            String nameNum;

            if(phoneNumber != null && phoneNumber.length() > 0){
                //nameNum = phoneNumber;
                //nameNum = checkToContact(phoneNumber);
            }

            if (nameNum1 != null){
                nameNum = nameNum1;
            }
            else nameNum = "name";

            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    //Входящий звонок
                    mState = 1;
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //Идёт телефонный разговор
                    mState = 2;
                    Intent intent = new Intent(mContext, BackgroundService.class);
                    intent.putExtra("name", nameNum);
                    mContext.startService(intent);
                    Toast.makeText(mContext, "Идёт запись", Toast.LENGTH_SHORT).show();
                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    if (mState == 2){
                        //Вызов, который был принят и завершён
                        mState = 0;
                        mContext.stopService(new Intent(mContext, BackgroundService.class));
                        Toast.makeText(mContext, "Запись остановлена", Toast.LENGTH_SHORT).show();
                    }
                    if (mState == 1){
                        //Вызов, который был отклонён
                        mState = 0;
                    }
                    break;
            }
        }
    } */

    public class StateListener {
        public void callState(Context context, Intent intent, String nameNum) {
            String state = intent.getStringExtra("state");
            switch (state) {
                case "RINGING":
                    //Входящий звонок
                    mState = 1;
                    //Toast.makeText(context, "" + mState, Toast.LENGTH_SHORT).show();
                    break;

                case "OFFHOOK":
                    //Идёт телефонный разговор
                    Intent intentCall = new Intent(context, BackgroundService.class);
                    intentCall.putExtra("name", nameNum);
                    context.startService(intentCall);
                    Toast.makeText(context, "Идёт запись", Toast.LENGTH_SHORT).show();
                    mState = 2;
                    //Toast.makeText(context, "" + mState, Toast.LENGTH_SHORT).show();
                    break;

                case "IDLE":
                    if (mState == 2) {
                        //Вызов, который был принят и завершён
                        mState = 0;
                        context.stopService(new Intent(context, BackgroundService.class));
                        Toast.makeText(context, "Запись остановлена", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(context, "Зав " + mState, Toast.LENGTH_SHORT).show();
                    }
                    if (mState == 1) {
                        //Вызов, который был отклонён
                        mState = 0;
                        //Toast.makeText(context, "Отк " + mState, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }


    @SuppressLint("Range")
    private String checkToContact(String phoneNumber){
        String res = null;
        try {
            ContentResolver resolver = mContext.getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
            Cursor cursor = resolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

            if (cursor != null) { // Если курсор не ноль, то значит id найден
                if (cursor.moveToFirst()) {   // Теперь ищем имя контакта
                    res = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //Увеличиваем громкость
    public void volumePlus(){
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
    }

}
