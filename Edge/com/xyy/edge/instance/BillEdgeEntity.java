package com.xyy.edge.instance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xyy.bill.def.BillDef;
import com.xyy.bill.def.DicDef;
import com.xyy.bill.instance.BillContext;
import com.xyy.bill.meta.DataSetMeta;
import com.xyy.bill.meta.DataTableMeta;
import com.xyy.bill.meta.DataTableMeta.TableType;

/**
 * 单据转换实体
 * 
 * @author evan
 *
 */
public class BillEdgeEntity {
	private String key;
	private BillDef billDef;
	private DicDef dicDef;
	private BillContext context;
	private String billID;
	private BillHeadEdgeEntity billEdgeHeadEntity;
	private Map<String, BillDtlEdgeEntity> billEdgeBodyEntities = new HashMap<>();
	private DataSetMeta dataSetMeta;

	/**
	 * 加载BillEdgeEntity
	 * 
	 * @param billDef
	 * @param context
	 * @param billID
	 * @throws BillHeadNullException
	 * @throws BillLoadEntityException
	 */
	public BillEdgeEntity(BillDef billDef, BillContext context, String billID)
			throws BillHeadNullException, BillLoadEntityException {
		this();
		this.billDef = billDef;
		this.context = context;
		this.billID = billID;
		this.init();
		this.loadEntity();
	}

	/**
	 * 加载实体关联的数据
	 * 
	 * @throws BillLoadEntityException
	 */
	private void loadEntity() throws BillLoadEntityException {
		try {
			if (this.billEdgeHeadEntity == null) {
				throw new BillLoadEntityException("billEdgeHeadEntity is null.");
			}
			this.billEdgeHeadEntity.loadEntity();
			for (BillDtlEdgeEntity bodyEntity : this.billEdgeBodyEntities.values()) {
				bodyEntity.loadEntity();
			}
		} catch (Exception e) {
			throw new BillLoadEntityException(e);
		}
	}

	/**
	 * 创建新的BillEdgeEntity
	 * 
	 * @param billDef
	 * @param context
	 * @throws BillHeadNullException
	 */
	public BillEdgeEntity(BillDef billDef, BillContext context) throws BillInitEntityException, BillHeadNullException {
		this();
		this.billDef = billDef;
		this.context = context;
		this.init();
		this.initEntity();
	}
	
	/**
	 * 创建新的BillEdgeEntity
	 * 
	 * @param billDef
	 * @param context
	 * @throws BillHeadNullException
	 */
	public BillEdgeEntity(DicDef dicDef, BillContext context) throws BillInitEntityException, BillHeadNullException {
		this();
		this.dicDef = dicDef;
		this.context = context;
		this.initDic();
		this.initEntity();
	}

	private void initEntity() throws BillInitEntityException {
		try {
			if (this.billEdgeHeadEntity == null) {
				throw new BillLoadEntityException("billEdgeHeadEntity is null.");
			}
			this.billEdgeHeadEntity.initEntity();
			for (BillDtlEdgeEntity bodyEntity : this.billEdgeBodyEntities.values()) {
				bodyEntity.initEntity();
			}
		} catch (Exception e) {
			throw new BillInitEntityException(e);
		}
		
	}

	/**
	 * 初始化实体： （1）构建billEdgeHeadEntity （2）构建billEdgeBodyEntities
	 * 注意：仅对Table::协议构架单据实体
	 * 
	 * @throws BillHeadNullException
	 */
	private void init() throws BillHeadNullException {
		this.key = this.billDef.getKey();
		this.dataSetMeta = this.billDef.getDataSet();
		DataTableMeta headTable = this.dataSetMeta.getHeadDataTable();
		if (headTable == null || headTable.getTableType() != TableType.Table) {
			throw new BillHeadNullException();
		}
		// 单据头
		this.billEdgeHeadEntity = new BillHeadEdgeEntity(context, this, headTable);
		List<DataTableMeta> bodyTables = this.dataSetMeta.getEntityBodyDataTables();
		// 构建单据体
		for (DataTableMeta table : bodyTables) {
			if (table.getTableType() == TableType.Table) {
				BillDtlEdgeEntity bodyEntity = new BillDtlEdgeEntity(context, this, table);
				this.billEdgeBodyEntities.put(bodyEntity.getKey(), bodyEntity);
			}
		}
	}
	
