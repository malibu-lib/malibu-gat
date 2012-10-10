/*******************************************************************************
 * Copyright (c) 2012 MASConsult Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.github.malibu_lib.gat;

import android.app.Activity;
import android.os.Bundle;

import com.github.malibu_lib.BaseAdvice;
import com.github.malibu_lib.Pointcut;
import com.github.malibu_lib.pointcuts.activity.OnCreateActivityAdvice;
import com.github.malibu_lib.pointcuts.activity.OnSaveInstanceStateActivityAdvice;
import com.github.malibu_lib.pointcuts.activity.OnStartActivityAdvice;
import com.github.malibu_lib.pointcuts.activity.OnStopActivityAdvice;

public class GATAdvice extends BaseAdvice implements OnCreateActivityAdvice, OnStopActivityAdvice,
        OnStartActivityAdvice, OnSaveInstanceStateActivityAdvice {

    public GATAdvice(Pointcut pointcut) {
        super(pointcut);
    }

    @Override
    public void onSaveInstanceState(Activity activity, Bundle outState) {
        EasyTracker.getTracker().trackActivityRetainNonConfigurationInstance();
    }

    @Override
    public void onStart(Activity activity) {
        // This call will ensure that the Activity in question is tracked
        // properly, based on the setting of ga_auto_activity_tracking
        // parameter. It will also ensure that startNewSession is called
        // appropriately.
        EasyTracker.getTracker().trackActivityStart(activity.getClass().getCanonicalName());
    }

    @Override
    public void onStop(Activity activity) {
        // This call is needed to ensure time spent in an Activity and an
        // Application are measured accurately.
        EasyTracker.getTracker().trackActivityStop();
    }

    @Override
    public void onCreate(Activity activity, Bundle savedInstanceState) {
        // Only one call to setContext is needed, but additional calls don't
        // hurt anything, so we'll always make the call to ensure EasyTracker
        // gets setup properly.
        EasyTracker.getTracker().setContext(activity);
    }

}
