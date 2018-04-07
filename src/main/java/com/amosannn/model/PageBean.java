package com.amosannn.model;

import java.util.ArrayList;
import java.util.List;

public class PageBean<T> {

	private int allPage;
	private int curPage;
	private int allCount;

	private List<T> list = new ArrayList<T>();

	public PageBean() {
	}

	public PageBean(int allPage, int curPage) {
		this.allPage = allPage;
		this.curPage = curPage;
	}

	public PageBean(int allPage, int curPage, int allCount) {
		this.allPage = allPage;
		this.curPage = curPage;
		this.allCount = allCount;
	}

	public int getAllPage() {
		return allPage;
	}

	public void setAllPage(int allPage) {
		this.allPage = allPage;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getAllCount() {
		return allCount;
	}

	public void setAllCount(int allCount) {
		this.allCount = allCount;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "PageBean{" + "allPage=" + allPage + ", curPage=" + curPage + ", list=" + list + '}';
	}
}
