package madelyntav.c4q.nyc.chipchop;

import android.app.Application;

import com.firebase.client.Firebase;

public class FirebaseApplication extends Application {

    private static FirebaseApplication singletonInstance;

    public static FirebaseApplication getInstance() {
        return singletonInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singletonInstance = this;
        Firebase.setAndroidContext(this);
    }
}
