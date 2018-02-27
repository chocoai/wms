package com.xyy.bill.controller;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.jfinal.upload.UploadFile;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.xyy.bill.services.util.AreaService;
import com.xyy.bill.services.util.FileService;
import com.xyy.erp.platform.common.plugin.PropertiesPlugin;
import com.xyy.erp.platform.common.tools.DictKeys;
import com.xyy.erp.platform.common.tools.StringUtil;
import com.xyy.util.UUIDUtil;

/**
 * 公共控制器
 * 
 * @author caofei
 *
 */
public class CommonController extends Controller {

	private static final Log LOG = Log.getLog(CommonController.class);

	/**
	 * 附件上传
	 */
	public void uploadFile() throws Exception {
		List<UploadFile> files = new ArrayList<>();
		try {
			files = this.getFiles();
			if (files != null && files.size() > 0) {
				try {
					Map<String, Object> retMap = this.mutilUploadFile(files);
					if(Integer.valueOf(retMap.get("status").toString()) == 1){
						this.setAttr("status", 1);
						this.setAttr("fileName", retMap.get("fileName"));
						this.setAttr("filePath", retMap.get("filePath"));
						this.renderJson();
						return ;
					}else{
						this.setAttr("status", 0);
						this.setAttr("error", "upload file failed.");
						return ;
					}
				} catch (IOException e) {
					this.setAttr("status", 0);
					this.setAttr("error", "upload file error.");
					return ;
				}
			} else {
				this.setAttr("status", 0);
				this.setAttr("error", "not found file.");
				return ;
			}
		} catch (RuntimeException e) {
			this.setAttr("status", 0);
			this.setAttr("error", "not found file.");
			this.renderJson();
			return;
		}

	}
	
	/**
	 * 删除附件
	 */
	public void deleteAttachment() {
		String fileName = this.getPara("fileName");
		if(StringUtil.isEmpty(fileName)){
			this.setAttr("status", 0);
			this.setAttr("error", "file name is null.");
			this.renderJson();
			return;
		}
		String filePath = PropertiesPlugin.getParamMapValue(DictKeys.config_basePath) + File.separator + fileName;
		
//		if(isOSLinux()){
//			filePath = fileName;
//		}
		LOG.info("*********删除文件路径********"+filePath);
		File file = new File(filePath);
		if (file.exists()) {
			if(file.delete()){
				this.setAttr("status", 1);
				this.renderJson();
				return;
			}
		}
		this.setAttr("status", 0);
		this.setAttr("error", "delete attachment failed.");
		this.renderJson();
		return;
	}

	private Map<String, Object> mutilUploadFile(List<UploadFile> files) throws IOException {
		Map<String, Object> retMap = new HashMap<>();
		for (UploadFile uploadFile : files) {
			String fileName = uploadFile.getOriginalFileName();
			File file = uploadFile.getFile();
			FileService fs = new FileService();
			String newFileName = UUIDUtil.newUUID() + fileName.substring(fileName.lastIndexOf("."));
			File target = new File(PropertiesPlugin.getParamMapValue(DictKeys.config_basePath) + File.separator + newFileName);
			try {
				File f = new File(PropertiesPlugin.getParamMapValue(DictKeys.config_basePath) + File.separator);
				// 创建文件夹
				if (!f.exists()) {
					f.mkdirs();
				}
				target.createNewFile();
				fs.fileChannelCopy(file, target);
				if(file.delete()){
					retMap.put("filePath", newFileName);
					retMap.put("fileName", fileName);
					retMap.put("status", 1);
				}else{
					retMap.put("status", 0);
				}
			} catch (IOException e) {
				LOG.error(e.getMessage());
				retMap.put("status", 0);
			}
		}
		return retMap;
	}

	/**
	 * 获取省市区信息
	 */
	public void findAreaByParentId() {
		int parentId = this.getParaToInt("parentId");
		AreaService areaService = new AreaService();
		this.setAttr("data", areaService.findAreaByParentId(parentId));
		this.renderJson();
	}

	@SuppressWarnings("unused")
	private void CompressImg(String fileRoot) {
		/**
		 * d://3.jpg 源图片 d://31.jpg 目标图片 压缩宽度和高度都是1000
		 * 
		 */
		String newFileRoot = fileRoot.split("\\.")[0] + "_min." + fileRoot.split("\\.")[1];
		reduceImg(fileRoot, newFileRoot, 320, 210, null);

	}

	/**
	 * 采用指定宽度、高度或压缩比例 的方式对图片进行压缩
	 * 
	 * @param imgsrc
	 *            源图片地址
	 * @param imgdist
	 *            目标图片地址
	 * @param widthdist
	 *            压缩后图片宽度（当rate==null时，必传）
	 * @param heightdist
	 *            压缩后图片高度（当rate==null时，必传）
	 * @param rate
	 *            压缩比例
	 */
	private static void reduceImg(String imgsrc, String imgdist, int widthdist, int heightdist, Float rate) {
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
			BufferedImage tag = new BufferedImage((int) widthdist, (int) heightdist, BufferedImage.TYPE_INT_RGB);

			tag.getGraphics().drawImage(src.getScaledInstance(widthdist, heightdist, Image.SCALE_SMOOTH), 0, 0, null);

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
	
	
	/**
	 * 判断当前的操作系统
	 * 
	 * @return
	 */
	public static boolean isOSLinux() {
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("linux") > -1) {
			return true;
		} else {
			return false;
		}
	}

}
