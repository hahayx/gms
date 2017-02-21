package com.hh.novel.biquge;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.hh.common.data.MapData;
import com.hh.common.utils.DateUtils;
import com.hh.common.utils.IOUtil;
import com.hh.common.utils.WebUtil;
import com.hh.db.DbException;
import com.hh.db.util.DbUtil;
import com.hh.db.util.SqlInputData;

public class WormUtil {

	public static String getHtml(String url) throws Exception{
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet http = new HttpGet(url);
		HttpResponse response = httpClient.execute(http);
		return IOUtil.inStream2String(response.getEntity().getContent());
	}
	
	public static String getRegVal(String html,String reg,int group){
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(html);
		if (m.find()) {
			return m.group(group);
		}
		return null;
	}
	
	public static void download(String url,String path) throws Exception{
		HttpClient httpClient = new DefaultHttpClient();;
		HttpGet http = new HttpGet(url);
		HttpResponse response = httpClient.execute(http);
	    IOUtil.saveFile(response.getEntity().getContent(), path);
	}
	 
	public static boolean isExistFile(String path) {
		File file = new File(path);
		return file.exists();
	}
	
	public static boolean isExistNovel(int bookId) throws DbException {
		return DbUtil.getInstance("novel").exist(new SqlInputData("Novel").addWhereField("bookId", bookId));
	}
	
	public static void insertBook(MapData book) throws DbException {
		DbUtil.getInstance("novel").insert(new SqlInputData("Novel").
				addInsertField("BookId", book.getInt("bookId")).
				addInsertField("BookName", book.getString("bookName")).
				addInsertField("Auther", book.getString("auther")).
				addInsertField("Info", book.getString("info")).
				addInsertField("BookType", book.getInt("bookType")).
				addInsertField("BookStatus", book.getInt("bookStatus")).
				addInsertField("LastChapterName", book.getString("lastChapterName")).
				addInsertField("LastChapterIndex", book.getInt("lastChapterIndex")).
				addInsertField("UpdateTime", DateUtils.parseDateYDM(book.getString("time"))));
	}
	
	public static void updateBook(MapData book) throws DbException {
		DbUtil.getInstance("novel").update(new SqlInputData("Novel").
				addWhereField("BookId", book.getInt("bookId")).
				addUpdateField("LastChapterName", book.getString("lastChapterName")).
				addUpdateField("LastChapterIndex", book.getInt("lastChapterIndex")).
				addUpdateField("UpdateTime", DateUtils.parseDateYDM(book.getString("time"))));
	}
	
	
	public static List<MapData> getUpdateList(int limit) {
		try {
			return DbUtil.getInstance("novel").selectList(new SqlInputData("Novel",0,limit).
					addOrderField("UpdateTime", "desc"),WebUtil.LowerCaseMapDataBuilder);
		} catch (DbException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	public static List<MapData> novelList(int bookType) {
		try {
			return DbUtil.getInstance("novel").selectList(new SqlInputData("Novel").
					addWhereField("BookType", bookType).
					addOrderField("UpdateTime", "desc"),WebUtil.LowerCaseMapDataBuilder);
		} catch (DbException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	public static List<MapData> finishList() {
		try {
			return DbUtil.getInstance("novel").selectList(new SqlInputData("Novel").
					addWhereField("BookStatus", 2).
					addOrderField("UpdateTime", "desc"),WebUtil.LowerCaseMapDataBuilder);
		} catch (DbException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	public static List<MapData> allList() {
		try {
			return DbUtil.getInstance("novel").selectList(new SqlInputData("Novel"),WebUtil.LowerCaseMapDataBuilder);
		} catch (DbException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
}
