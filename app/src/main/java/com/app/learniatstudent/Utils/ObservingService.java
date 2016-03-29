package com.app.learniatstudent.Utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by macbookpro on 22/02/16.
 */
public class ObservingService extends Observable {

    public static ObservingService _sharedManager;
    static Context _mContext;
    public static HashMap<String, Observable> observables;
    public static LinkedList<String> notifications;
    public static String key;
    public static ArrayList<String> keys;

    public static String getKey() {
        return key;
    }


    public static void setKey(String key) {
        ObservingService.key = key;
    }


    public static synchronized ObservingService sharedManager(Context context)
    {
        if(_sharedManager == null)
        {
            _sharedManager = new ObservingService();
            observables = new HashMap<String, Observable>();
            notifications= new LinkedList<String>();
            keys = new ArrayList<String>();
            _mContext = context;
        }
        return _sharedManager;
    }


    public void addObserver(String notification, Observer observer) {
        Observable observable = observables.get(notification);
        if (observable==null) {
            observable = new Observable();
            observables.put(notification, observable);
            notifications.add(notification);
        }
        super.addObserver(observer);
    }

    public void removeObserver(String notification, Observer observer) {
        Observable observable = observables.get(notification);
        if (observable!=null) {
            //observables.remove(notification);
            //notifications.remove(notification);
            super.deleteObserver(observer);
        }
    }

    public void postNotification(String notification, Object object) {
        Observable observable = observables.get(notification);
        if (observable!=null) {
            setKey(notification);
//	        	if(notification.equals(kNotificationUserDownloadProfilePicture) || notification.equals(kNotificationUserGetProfileDetail))
//	        	keys.add(notification);
            setChanged();
            super.notifyObservers(object);
            //observable.notifyObservers(object);
        }
    }

//	    public void notifyObservers(Object data) {
//            setChanged();
//            super.notifyObservers(data);
//    }
}
