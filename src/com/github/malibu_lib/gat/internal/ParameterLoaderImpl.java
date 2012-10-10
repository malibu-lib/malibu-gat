// Copyright 2011 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.github.malibu_lib.gat.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.github.malibu_lib.gat.EasyTracker;
import com.github.malibu_lib.gat.TrackingParams;

/**
 * This class implements the ParameterLoader interface by loading parameters
 * from the Application's resources.
 */
public class ParameterLoaderImpl implements ParameterLoader {

    private final Context ctx;
    private String versionName;
    private String packageName;
    private boolean packageInfoInitialized = false;
    private String version;
    private int versionCode;

    public ParameterLoaderImpl(Context ctx) {
        if (ctx == null) {
            throw new NullPointerException("Context cannot be null");
        }
        this.ctx = ctx;
    }

    /**
     * Look up the resource id for the given type in the package identified by
     * gaContext. The lookup is done by key instead of id as presence of these
     * parameters are optional. Some or all may not be present. If gaContext is
     * null, return 0.
     * 
     * @param key the key for the string resource we're seeking
     * @param type the type (string, bool or integer)
     * @return resource id of the given string resource, or 0 if not found
     */
    private int getResourceIdForType(String key, String type) {
        if (ctx == null) {
            return 0;
        }
        return ctx.getResources()
                .getIdentifier(key, type, ctx.getPackageName());
    }

    @Override
    public String getString(String key) {
        int id = getResourceIdForType(key, "string");
        if (id == 0) {
            return null;
        } else {
            return ctx.getString(id);
        }
    }

    @Override
    public boolean getBoolean(String key) {
        int id = getResourceIdForType(key, "bool");
        if (id == 0) {
            return false;
        } else {
            return "true".equalsIgnoreCase(ctx.getString(id));
        }
    }

    @Override
    public int getInt(String key, int defaultValue) {
        int id = getResourceIdForType(key, "integer");
        if (id == 0) {
            return defaultValue;
        } else {
            try {
                return Integer.parseInt(ctx.getString(id));
            } catch (NumberFormatException e) {
                Log.w(EasyTracker.LOG_TAG, "NumberFormatException parsing "
                        + ctx.getString(id));
                return defaultValue;
            }
        }
    }

    @Override
    public String getVersion() {
        initPackageInfo();
        return version;
    }

    private void initPackageInfo() {
        if (packageInfoInitialized) {
            return;
        }
        packageInfoInitialized = true;
        // Get information about the Package
        PackageManager pm = ctx.getPackageManager();
        try {
            PackageInfo pi;
            // Version
            pi = pm.getPackageInfo(ctx.getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;
            // Package name
            packageName = pi.packageName;

        } catch (NameNotFoundException e) {
            Log.e(EasyTracker.LOG_TAG, "Error collecting trace information", e);
        }

        // handle customizations
        if (ctx.getApplicationContext() instanceof TrackingParams) {
            version = ((TrackingParams) ctx.getApplicationContext()).getVersion(versionName,
                    versionCode);
        } else {
            version = versionName + "(" + versionCode + ")";
        }
    }

    public String getPackageName() {
        return packageName;
    }

}
