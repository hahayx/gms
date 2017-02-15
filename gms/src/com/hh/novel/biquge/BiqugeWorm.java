package com.hh.novel.biquge;

import java.util.List;

import com.hh.common.data.MapData;
import com.hh.common.utils.VelocityUtil;

public class BiqugeWorm {

	public static void doUpdate() {
		List<String> urls = UpdateListWorm.getUpdateList();
		for (String url : urls) {
			MapData book = NovelInfoWorm.getInfo(url);
			System.out.println(book);
			List<String> chapters = ChapterListWorm.getChapterList(book.getInt("bookId"));
			int i = 1;
			for (String curl : chapters) {
				MapData chapter = ChapterInfoWorm.getChapterInfo(curl);
				chapter.putAll(book);
				try {
					VelocityUtil.generateStaticFile("G:\\home\\biquge\\content.vm", "G:\\home\\biquge\\"+book.getInt("bookId")+"\\"+i+".html", chapter);
				} catch (Exception e) {
					// TODO: handle exception
				}
				i++;
			}
		}
	}
	
	public static void main(String[] args) {
		doUpdate();
	}
}
