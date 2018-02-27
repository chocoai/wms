package com.xyy.erp.platform.system.controller;


import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.xyy.bill.services.util.AreaService;
import com.xyy.bill.services.util.FileService;
import com.xyy.erp.platform.common.tools.ToolContext;
import com.xyy.util.UUIDUtil;


/**
 * 公共控制器
 * @author caofei
 *
 */
public class ImageController extends Controller {
	
	private static final Log LOG = Log.getLog(ImageController.class);
	
	private static final String BASE_PATH = PathKit.getWebRootPath()+ File.separator + "upload";
	
	private static final String ROOT = JFinal.me().getServletContext().getContextPath()+ File.separator + "upload";
	
	/**
	 * 文件上传
	 */
	public void uploadFile() throws Exception{
		List<UploadFile> files = new ArrayList<>();
		try {
			files = this.getFiles();
			String pid = this.getPara("pid");
			String tid = this.getPara("tid");
			String userId = this.getPara("userId");
			if(files != null && files.size() > 0){
				try{
					this.mutilUploadFile(files,tid,pid,userId);
					List<Record> attachList = Db.find("select * from tb_pd_processattach where pi = '"+pid+"' order by createTime desc");
					this.setAttr("status", 1);
					this.setAttr("attachList", attachList);
					this.renderJson();
				}catch(IOException e){
					this.setAttr("status", 0);
					this.setAttr("error", "upload file error.");
				}
			}else{
				this.setAttr("status", 0);
				this.setAttr("error", "not found file.");
			}
		} catch (RuntimeException e) {
			this.setAttr("status", 0);
			this.setAttr("error", "文件大小不可超过10M");
			this.renderJson();
			return;
		}
		
		
	}
	
	/**
	 * 删除附件
	 */
	public void delFile() {
		int attachId = this.getParaToInt("id");
		Record atttach = Db.findFirst("select * from tb_pd_processattach where id = "+attachId+"");
		String fileUrl = PathKit.getWebRootPath() + atttach.getStr("url");
		File file = new File(fileUrl);
		if (file.exists()) {
			file.delete();
			if (atttach.getInt("type")==1) {
				String thumbUrl = fileUrl.substring(0, fileUrl.lastIndexOf("."))+"_thumb"+fileUrl.substring(fileUrl.lastIndexOf("."));
				File tFile = new File(thumbUrl);
				if (tFile.exists()) {
					tFile.delete();
					Db.deleteById("tb_pd_processattach", attachId);
					this.setAttr("status", 1);
					this.renderJson();
					return;
				}
			}else {
				Db.deleteById("tb_pd_processattach", attachId);
				this.setAttr("status", 1);
				this.renderJson();
				return;
			}
		}
		
		this.setAttr("status", 0);
		this.renderJson();
	}
	

	private Map<String, Object> mutilUploadFile(List<UploadFile> files, String tid, String pid, String userId) throws IOException{
		Map<String, Object> retMap = new HashMap<>();
		for(UploadFile uploadFile : files){
			String fileName = uploadFile.getOriginalFileName();
			File file = uploadFile.getFile();
			FileService fs = new FileService();
			String newFileName = UUIDUtil.newUUID()+fileName.substring(fileName.lastIndexOf("."));
			String newFIleRoot = BASE_PATH + File.separator +pid + File.separator +newFileName;
			File target = new File(newFIleRoot);
			try {
				File f = new File(BASE_PATH + File.separator +pid + File.separator);
		        // 创建文件夹
		        if (!f.exists()) {
		            f.mkdirs();
		        }
				target.createNewFile();
				fs.fileChannelCopy(file, target);
				retMap.put(fileName, 1);
				file.delete();
				Record record = new Record();
				if (fileName.contains(".jpg")||fileName.contains(".png")||fileName.contains(".jpeg")||fileName.contains(".bmp")
						||fileName.contains(".JPG")||fileName.contains(".PNG")||fileName.contains(".JPEG")||fileName.contains(".BMP")) {
					record.set("type", 1);
					this.CompressImg(BASE_PATH + File.separator +pid + File.separator +newFileName);
				}else {
					record.set("type", 2);
				}
				record.set("url", ROOT + File.separator +pid + File.separator +newFileName);
				record.set("name", fileName);
				record.set("pi", pid);
				record.set("ti", tid);
				if(ToolContext.getCurrentUser(getRequest(), true)==null){
					record.set("userId", userId);
				}else {
					record.set("userId", ToolContext.getCurrentUser(getRequest(), true).getId());
				}
				record.set("createTime", new Timestamp(System.currentTimeMillis()));
				Db.save("tb_pd_processattach", record);
			} catch (IOException e) {
				LOG.error(e.getMessage());
				retMap.put(fileName, 0);
			}
		}
		return retMap;
	}
	
