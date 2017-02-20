package neighbor.com.mbis.maputil;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;

import org.apache.log4j.Logger;

import java.util.ArrayList;

public class LocationWrapper implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

  private static final String TAG = "LocationWrapper";

  private static final int MIN_ACCURACY = 50; // [meters]
  private static final int INTERVAL = 1000; // [seconds]
  private static final int FASTEST_INTERVAL = 1000; // [seconds]

  private final GoogleApiClient mGoogleApiClient;
  private final LocationRequest mRequest;
  private final ArrayList<OnLocationChangedListener> mOnLocationChangedListeners =
      new ArrayList<>();
  private final Context context;
  private boolean mAccuracyFilterEnabled = false;
  private int mMinAccuracy = MIN_ACCURACY;
  private boolean mRequestLocationUpdates = false;
  private final Object mLock = new Object();

  public LocationWrapper(Context context) {
//    Logger.getLogger(TAG).setLevel(Level.toLevel(Consts.LOG_OFF_INT));
    mGoogleApiClient = new GoogleApiClient.Builder(context)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(LocationServices.API)
        .build();

    mRequest = LocationRequest.create()
        .setInterval(INTERVAL)
        .setFastestInterval(FASTEST_INTERVAL)
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    this.context = context;
  }

  public void setAccuracyFilterEnabled(boolean enabled, int minAccuracy) {
    mAccuracyFilterEnabled = enabled;
    mMinAccuracy = minAccuracy;
  }


  public void requestUpdates() {
    synchronized (mLock) {
      if (!mRequestLocationUpdates) {
        mRequestLocationUpdates = true;
        if (mGoogleApiClient.isConnected()) {
          Logger.getLogger(TAG).debug("requestUpdates: requestLocationUpdates");
          if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
              ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
          }
          LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mRequest, this);
        } else {
          mGoogleApiClient.connect();
        }
      }
    }
  }

  public void cancelUpdates() {
    synchronized (mLock) {
      if (mRequestLocationUpdates) {
        mRequestLocationUpdates = false;
        if (mGoogleApiClient.isConnected()) {
          Logger.getLogger(TAG).debug("cancelUpdates(): removeLocationUpdates");
          LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
          mGoogleApiClient.disconnect();
        }
      }
    }
  }

  public Location getLastLocation() {
    Logger.getLogger(TAG).debug("getLastLocation");
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      Logger.getLogger(TAG).debug("PERMISSION_GRANTED");
      return null;
    }
    Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    if (mLastLocation != null) {
      Logger.getLogger(TAG).debug("(mLastLocation != null");
      return mLastLocation;
    }
    return null;
  }

  public void registerOnLocationChangedListener(OnLocationChangedListener listener) {
    synchronized (mOnLocationChangedListeners) {
      mOnLocationChangedListeners.add(listener);
    }
  }

  public void unregisterOnLocationChangedListener(OnLocationChangedListener listener) {
    synchronized (mOnLocationChangedListeners) {
      mOnLocationChangedListeners.remove(listener);
    }
  }

  /**
   * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
   */
  @Override
  public void onConnected(Bundle connectionHint) {
    synchronized (mLock) {
      if (mRequestLocationUpdates) {
        Logger.getLogger(TAG).debug("onConnected(): requestLocationUpdates");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mRequest, this);
      }
    }
  }

  @Override
  public void onConnectionSuspended(int cause) {
    // Called when the client is temporarily in a disconnected state.
    Logger.getLogger(TAG).debug("onConnectionFailed(): cause=" + cause);
  }

  /**
   * Implementation of {@link OnConnectionFailedListener}.
   */
  @Override
  public void onConnectionFailed(ConnectionResult result) {
    Logger.getLogger(TAG).debug("onConnectionFailed(): result=" + result);
  }

  /**
   * Implementation of {@link LocationListener}.
   */
  @Override
  public void onLocationChanged(Location location) {
    if (mAccuracyFilterEnabled) {
      if (!location.hasAccuracy()) {
        Logger.getLogger(TAG).warn("No accuracy -> ignore");
        return;
      } else if (location.getAccuracy() > mMinAccuracy) {
        Logger.getLogger(TAG).warn("Too low accuracy: " + location.getAccuracy() + " -> ignore");
        return;
      }
    }

    if (location.getTime() == 0) {
      location.setTime(System.currentTimeMillis());
    }

    synchronized (mOnLocationChangedListeners) {
      for (OnLocationChangedListener listener : mOnLocationChangedListeners) {
        listener.onLocationChanged(location);
      }
    }
  }
}
