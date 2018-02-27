package com.xyy.bill.def;

import java.util.List;
import java.util.Vector;

import com.xyy.bill.event.DelEvent;
import com.xyy.bill.event.DelEventListener;
import com.xyy.bill.event.LoadEvent;
import com.xyy.bill.event.LoadEventListener;
import com.xyy.bill.event.ParseExcelEvent;
import com.xyy.bill.event.ParseExcelEventListener;
import com.xyy.bill.event.PostDelEvent;
import com.xyy.bill.event.PostDelEventListener;
import com.xyy.bill.event.PostLoadEvent;
import com.xyy.bill.event.PostLoadEventListener;
import com.xyy.bill.event.PostSaveEvent;
import com.xyy.bill.event.PostSaveEventListener;
import com.xyy.bill.event.PreDelEvent;
import com.xyy.bill.event.PreDelEventListener;
import com.xyy.bill.event.PreLoadEvent;
import com.xyy.bill.event.PreLoadEventListener;
import com.xyy.bill.event.PreSaveEvent;
import com.xyy.bill.event.PreSaveEventListener;
import com.xyy.bill.event.SaveEvent;
import com.xyy.bill.event.SaveEventListener;
import com.xyy.bill.event.StatusChangedEvent;
import com.xyy.bill.event.StatusChangedEventListener;
import com.xyy.bill.meta.BillEvent;
import com.xyy.bill.meta.BillEvent.EventType;
import com.xyy.bill.meta.BillFormMeta;

public abstract class BillEventSupport {
	private Vector<PreLoadEventListener> preLoadEventListeners=new Vector<>();
	private Vector<LoadEventListener> loadEventListeners=new Vector<>();
	private Vector<PostLoadEventListener> postLoadEventListeners=new Vector<>();
	private Vector<PreSaveEventListener> preSaveEventListeners=new Vector<>();
	private Vector<SaveEventListener> saveEventListeners=new Vector<>();
	private Vector<PostSaveEventListener> postSaveEventListeners=new Vector<>();
	private Vector<PreDelEventListener> preDelEventListeners=new Vector<>();
	private Vector<DelEventListener> delEventListeners=new Vector<>();
	private Vector<PostDelEventListener> postDelEventListeners=new Vector<>();
	private Vector<StatusChangedEventListener> statusChangedEventListeners=new Vector<>();
	private Vector<ParseExcelEventListener> parseExcelEventListeners = new Vector<>();
	
	public void addParseExcelEventListeners(ParseExcelEventListener listener){
		parseExcelEventListeners.add(listener);
	}
	
	public void fireParseExcelEvent(ParseExcelEvent event){
		for (ParseExcelEventListener listener : parseExcelEventListeners) {
			listener.execute(event);
		}
	}
	
	public void addStatusChangedEventListener(StatusChangedEventListener listener){
		statusChangedEventListeners.add(listener);
	}
	
	
	public void fireStatusChangedEvent(StatusChangedEvent event){
		for (StatusChangedEventListener listener : statusChangedEventListeners) {
			listener.execute(event);
		}
	}
	
	public void addPreLoadEventListener(PreLoadEventListener listener){
		preLoadEventListeners.add(listener);
	}
	
	public void addLoadEventListener(LoadEventListener listener){
		loadEventListeners.add(listener);
	}
	
	
	public void addPostLoadEventListener(PostLoadEventListener listener){
		postLoadEventListeners.add(listener);
	}
	
	public void firePreLoadEvent(PreLoadEvent event){
		for (PreLoadEventListener preLoadEventListener : preLoadEventListeners) {
			preLoadEventListener.execute(event);
		}
	}
	
	public void fireLoadEvent(LoadEvent event){
		for (LoadEventListener loadEventListener : loadEventListeners) {
			loadEventListener.execute(event);
		}
	}
	
