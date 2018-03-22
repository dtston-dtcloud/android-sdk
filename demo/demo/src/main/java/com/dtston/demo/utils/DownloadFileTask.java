package com.dtston.demo.utils;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadFileTask extends AsyncTask<String, Integer, Integer> {
	
	public static final int RESULT_OK = 1;
	public static final int ERROR_OTHER = -1;
	public static final int ERROR_MALFORMED_URL_EXCEPTION = -2;
	public static final int ERROR_FILE_NOT_FOUND = -3;
	
	private OnDownListener mListener;
	private boolean cancel = false;
	private String saveFilePath = "";
	
	public DownloadFileTask() {
	}
	
    @Override
    protected Integer doInBackground(String... sUrl) {
    	Integer result = ERROR_OTHER;
        try {
            URL url = new URL(sUrl[0]);
            saveFilePath = sUrl[1];
            URLConnection connection = url.openConnection();
            connection.connect();
            int fileLength = connection.getContentLength();
            // 下载文件
            File file = new File(saveFilePath);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(file);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
            	if (cancel) {
            		break;
            	}
                total += count;
                publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            
            if (total == fileLength) {
            	result = RESULT_OK;
            }
        } catch (Throwable e) {
        	if (e instanceof MalformedURLException) {
        		result = ERROR_MALFORMED_URL_EXCEPTION;
        	} else if (e instanceof java.io.FileNotFoundException) {
				result = ERROR_FILE_NOT_FOUND;
			}
			System.out.println(DownloadFileTask.class.getSimpleName() + "DownloadFile Error: " + e.toString());
        }
        
        return result;
    } 
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        if (mListener != null) {
			mListener.onProgress(progress[0]);
		}
    }
    
    @Override
    protected void onPostExecute(Integer result) {
    	if (cancel) {
    		return;
    	}
    	if (result == RESULT_OK) {
    		if (mListener != null) {
    			mListener.onResult(saveFilePath);
    		}
    	} else {
    		if (mListener != null) {
    			mListener.onError(result);
    		}
    	}
    }
    
    public interface OnDownListener {
    	public void onProgress(int progress);
    	public void onResult(String saveFilePath);
    	public void onError(int error);
    }

	public OnDownListener getListener() {
		return mListener;
	}

	public void setListener(OnDownListener listener) {
		this.mListener = listener;
	}

	public void cancel() {
		cancel = true;
	}
	
}
