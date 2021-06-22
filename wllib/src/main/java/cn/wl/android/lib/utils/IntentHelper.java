package cn.wl.android.lib.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.FileUtils;
import com.watermark.androidwm.bean.WatermarkText;
import com.yalantis.ucrop.UCrop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.util.EnumSet;
import java.util.List;

import cn.wl.android.lib.R;
import cn.wl.android.lib.config.WLConfig;
import cn.wl.android.lib.ui.internal.IRxOption;
import cn.wl.android.lib.utils.result.ActivityResult;
import cn.wl.android.lib.utils.result.DataResult;
import cn.wl.android.lib.utils.result.ResultFragment;
import cn.wl.android.lib.utils.result.RxResult;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

import static com.zhihu.matisse.MimeType.JPEG;
import static com.zhihu.matisse.MimeType.PNG;

/**
 * Created by JustBlue on 2019-08-31.
 *
 * @email: bo.li@cdxzhi.com
 * @desc: 界面跳转辅助
 */
public class IntentHelper {

    // 跳转拍摄界面需要的权限
    public static final String[] PHOTO_PERMISSION = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    // 跳转相册需要的权限
    public static final String[] ALBUM_PERMISSION = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    // 跳转相册需要的权限
    public static final String[] SD_PERMISSION = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static final int RC_ALBUM = 301;
    public static final int RC_PHOTO = 302;
    public static final int RC_VIDEO = 303;
    public static final int RC_FILE = 304;

    /**
     * @param option
     * @return
     */
    public static Observable<Boolean> requestCameraPermission(IRxOption option) {
        return option.getRxPermission().request(ALBUM_PERMISSION);
    }

    /**
     * 跳转自定义拍照界面
     *
     * @param option
     * @return
     */
    public static Observable<DataResult<RecordResult>> startPhoto(IRxOption option) {
        return option.getRxPermission().request(ALBUM_PERMISSION)
                .flatMap(success -> {
                    if (success) {
                        return lxPhoto(option);
                    } else {
                        Toasts.show("获取权限失败, 跳转拍摄界面失败!");
                        return Observable.empty();
                    }
                })
                .onErrorReturn(t -> DataResult.cancel()); // 发生异常时返回取消事件
    }

    /**
     * 跳转自定义图片\视频拍摄界面
     *
     * @param option
     * @return
     */
    private static Observable<DataResult<RecordResult>> lxPhoto(IRxOption option) {
        RxResult rxResult = option.getRxResult();

        Activity activity = rxResult.getFragment().getActivity();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String savePath = DirHelper.getImageSavePath("photo_" + WLID.getId() + ".jpg");

        File file = new File(savePath);
        final Uri imgUri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            imgUri = FileProvider.getUriForFile(activity, WLConfig.getAuthCode(), file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        } else {
            imgUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        }

        return rxResult.start(intent, RC_PHOTO)
                .take(1)
                .flatMap(result -> {
                    if (result.isSuccess()) {
//                        Intent data = result.getData();
//
//                        if (option instanceof IAlertAction) {
//                            ((IAlertAction) option).showLoadingAlert("正在处理图片...");
//                        }
//
//                        return saveWMPhoto(option, data, imgUri);
//                        String image = handleImageOnKitKat(imgUri);
                        // 指定更新某个文件, 添加到媒体库(相册)
                        MediaScannerConnection.scanFile(WLConfig.getContext(), new String[]{savePath},
                                null, null);
                        return Observable.just(savePath);
                    } else {
                        return Observable.just("");
                    }
                })
                .onErrorReturn((t) -> {
                    t.printStackTrace();
                    return "";
                })
                .map((path) -> {
                    if (TextUtils.isEmpty(path)) {
                        return DataResult.cancel();
                    } else {
                        RecordResult photo = RecordResult.photo(path);
                        return DataResult.back(photo);
                    }
                });
    }

