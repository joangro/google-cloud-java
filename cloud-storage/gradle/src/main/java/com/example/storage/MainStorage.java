package com.example.storage;

import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;

import java.net.URL;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage.SignUrlOption;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.HttpMethod;

import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.appengine.api.urlfetch.HTTPHeader;

@WebServlet(name = "MainStorage", value = "/")
public class MainStorage extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Ho
		String bucketName = "public-bucket-joan";
		String blobName = "testFileStack";
		String keyPath = "/home/grauj/google-cloud-java/cloud-storage/gradle/key.json";
		BlobId blobId = BlobId.of(bucketName, blobName);
		Storage storage = StorageOptions.getDefaultInstance().getService();
		URL signedUrl = storage.signUrl(BlobInfo.newBuilder(bucketName, blobName).build(), 14, TimeUnit.DAYS,
						SignUrlOption.signWith(ServiceAccountCredentials.fromStream(new FileInputStream(keyPath))),
						SignUrlOption.httpMethod(HttpMethod.PUT));

		String content = "My-File-contents";

		HTTPRequest upload_request = new HTTPRequest(signedUrl, HTTPMethod.PUT);
		upload_request.setPayload(content.getBytes(StandardCharsets.UTF_8));
		HTTPHeader set_resumable = new HTTPHeader("uploadType", "resumable");
		upload_request.setHeader(set_resumable);
        	URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
		fetcher.fetchAsync(upload_request);
		response.setContentType("text/plain");
		response.getWriter().println("Hello Storage");
	}
}
