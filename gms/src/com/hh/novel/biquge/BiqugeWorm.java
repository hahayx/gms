package com.hh.novel.biquge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.hh.common.data.MapData;
import com.hh.common.utils.VelocityUtil;
import com.hh.common.utils.WebUtil;
import com.hh.db.DbManager;

public class BiqugeWorm {

	private static String bookPath ;
	private static String chapterPath ;
	private static String coverPath ;
	private static String vmPath ;
	
	public static void doUpdate() {
		List<String> urls = UpdateListWorm.getUpdateList();
		ThreadPoolExecutor executor =  new ThreadPoolExecutor(1, 1,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		System.out.println(urls.size());
		for (String url : urls) {
			executor.submit(new WormRun(url));
		}
		while (true) {
			if (executor.getQueue().isEmpty()) {
				break;
			}
			try {
				Thread.sleep(60*1000);
			} catch (Exception e) {
				
			}
		}
		executor.shutdown();
	}
	
	public static void doAll() {
		List<String> urls = WormUtil.readFileToList(vmPath+"/tw.csv");
		ThreadPoolExecutor executor =  new ThreadPoolExecutor(5, 5,0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		System.out.println(urls.size());
		for (String url : urls) {
			executor.submit(new WormRun(url));
		}
		while (true) {
			if (executor.getQueue().isEmpty()) {
				break;
			}
			try {
				Thread.sleep(60*1000);
			} catch (Exception e) {
				
			}
		}
		executor.shutdown();
	}
	
	public static void downloadBook(String url) {
		try {
			MapData book = NovelInfoWorm.getInfo(url);
			
			List<MapData> chapters = ChapterListWorm.getChapterList(book.getString("srcId"));
			if (chapters.size() <1) {
				return;
			}
			MapData last =chapters.get(chapters.size()-1) ;
			book.set("lastChapterName", last.getString("name"));
			book.set("lastChapterIndex", chapters.size());
			book.set("chapters", chapters);
			VelocityUtil.generateStaticFile(vmPath+"book.vm", String.format(bookPath, book.getInt("bookId")), book);
			int i = 1;
			/*
			for (MapData c : chapters) {
				i++;
				try {
					if ( WormUtil.isExistFile(String.format(chapterPath,book.getInt("bookId"), i))) {
						continue;
					}
					MapData chapter = ChapterInfoWorm.getChapterInfo(c.getString("url"));
					chapter.set("index", i-1);
					VelocityUtil.generateStaticFile(vmPath+"content.vm", String.format(chapterPath, book.getInt("bookId"),i-1), chapter);
				} catch (Exception e) {
					// TODO: handle exception
				}	
			}
			*/
			if (!WormUtil.isExistFile(String.format(coverPath, book.getInt("bookId")))) {
				WormUtil.download(book.getString("img"), String.format(coverPath, book.getInt("bookId")));
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
	
	public static void doIndex() {
		
	}
	
	public static void doTypeList() {
		for (int i = 1; i <= 8; i++) {
			List<MapData> list =  i != 8 ? WormUtil.novelList(i) : WormUtil.finishList();
			String type = typeMap.get(i);
			int count = list.size();
			int limit = 20;
			int page = (count +1)/limit;
			for (int j = 1; j <= page; j++) {
				int offset = (j - 1)*limit;
				MapData data = new MapData();
				data.set("type", type);
				data.set("list", WebUtil.getCachePager(offset, limit, list));
				data.set("count", count);
				data.set("page", page);
				data.set("index", j);
			}
		}
		
		
	}
	
	public static void doSiteMap() {
		
	}
	
	static class WormRun implements Runnable{
		String url ;
		WormRun(String url){
			this.url = url;
		}
		
		@Override
		public void run() {
			downloadBook(url);
		}
		
	}
	
	private static Map<Integer, String> typeMap = new HashMap<Integer, String>();
	
	
	private static void init(String vmPath,String htmlPath) throws Exception {
		DbManager.init("/tmp/config2.xml");
		bookPath = htmlPath+"%s/index.html";
		chapterPath = htmlPath+"book-%s\\%s.html";
		coverPath = htmlPath+"cover\\%s.jpg";
		BiqugeWorm.vmPath = vmPath;
		typeMap.put(1,"玄幻奇幻");
		typeMap.put(2,"武侠仙侠");
		typeMap.put(3,"都市言情");
		typeMap.put(4,"历史军事");
		typeMap.put(5,"科幻灵异");
		typeMap.put(6,"网游竞技");
		typeMap.put(7,"女生频道");
		typeMap.put(8,"完本");
	}
	
	public static void main(String[] args) throws Exception {
		//init(args[0], args[1]);
		init("G:\\home\\biquge\\", "G:\\home\\biquge\\xs\\");
		doAll();
	}
}
