package cn.spsilab.locatethings;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import cn.spsilab.locatethings.module.ResponseResult;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Feng on 2/24/2017.
 * methods about load,and upload
 */

public class PictureUtil {

    /**
     * get real path by uri
     */
    private static String getRealPathFromURI(Uri contentURI, Context context) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    /**
     * get bitmap by uri
     *
     * @param uri img uri
     * @return bitmap to img
     */
    public static Bitmap getSmallBitmap(Uri uri, int width, int height, Context context) {
        return getSmallBitmap(getRealPathFromURI(uri, context), width, height);
    }

    /**
     * 计算图片的缩放值
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 根据路径获得突破并压缩返回bitmap用于显示
     */
    private static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    protected static void saveBitMap(Bitmap bitmap, String imgName) {
        String savePath = Environment.getExternalStorageDirectory() + "/image/" + imgName;
        File saveFile = new File(savePath);
        if (!saveFile.exists()) {
            saveFile.getParentFile().mkdirs();
            try {
                saveFile.createNewFile();
                OutputStream outputStream = new FileOutputStream(saveFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * @param fileUri        the img uri
     * @param id             id
     * @param type           user/item/tag
     * @param uploadCallback callback
     */
    public static void uploadImg(Uri fileUri, long id, int type, final Context context, final NetworkService.NetworkCallback uploadCallback) {
        APIService apiService = NetworkService.getInstance().getService(APIService.class);
        String filePath = PictureUtil.getRealPathFromURI(fileUri, context);
        File file = new File(filePath);
        final RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(context.getContentResolver().getType(fileUri)),
                        file
                );
        RequestBody requestId = RequestBody.create(MultipartBody.FORM, String.valueOf(id));
        RequestBody requestType = RequestBody.create(MultipartBody.FORM, String.valueOf(type));
        final MultipartBody.Part body =
                MultipartBody.Part.createFormData("img", file.getName(), requestFile);
        Call<ResponseResult<Map<String, Object>>> call = apiService.upload(requestId, requestType, body);
        call.enqueue(new Callback<ResponseResult<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ResponseResult<Map<String, Object>>> call,
                                   Response<ResponseResult<Map<String, Object>>> response) {
                if (response.isSuccessful()) {
                    ResponseResult<Map<String, Object>> res = response.body();
                    if (res.getStatus() == context.getResources().getInteger(R.integer.UPDATE_SUCCESS)) {
                        uploadCallback.onSuccess(res);
                    } else {
                        uploadCallback.onFailure(res, new RuntimeException(res.getMsg()));
                    }
                } else {
                    uploadCallback.onFailure(ResponseResult.build(context.getResources().getInteger(R.integer.UPDATE_FAILED), "upload img failed , the server occur some error"), new RuntimeException(""));
                }
            }

            @Override
            public void onFailure(Call<ResponseResult<Map<String, Object>>> call, Throwable t) {
                uploadCallback.onFailure(ResponseResult.build(context.getResources().getInteger(R.integer.UPDATE_FAILED), "connect to server error"), t);
            }
        });
    }

    /**
     * if your url is /images/12.jpg, the request url is baseurl + url
     * if url is http://ab.xyz/23.jpg the request url is url
     * thd load img save in local storage,
     * next load will read form local
     */
    public static void getPicture(final String url, final ImageView show, final int defaultImgId) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        String name = url.substring(url.lastIndexOf("/") + 1);
        String path = Environment.getExternalStorageDirectory() + "/image/" + name;
        final File file = new File(path);
        // if don't save in local, download it and save
        if (!file.exists()) {
            APIService apiService = NetworkService.getInstance().getService(APIService.class);
            Call<ResponseBody> resp = apiService.getPicture(url);
            resp.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        file.getParentFile().mkdirs();
                        if (file.createNewFile()) {
                            InputStream is = response.body().byteStream();
                            // save
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
                            try {
                                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
                                byte[] bytes = new byte[1024];
                                int l = 0;
                                while ((l = bufferedInputStream.read(bytes)) != -1) {
                                    bufferedOutputStream.write(bytes, 0, l);
                                }
                                bufferedOutputStream.flush();
                                bufferedOutputStream.close();
                                bufferedInputStream.close();
                            } catch (java.io.IOException e) {
                                e.printStackTrace();
                            }
                            // then set img
                            Bitmap img = BitmapFactory.decodeFile(file.getAbsolutePath());
                            if (img == null) {
                                show.setImageResource(defaultImgId);
                            } else {
                                show.setImageBitmap(img);
                            }
                        }
                    } catch (IOException e) {
                        show.setImageResource(defaultImgId);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // if failed, set default img
                    show.setImageResource(defaultImgId);
                }
            });
        } else {
            // if already save
            Bitmap img = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (img == null) {
                show.setImageResource(defaultImgId);
            } else {
                show.setImageBitmap(img);
            }
        }
    }
}
