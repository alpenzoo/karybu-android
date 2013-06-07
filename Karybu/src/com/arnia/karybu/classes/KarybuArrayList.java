package com.arnia.karybu.classes;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

public class KarybuArrayList {
	@Element(required = false)
	public KarybuPagination pagination;

	@ElementList(inline = true, required = false)
	public List<KarybuMember> members;

	@ElementList(inline = true, required = false)
	public ArrayList<KarybuMenu> menus;

	@ElementList(inline = true, required = false)
	public ArrayList<KarybuModule> modules;

	@ElementList(inline = true, required = false)
	public ArrayList<KarybuPage> pages;

	@ElementList(inline = true, required = false)
	public ArrayList<KarybuLayout> layouts;

	@ElementList(inline = true, required = false)
	public ArrayList<KarybuDayStats> stats;

	@ElementList(inline = true, required = false)
	public ArrayList<KarybuTextyle> textyles;

	@ElementList(inline = true, required = false)
	public ArrayList<KarybuTextylePost> posts;

	@ElementList(inline = true, required = false)
	public ArrayList<KarybuTextylePage> textylePages;

	@ElementList(inline = true, required = false)
	public ArrayList<KarybuSkin> skins;

	@ElementList(inline = true, required = false)
	public ArrayList<KarybuComment> comments;

	@ElementList(inline = true, required = false)
	public ArrayList<KarybuTheme> themes;

}