    /**
     * 跳转图库并获取界面返回值
     *
     * @param option
     * @return
     */
    public static Observable<DataResult<List<String>>> startAlbum(IRxOption option, int maxCount) {
        return option.getRxPermission().request(ALBUM_PERMISSION)
                .flatMap(success -> {
                    if (success) {
                        return actualAlbum(option, maxCount);
                    } else {
                        Toasts.show("获取权限失败, 跳转相册界面失败!");
                        return Observable.empty();
                    }
                })
                .onErrorReturn(t -> DataResult.cancel()); // 发生异常时返回取消事件
    }

    /**
     * 实际跳转图库方法, 此方法存在权限风险
     * 需申请权限后才能调用
     *
     * @param option
     * @param maxCount
     * @return
     */
    private static Observable<DataResult<List<String>>> actualAlbum(IRxOption option, int maxCount) {
        RxResult rxResult = option.getRxResult();

        CaptureStrategy strategy = new CaptureStrategy(
                true,
                WLConfig.getAuthCode()
        );

        int size = WLConfig.getContext().getResources()
                .getDimensionPixelSize(R.dimen.grid_expected_size);

        Matisse.from(rxResult.getFragment())
                .choose(EnumSet.of(JPEG, PNG))
                .countable(maxCount > 1)
                .gridExpectedSize(size) //一行3列
                .maxSelectable(maxCount)
                .showSingleMediaType(true)
                .captureStrategy(strategy)
                .imageEngine(new GlideEngine())
                .forResult(RC_ALBUM);

        return rxResult.getResultEvent(RC_ALBUM)
                .mapBy(intent -> {
                    //原始图片
                    return Matisse.obtainPathResult(intent);
                });
    }

    /**
     * 跳转视频并获取界面返回值
     *
     * @param option
     * @return
     */
    public static Observable<DataResult<List<String>>> startVideoAlbum(IRxOption option, int maxCount) {
        return option.getRxPermission().request(ALBUM_PERMISSION)
                .flatMap(success -> {
                    if (success) {
                        return actualVideoAlbum(option, maxCount);
                    } else {
                        Toasts.show("获取权限失败, 跳转相册界面失败!");
                        return Observable.empty();
                    }
                })
                .onErrorReturn(t -> DataResult.cancel()); // 发生异常时返回取消事件
    }

    /**
     * 实际跳转图库方法, 此方法存在权限风险
     * 需申请权限后才能调用
     *
     * @param option
     * @param maxCount
     * @return
     */
    private static Observable<DataResult<List<String>>> actualVideoAlbum(IRxOption option, int maxCount) {
        RxResult rxResult = option.getRxResult();

        CaptureStrategy strategy = new CaptureStrategy(
                true,
                WLConfig.getAuthCode()
        );

        int size = WLConfig.getContext().getResources()
                .getDimensionPixelSize(R.dimen.grid_expected_size);

        Matisse.from(rxResult.getFragment())
                .choose(EnumSet.of(MimeType.MP4))
                .countable(true)
                .gridExpectedSize(size) //一行3列
                .maxSelectable(maxCount)
                .showSingleMediaType(true)
                .captureStrategy(strategy)
                .imageEngine(new GlideEngine())
                .forResult(RC_ALBUM);

        return rxResult.getResultEvent(RC_ALBUM)
                .mapBy(intent -> {
                    //原始图片
                    return Matisse.obtainPathResult(intent);
                });
    }

    public static Observable<String> startFile(IRxOption option) {
        return option.getRxPermission().request(ALBUM_PERMISSION)
                .flatMap(success -> {
                    if (success) {
                        return actualFile(option);
                    } else {
                        Toasts.show("获取权限失败, 跳转拍摄界面失败!");
                        return Observable.empty();
                    }
                });
    }

