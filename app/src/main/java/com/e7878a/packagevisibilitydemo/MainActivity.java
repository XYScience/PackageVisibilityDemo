package com.e7878a.packagevisibilitydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.hash.Hashing;

import java.util.Locale;

/**
 * 在 OPPO 手机上测试
 * <p>
 * 如果应用以 Android 11（API 级别 30）或更高版本为目标平台，并查询设备上已安装的其他应用（非系统应用）相关的信息，则系统在默认情况下会过滤此信息。
 * <p>
 * 调用 PackageInfo(get signature or etc..) 或 resolveActivity 或 getLaunchIntentForPackage，将返回空的信息，
 * 除非声明 QUERY_ALL_PACKAGES 权限或使用 manifest/queries 元素
 */
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    private String[] packages = {
            "com.android.phone", "com.oplus.appplatform", "com.android.vendors.bridge.softsim",
            "com.finshell.wallet", "com.coloros.findmyphone", "com.redteamobile.roaming",
            "com.heytap.browser", "com.heytap.market"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StringBuilder sb = new StringBuilder();
        PackageInfo packageInfoDemo = getPackageInfo(this, getPackageName());
        sb.append(String.format(Locale.ENGLISH, "Demo versionCode: %d, versionName: %S", packageInfoDemo.versionCode, packageInfoDemo.versionName));
        sb.append("\n\n");
        for (String p : packages) {
            PackageInfo packageInfo = getPackageInfo(this, p);
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            if (applicationInfo != null) {
                sb.append(String.format("%s(enable: %b)", p, applicationInfo.enabled));
                sb.append("\n");
            }
            sb.append(getPackageLabel(this, p));
            sb.append("\n");
            sb.append(String.format(Locale.ENGLISH, "versionCode: %d, versionName: %s", packageInfo.versionCode, packageInfo.versionName));
            String signature = getSignature(this, p);
            if (!TextUtils.isEmpty(signature)) {
                sb.append("\n");
                sb.append(signature);
            }
            sb.append("\n\n");
        }
        TextView textView = findViewById(R.id.text_signatures);
        textView.setText(sb.toString());
    }

    /**
     * you can get system app`s signature without android.permission.QUERY_ALL_PACKAGES or manifest/queries tag
     */
    public String getSignature(Context context, String packageName) {
        String certificate = "";
        try {
            PackageInfo packageInfo = getPackageInfo(context, packageName, PackageManager.GET_SIGNATURES);
            Signature[] signatures = packageInfo.signatures;
            if (signatures.length > 0) {
                certificate = Hashing.sha1().hashBytes(signatures[0].toByteArray()).toString();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "getSignature", e);
        }
        return certificate;
    }

    public PackageInfo getPackageInfo(Context context, String packageName) {
        return getPackageInfo(context, packageName, 0);
    }

    /**
     * you can get system app`s signature without android.permission.QUERY_ALL_PACKAGES or manifest/queries tag
     */
    public PackageInfo getPackageInfo(Context context, String packageName, int flag) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(packageName, flag);
        } catch (Exception e) {
            Log.e(LOG_TAG, "getPackageInfo: " + e.toString());
        }
        if (info == null) {
            info = new PackageInfo();
        }
        return info;
    }

    /**
     * you can get system app`s signature without android.permission.QUERY_ALL_PACKAGES or manifest/queries tag
     */
    public static String getPackageLabel(Context context, String packageName) {
        String label = packageName;
        try {
            label = context.getPackageManager().getApplicationLabel(
                    context.getPackageManager().getApplicationInfo(packageName,
                            PackageManager.GET_META_DATA)).toString();
        } catch (Exception e) {
            Log.i(LOG_TAG, "getPackageLabel: " + e.getMessage());
        }
        return label;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_oroming:
                Intent intentGetLaunchIntent = getPackageManager().getLaunchIntentForPackage("com.redteamobile.roaming");
                if (intentGetLaunchIntent != null && intentGetLaunchIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intentGetLaunchIntent);
                } else {
                    showToast("没有权限或找不到国际上网");
                }
                break;
            case R.id.btn_oroming_package:  // you can startActivity without anything
                Intent intentWallet = new Intent();
                intentWallet.setPackage("com.redteamobile.roaming");
                try {
                    startActivity(intentWallet);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "startActivity: " + e);
                }
                break;
            case R.id.btn_find_my_phone:  // you can startActivity without anything
                Intent intentFindMyPhone = new Intent();
                intentFindMyPhone.setAction("com.oplus.findmyphone.LAUNCHER");
                try {
                    startActivity(intentFindMyPhone);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "startActivity: " + e);
                }
                break;
            case R.id.btn_oppo_wallet2:  // you can startActivity without anything
                Intent intentWalletDeeplink = new Intent(Intent.ACTION_VIEW);
                intentWalletDeeplink.setData(Uri.parse("wallet://fintech/main/opencos"));
                try {
                    startActivity(intentWalletDeeplink);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "startActivity: " + e);
                }
                break;
            case R.id.btn_oppo_wallet3:  // you can startActivity without anything
                Intent intentWalletDeeplink2 = new Intent(Intent.ACTION_VIEW);
                intentWalletDeeplink2.setData(Uri.parse("wallet://fintech/bank/opencheck?stage=2"));
                try {
                    startActivity(intentWalletDeeplink2);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "startActivity: " + e);
                }
                break;
            case R.id.btn_alipay:  // you can startActivity without anything
                Intent intentAlipay = new Intent(Intent.ACTION_VIEW);
                intentAlipay.setData(Uri.parse("alipays://platformapi/startApp?appId=10000007&actionType=route&qrcode=https%3A%2F%2Fglobal.alipay.com%2F2810020400932Am17PhzX6JEPs1kHca2rZwm"));
                try {
                    startActivity(intentAlipay);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "startActivity: " + e);
                }
                break;
            case R.id.btn_tng:  // you can startActivity without anything
                Intent intentTng = new Intent(Intent.ACTION_VIEW);
                intentTng.setData(Uri.parse("https://m.tngdigital.com.my/s/ac-cashier-intermediate/h5.html?ACCodeValue=https%3A%2F%2Fglobal.alipay.com%2F281002040092t27PzSfAZ30V1z31tfv31tLM"));
                try {
                    startActivity(intentTng);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "startActivity: " + e);
                }
                break;
            case R.id.btn_browser:  // you can startActivity without anything
                Intent intentBrowser = new Intent(Intent.ACTION_VIEW);
                intentBrowser.setData(Uri.parse("https://static.redteamobile.com/commons/v2/privacy/oplus/zh_CN/index.html"));
                try {
                    startActivity(intentBrowser);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "startActivity: " + e);
                }
                break;
            case R.id.btn_market:  // you can startActivity without anything
                Intent intentMarket = new Intent(Intent.ACTION_VIEW);
                intentMarket.setData(Uri.parse("market://details?id=com.finshell.wallet"));
                try {
                    startActivity(intentMarket);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "startActivity: " + e);
                }
                break;
            case R.id.btn_dial:  // you can startActivity without anything
                Intent intentDial = new Intent(Intent.ACTION_DIAL);
                intentDial.setData(Uri.parse("tel:" + 10086));
                try {
                    startActivity(intentDial);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "startActivity: " + e);
                }
                break;
            case R.id.btn_mail:  // you can startActivity without anything
                Intent intentMail = new Intent(Intent.ACTION_SENDTO);
                intentMail.setData(Uri.parse("mailto:" + "support@redteamobile.net"));
                try {
                    startActivity(intentMail);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "startActivity: " + e);
                }
                break;
            default:
                break;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}