	public void firePostLoadEvent(PostLoadEvent event){
		for (PostLoadEventListener postLoadEventListener : postLoadEventListeners) {
			postLoadEventListener.execute(event);
		}
	}
	
	
	public void addPreSaveEventListener(PreSaveEventListener listener){
		preSaveEventListeners.add(listener);
	}
	
	
	public void addSaveEventListener(SaveEventListener listener){
		saveEventListeners.add(listener);
	}
	
	
	public void addPostSaveEventListener(PostSaveEventListener listener){
		postSaveEventListeners.add(listener);
	}
	
	
	public void firePreSaveEvent(PreSaveEvent event){
		for (PreSaveEventListener preSaveEventListener : preSaveEventListeners) {
			preSaveEventListener.execute(event);
		}
	}
	
	public void fireSaveEvent(SaveEvent event){
		for (SaveEventListener saveEventListener : saveEventListeners) {
			saveEventListener.execute(event);
		}
	}
	
	public void firePostSaveEvent(PostSaveEvent event){
		for (PostSaveEventListener postSaveEventListener : postSaveEventListeners) {
			postSaveEventListener.execute(event);
		}
	}
	
	public void addPreDelEventListener(PreDelEventListener listener){
		preDelEventListeners.add(listener);
	}
	
	
	public void addDelEventListener(DelEventListener listener){
		delEventListeners.add(listener);
	}
	
	
	public void addPostDelEventListener(PostDelEventListener listener){
		postDelEventListeners.add(listener);
	}
	
	
	
	public void firePreDelEvent(PreDelEvent event){
		for (PreDelEventListener preDelEventListener : preDelEventListeners) {
			preDelEventListener.execute(event);
		}
	}
	
	public void fireDelEvent(DelEvent event){
		for (DelEventListener delEventListener : delEventListeners) {
			delEventListener.execute(event);
		}
	}
	
	public void firePostDelEvent(PostDelEvent event){
		for (PostDelEventListener postDelEventListener : postDelEventListeners) {
			postDelEventListener.execute(event);
		}
	}
	
	
	public abstract BillFormMeta getView();
	
	/**
	 * 构建事件处理器
	 */
	public void buildBillEventProcessor(){
		BillFormMeta view=this.getView();
		if(view!=null){
			List<BillEvent> billEvents=view.getBillMeta().getBillEvents();
			for(BillEvent event:billEvents){
				EventType type=event.getEventType();
				switch (type) {
				//PreLoad, Load, PostLoad, PreSave, Save, PostSave,
				//PreDel, Del, PostDel
				case PreLoad:
					try {
						String clazz=event.getProcessor();
						this.addPreLoadEventListener((PreLoadEventListener)Class.forName(clazz).newInstance());
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case Load:
					try {
						String clazz=event.getProcessor();
						this.addLoadEventListener((LoadEventListener)Class.forName(clazz).newInstance());
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case PostLoad:
					try {
						String clazz=event.getProcessor();
						this.addPostLoadEventListener((PostLoadEventListener)Class.forName(clazz).newInstance());
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case PreSave:
					try {
						String clazz=event.getProcessor();
						this.addPreSaveEventListener((PreSaveEventListener)Class.forName(clazz).newInstance());
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case Save:
					try {
						String clazz=event.getProcessor();
						this.addSaveEventListener((SaveEventListener)Class.forName(clazz).newInstance());
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case PostSave:
					try {
						String clazz=event.getProcessor();
						this.addPostSaveEventListener((PostSaveEventListener)Class.forName(clazz).newInstance());
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case PreDel:
					try {
						String clazz=event.getProcessor();
						this.addPreDelEventListener((PreDelEventListener)Class.forName(clazz).newInstance());
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case Del:
					try {
						String clazz=event.getProcessor();
						this.addDelEventListener((DelEventListener)Class.forName(clazz).newInstance());
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case PostDel:
					try {
						String clazz=event.getProcessor();
						this.addPostDelEventListener((PostDelEventListener)Class.forName(clazz).newInstance());
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case StatusChanged:
					try {
						String clazz=event.getProcessor();
						this.addStatusChangedEventListener((StatusChangedEventListener)Class.forName(clazz).newInstance());
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case ParseExcel:
					try {
						String clazz=event.getProcessor();
						this.addParseExcelEventListeners((ParseExcelEventListener)Class.forName(clazz).newInstance());
					} catch (Exception e) {
						e.printStackTrace();
					}
				default:
					break;
				}
			}
			
		}
	}
	
	
	
	
	
	
	
}