    /**
     * 跳转系统文件管理
     *
     * @param option
     * @return
     */
    private static ObservableSource<String> actualFile(IRxOption option) {
        //调用系统文件管理器打开指定路径目录
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setDataAndType(Uri.fromFile(dir.getParentFile()), "file/*.txt");
        //intent.setType("file/*.txt"); //华为手机mate7不支持
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return option.getRxResult().start(intent, RC_FILE)
                .take(1)
                .flatMap(result -> {
                    if (result.isSuccess()) {
                        Intent data = result.getData();
                        Uri uri = data.getData();

                        if (uri != null) {
                            String path = null;

                            try {
                                path = FileSaveHelper.getPath(
                                        WLConfig.getContext(), uri);
                            } catch (Exception e) {
                                e.printStackTrace();
                                path = FileSaveHelper.getRealFilePath(
                                        WLConfig.getContext(), uri);
                            }

                            if (path != null) {
                                File file = new File(path);
                                if (file.exists()) {
                                    return Observable.just(path);
                                }
                            }
                        }
                    }
                    return Observable.just("");
                });
    }

    /**
     * 4.4版本以下对返回的图片Uri的处理：
     * 就是从返回的Intent中取出图片Uri，直接显示就好
     *
     * @param data 调用系统相册之后返回的Uri
     */
    private static String handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        return getImagePath(uri, null);
    }

    /**
     * 4.4版本以上对返回的图片Uri的处理：
     * 返回的Uri是经过封装的，要进行处理才能得到真实路径
     */
    @TargetApi(19)
    private static String handleImageOnKitKat(Uri uri) {
        String imagePath = "";
        if (DocumentsContract.isDocumentUri(WLConfig.getContext(), uri)) {
            //如果是document类型的Uri，则提供document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的uri，则进行普通处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的uri，则直接获取路径
            imagePath = uri.getPath();
        }

        return imagePath;
    }

    /**
     * 将Uri转化为路径
     *
     * @param uri       要转化的Uri
     * @param selection 4.4之后需要解析Uri，因此需要该参数
     * @return 转化之后的路径
     */
    private static String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = WLConfig.getContext().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 跳转剪切界面
     *
     * @param option
     * @param path
     * @return
     */
    public static Observable<DataResult<String>> startCrop(IRxOption option, String path) {
        return startCrop(option, path, 600, 1);
    }

    /**
     * 跳转剪切界面
     *
     * @param option
     * @param path
     * @param whRatio
     * @return
     */
    public static Observable<DataResult<String>> startCrop(IRxOption option, String path, float whRatio) {
        return startCrop(option, path, 600, whRatio);
    }

    /**
     * 跳转剪切界面
     *
     * @param option
     * @param path
     * @param size
     * @param whRatio
     * @return
     */
    public static Observable<DataResult<String>> startCrop(IRxOption option, String path, int size, float whRatio) {
        return option.getRxPermission().request(ALBUM_PERMISSION)
                .flatMap(success -> {
                    if (success) {
                        return actualCrop(option, path, size, whRatio);
                    } else {
                        Toasts.show("获取权限失败, 跳转相册界面失败!");
                        return Observable.empty();
                    }
                })
                .onErrorReturn(t -> {
                    t.printStackTrace();
                    return DataResult.cancel();
                }); // 发生异常时返回取消事件
    }

    /**
     * 跳转实际剪切界面
     *
     * @param option
     * @param path
     * @param size
     * @return
     */
    private static Observable<DataResult<String>> actualCrop(IRxOption option, String path, int size, float whRatio) {
        RxResult rxResult = option.getRxResult();

        Uri sUri = UriUtil.getUri(path);

        String dir = Environment.getExternalStorageDirectory()
                .getAbsoluteFile() + "/crop-image/";

        FileUtils.createOrExistsDir(dir);

        Uri dUri = Uri.fromFile(new File(dir,
                System.currentTimeMillis() + "_crop_temp.jpg"));

        UCrop.Options options = new UCrop.Options();
        options.setHideBottomControls(true);

        ResultFragment fragment = rxResult.getFragment();
        UCrop.of(sUri, dUri)
                .withOptions(options)
                .withAspectRatio(1F, whRatio)
                .withMaxResultSize(size, size)
                .start(fragment.getActivity(), fragment);

        return rxResult.getResultEvent(UCrop.REQUEST_CROP)
                .mapBy(intent -> {
                    Uri resultUri = UCrop.getOutput(intent);
                    String actualUrl = UriUtil.getFilePathByUri(resultUri);
                    return actualUrl;
                });
    }

}
