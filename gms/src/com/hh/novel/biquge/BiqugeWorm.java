package com.hh.novel.biquge;

import java.util.List;

import com.hh.common.data.MapData;
import com.hh.common.utils.VelocityUtil;
import com.hh.db.DbManager;

public class BiqugeWorm {

	private static final String htmlPath = "G:\\home\\biquge\\";
	private static final String vmPath = "G:\\home\\biquge\\";
	
	public static void doUpdate() {
		List<String> urls = UpdateListWorm.getUpdateList();
		for (String url : urls) {
			downloadBook(url);
		}
	}
	
	public static void doAll() {
		List<String> urls = AllBookListWorm.allUrlList();
		System.out.println(urls.size());
		for (String url : urls) {
			downloadBook(url);
		}
	}
	
	public static void downloadBook(String url) {
		try {
			MapData book = NovelInfoWorm.getInfo(url);
			List<MapData> chapters = ChapterListWorm.getChapterList(book.getInt("srcBookId"));
			if (chapters.size() <1) {
				return;
			}
			MapData last =chapters.get(chapters.size()-1) ;
			book.set("lastChapterName", last.getString("name"));
			book.set("lastChapterIndex", chapters.size());
			System.out.println(book);
			book.set("chapters", chapters);
			VelocityUtil.generateStaticFile(vmPath+"book.vm", htmlPath+book.getInt("bookId")+".html", book);
			int i = 1;
			for (MapData c : chapters) {
				if ( WormUtil.isExistFile(String.format(htmlPath +"book-"+book.getInt("bookId")+"/%s.html", i+1))) {
					continue;
				}
				MapData chapter = ChapterInfoWorm.getChapterInfo(c.getString("url"));
				chapter.set("index", i);
				
				i++;
				
			}
			if (WormUtil.isExistNovel(book.getInt("bookId"))) {
				WormUtil.updateBook(book);
			}else {
				WormUtil.insertBook(book);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		DbManager.init("/tmp/config2.xml");
		doAll();
	}
}