	/**
	 * 初始化实体： （1）构建billEdgeHeadEntity （2）构建billEdgeBodyEntities
	 * 注意：仅对Table::协议构架单据实体
	 * 
	 * @throws BillHeadNullException
	 */
	private void initDic() throws BillHeadNullException {
		this.key = this.dicDef.getKey();
		this.dataSetMeta = this.dicDef.getDataSet();
		DataTableMeta headTable = this.dataSetMeta.getHeadDataTable();
		if (headTable == null || headTable.getTableType() != TableType.Table) {
			throw new BillHeadNullException();
		}
		// 单据头
		this.billEdgeHeadEntity = new BillHeadEdgeEntity(context, this, headTable);
		List<DataTableMeta> bodyTables = this.dataSetMeta.getEntityBodyDataTables();
		// 构建单据体
		for (DataTableMeta table : bodyTables) {
			if (table.getTableType() == TableType.Table) {
				BillDtlEdgeEntity bodyEntity = new BillDtlEdgeEntity(context, this, table);
				this.billEdgeBodyEntities.put(bodyEntity.getKey(), bodyEntity);
			}
		}
	}

	private BillEdgeEntity() {
		super();
	}

	public BillDef getBillDef() {
		return billDef;
	}

	public void setBillDef(BillDef billDef) {
		this.billDef = billDef;
	}

	public BillContext getContext() {
		return context;
	}

	public void setContext(BillContext context) {
		this.context = context;
	}

	public String getBillID() {
		return billID;
	}

	public BillHeadEdgeEntity getBillEdgeHeadEntity() {
		return billEdgeHeadEntity;
	}

	public Map<String, BillDtlEdgeEntity> getBillEdgeBodyEntities() {
		return billEdgeBodyEntities;
	}

	public String getKey() {
		return key;
	}

	public DicDef getDicDef() {
		return dicDef;
	}

	public void setDicDef(DicDef dicDef) {
		this.dicDef = dicDef;
	}

	/**
	 * 复制billEdgeEntity的所有明细到当前单据体中
	 * 
	 * @param billEdgeEntity
	 */
	public void copyAllBillDtl(BillEdgeEntity billEdgeEntity) {
		for (String key : this.billEdgeBodyEntities.keySet()) {
			BillDtlEdgeEntity billDtlEdgeEntity = billEdgeEntity.getBillEdgeBodyEntities().get(key);
			if (billDtlEdgeEntity != null) {
				this.billEdgeBodyEntities.get(key).getBillDtlItemEntities()
						.addAll(billDtlEdgeEntity.getBillDtlItemEntities());
			}

		}
	}

	
	/**
	 * 加载单据指定明细数据
	 * @param context2
	 * @param sourceDtlKey
	 * @param ids
	 * @throws BillDtlLoadException 
	 * @throws LoadEntityException 
	 */
	public void loadDataForBillDtl(BillContext context, String billDtlKey, List<String> billDtlIDs) throws BillDtlLoadException, LoadEntityException {
		if(billDtlIDs==null || billDtlIDs.size()<=0){
			throw new BillDtlLoadException("bill details:"+billDtlKey+" is null or empty.");
		}
		BillDtlEdgeEntity billDtlEdgeEntity=this.getBillEdgeBodyEntities().get(billDtlKey);
		if(billDtlEdgeEntity==null){
			throw new BillDtlLoadException("billDtlEdgeEntity is null.");
		}
		
		//加载明细
		billDtlEdgeEntity.loadEntity(billDtlIDs);
		
		
	}

	
	/**
	 * 
	 * @param context
	 * 			
	 * @param billEdgeInstance
	 * 			
	 */
	public void saveRelative(BillContext context, BillEdgeInstance billEdgeInstance) {
		if(this.getBillEdgeHeadEntity().saveRelative(context,billEdgeInstance)){
			for(BillDtlEdgeEntity bdee:this.getBillEdgeBodyEntities().values()){
				bdee.saveRelative(context,billEdgeInstance);
			}
		}
	}



}