	/**
	 * 获取省市区信息
	 */
	public void findAreaByParentId(){
		int parentId = this.getParaToInt("parentId");
		AreaService areaService = new AreaService();
		this.setAttr("data", areaService.findAreaByParentId(parentId));
		this.renderJson();
    }
	
	private void CompressImg(String fileRoot) {  
	    /** 
	     * d://3.jpg 源图片 
	     * d://31.jpg 目标图片 
	     * 压缩宽度和高度都是1000 
	     *  
	     */  
		String newFileRoot = fileRoot.split("\\.")[0]+"_thumb."+fileRoot.split("\\.")[1];
	    reduceImg(fileRoot, newFileRoot, 320, 210,null);  
	  
	}  
	
	/** 
     * 采用指定宽度、高度或压缩比例 的方式对图片进行压缩 
     * @param imgsrc 源图片地址 
     * @param imgdist 目标图片地址 
     * @param widthdist 压缩后图片宽度（当rate==null时，必传） 
     * @param heightdist 压缩后图片高度（当rate==null时，必传） 
     * @param rate 压缩比例  
     */  
	private static void reduceImg(String imgsrc, String imgdist, int widthdist,  
            int heightdist, Float rate) {  
        try {  
            File srcfile = new File(imgsrc);  
            // 检查文件是否存在  
            if (!srcfile.exists()) {  
                return;  
            }  
            // 如果rate不为空说明是按比例压缩  
            if (rate != null && rate > 0) {  
                // 获取文件高度和宽度  
                int[] results = getImgWidth(srcfile);  
                if (results == null || results[0] == 0 || results[1] == 0) {  
                    return;  
                } else {  
                    widthdist = (int) (results[0] * rate);  
                    heightdist = (int) (results[1] * rate);  
                }  
            }  
            // 开始读取文件并进行压缩  
            Image src = javax.imageio.ImageIO.read(srcfile);  
            BufferedImage tag = new BufferedImage((int) widthdist,  
                    (int) heightdist, BufferedImage.TYPE_INT_RGB);  
  
            tag.getGraphics().drawImage(  
                    src.getScaledInstance(widthdist, heightdist,  
                            Image.SCALE_SMOOTH), 0, 0, null);  
  
            FileOutputStream out = new FileOutputStream(imgdist);  
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);  
            encoder.encode(tag);  
            out.close();  
  
        } catch (IOException ex) {  
            ex.printStackTrace();  
        }  
    }  
	
	/** 
     * 获取图片宽度 
     *  
     * @param file 
     *            图片文件 
     * @return 宽度 
     */  
	private static int[] getImgWidth(File file) {  
        InputStream is = null;  
        BufferedImage src = null;  
        int result[] = { 0, 0 };  
        try {  
            is = new FileInputStream(file);  
            src = javax.imageio.ImageIO.read(is);  
            result[0] = src.getWidth(null); // 得到源图宽  
            result[1] = src.getHeight(null); // 得到源图高  
            is.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return result;  
    }  

}
