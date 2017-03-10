package com.cyho.lng.currencyconversionapp.runner;

import android.app.Activity;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.FileFsFile;

/**
 * Created by LNG on 3/10/2017.
 */

public class CustomRobolectricTestRunner extends RobolectricTestRunner {

    public CustomRobolectricTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        final String BUILD_PATH = "src/main";
        final FileFsFile manifestFile = FileFsFile.from(BUILD_PATH, "AndroidManifest.xml");
        final FileFsFile resFile = FileFsFile.from(BUILD_PATH, "res");
        final FileFsFile assetFile = FileFsFile.from(BUILD_PATH, "assets");

        return new AndroidManifest(manifestFile, resFile, assetFile) {
            @Override
            public String getThemeRef(Class<? extends Activity> activityClass) {
                return "@style/RoboAppTheme";
            }
        };
    }
}